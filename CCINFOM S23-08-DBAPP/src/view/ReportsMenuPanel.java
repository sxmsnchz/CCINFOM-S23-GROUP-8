package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsMenuPanel extends JPanel {

    private final MainFrame mainFrame;

    // area inside the white card that we swap between menu and reports
    private final JPanel contentPanel;

    // used in Registrations-by-Branch report
    private JComboBox<String> regionCombo;
    private JTextField searchField;

    public ReportsMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 251));

        // ===== Top header =====
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(17, 54, 102));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose a report or view summarized results");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(120, 120, 130));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(4));

        add(header, BorderLayout.NORTH);

        // ===== Rounded white card =====
        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setBackground(Color.WHITE);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // initial view: menu
        contentPanel.add(buildMenuView(), BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    // ============================================================
    // MENU VIEW
    // ============================================================
    private JPanel buildMenuView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(10));

        panel.add(createMenuButton("[1] Registrations by Branch",
                e -> showRegistrationsByBranchReport()));
        panel.add(Box.createVerticalStrut(10));

        panel.add(createMenuButton("[2] Renewal On Time",
                e -> showRenewalOnTimeReport()));
        panel.add(Box.createVerticalStrut(10));

        panel.add(createMenuButton("[3] Violations Issued by Officer",
                e -> showViolationsByOfficerReport()));
        panel.add(Box.createVerticalStrut(10));

        panel.add(createMenuButton("[4] Outstanding Violations",
                e -> showOutstandingViolationsReport()));
        panel.add(Box.createVerticalStrut(20));

        JButton backOfficer = createSecondaryButton("Back to Officer Menu");
        backOfficer.addActionListener(e -> mainFrame.showOfficerMenu());
        panel.add(backOfficer);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Dimension d = new Dimension(260, 40);
        b.setPreferredSize(d);
        b.setMaximumSize(d);

        b.addActionListener(action);
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Dimension d = new Dimension(260, 40);
        b.setPreferredSize(d);
        b.setMaximumSize(d);

        return b;
    }

    private void setContent(JComponent comp) {
        contentPanel.removeAll();
        contentPanel.add(comp, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ============================================================
    // 1) REGISTRATIONS BY BRANCH – CARD VIEW WITH FILTERS
    // ============================================================
    private void showRegistrationsByBranchReport() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        // ----- top bar -----
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topBar.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> setContent(buildMenuView()));

        regionCombo = new JComboBox<>(new String[]{
                "All", "NCR", "Region I", "Region II", "Region III",
                "Region IV-A", "Region IV-B", "Region V", "Region VI",
                "Region VII", "Region VIII", "Region IX", "Region X",
                "Region XI", "Region XII", "CAR", "CARAGA", "BARMM"
        });

        searchField = new JTextField(18);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 246, 251));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 90, 200));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadRegistrationsByBranchCards(listPanel));

        topBar.add(backButton);
        topBar.add(regionCombo);
        topBar.add(searchField);
        topBar.add(searchBtn);

        root.add(topBar, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        // initial load
        loadRegistrationsByBranchCards(listPanel);

        setContent(root);
    }

    private void loadRegistrationsByBranchCards(JPanel listPanel) {
        listPanel.removeAll();

        String regionFilter = regionCombo.getSelectedItem().toString();
        String searchText = searchField.getText().trim();

        String sql = """
            SELECT
                b.branch_id,
                b.branch_name,
                CONCAT(
                    b.street, ', ',
                    b.barangay, ', ',
                    b.city, ', ',
                    b.province, ', ',
                    LPAD(b.postal_code, 4, '0'), ', ',
                    b.region
                ) AS full_address,
                b.region,
                b.contact_number,
                COUNT(DISTINCT r.registration_id) AS registrations_count,
                COUNT(DISTINCT o.officer_id)      AS officers_count
            FROM Branch b
            LEFT JOIN Registration r ON r.branch_id = b.branch_id
            LEFT JOIN Officer o       ON o.branch_id = b.branch_id
            WHERE
                (? = 'All' OR b.region = ?)
                AND (
                     ? = ''
                  OR b.branch_name LIKE CONCAT('%', ?, '%')
                  OR b.city        LIKE CONCAT('%', ?, '%')
                  OR b.province    LIKE CONCAT('%', ?, '%')
                )
            GROUP BY
                b.branch_id, b.branch_name, full_address, b.region, b.contact_number
            ORDER BY b.branch_name
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, regionFilter);
            ps.setString(2, regionFilter);
            ps.setString(3, searchText);
            ps.setString(4, searchText);
            ps.setString(5, searchText);
            ps.setString(6, searchText);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasRows = false;

                while (rs.next()) {
                    hasRows = true;
                    JPanel card = createBranchCard(
                            rs.getInt("branch_id"),
                            rs.getString("branch_name"),
                            rs.getString("full_address"),
                            rs.getString("region"),
                            rs.getString("contact_number"),
                            rs.getInt("officers_count"),
                            rs.getInt("registrations_count")
                    );
                    listPanel.add(card);
                    listPanel.add(Box.createVerticalStrut(16));
                }

                if (!hasRows) {
                    listPanel.add(new JLabel("No branches found for this filter."));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            listPanel.add(new JLabel("Error loading data: " + ex.getMessage()));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createBranchCard(int id, String name, String address,
                                    String region, String contact,
                                    int officersCount, int registrationsCount) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(17, 54, 102));

        JLabel idLabel      = new JLabel("Branch ID: " + id);
        JLabel addrLabel    = new JLabel("Address: " + address);
        JLabel regionLabel  = new JLabel("Region: " + region);
        JLabel contactLabel = new JLabel("Contact No: " + contact);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(idLabel);
        card.add(addrLabel);
        card.add(regionLabel);
        card.add(contactLabel);
        card.add(Box.createVerticalStrut(8));

        JButton officersBtn = new JButton("Assigned Officers: " + officersCount);
        JButton regBtn      = new JButton("Registrations Processed: " + registrationsCount);

        for (JButton b : new JButton[]{officersBtn, regBtn}) {
            b.setBackground(new Color(230, 230, 230));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }

        card.add(officersBtn);
        card.add(Box.createVerticalStrut(4));
        card.add(regBtn);

        return card;
    }

    // ============================================================
    // 2) RENEWAL ON TIME – SUMMARY BY BRANCH
    // ============================================================
    private void showRenewalOnTimeReport() {
        JPanel root = createReportRootWithBack();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 246, 251));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        String sql = """
            SELECT
                b.branch_id,
                b.branch_name,
                COUNT(*) AS total_renewals,
                SUM(CASE
                        WHEN r.expiry_date IS NOT NULL
                         AND DATEDIFF(Re.last_renewal_date, r.expiry_date) <= 0
                        THEN 1 ELSE 0
                    END) AS on_time,
                SUM(CASE
                        WHEN r.expiry_date IS NOT NULL
                         AND DATEDIFF(Re.last_renewal_date, r.expiry_date) > 0
                        THEN 1 ELSE 0
                    END) AS late
            FROM Renewal Re
            JOIN Registration r ON Re.registration_id = r.registration_id
            JOIN Branch b       ON Re.branch_id       = b.branch_id
            GROUP BY b.branch_id, b.branch_name
            ORDER BY b.branch_name
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;

                int branchId  = rs.getInt("branch_id");
                String name   = rs.getString("branch_name");
                int total     = rs.getInt("total_renewals");
                int onTime    = rs.getInt("on_time");
                int late      = rs.getInt("late");

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                card.setBackground(Color.WHITE);

                JLabel nameLabel  = new JLabel(name);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                nameLabel.setForeground(new Color(17, 54, 102));

                JLabel idLabel    = new JLabel("Branch ID: " + branchId);
                JLabel totalLabel = new JLabel("Total Renewals: " + total);
                JLabel onLabel    = new JLabel("On-Time Renewals: " + onTime);
                JLabel lateLabel  = new JLabel("Late Renewals: " + late);

                card.add(nameLabel);
                card.add(Box.createVerticalStrut(4));
                card.add(idLabel);
                card.add(totalLabel);
                card.add(onLabel);
                card.add(lateLabel);

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(16));
            }

            if (!hasRows) {
                listPanel.add(new JLabel("No renewal records found."));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            listPanel.add(new JLabel("Error loading renewal report: " + ex.getMessage()));
        }

        setContent(root);
    }

    // ============================================================
    // 3) VIOLATIONS ISSUED BY OFFICER – ONE CARD PER OFFICER
    // ============================================================
    private void showViolationsByOfficerReport() {
        JPanel root = createReportRootWithBack();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 246, 251));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        String sql = """
            SELECT
                o.officer_id,
                CONCAT(o.first_name, ' ', o.last_name) AS officer_name,
                b.branch_name,
                COUNT(v.violation_id)           AS total_violations,
                COALESCE(SUM(v.fine_amount), 0) AS total_fines
            FROM Officer o
            LEFT JOIN Violation v ON v.officer_id = o.officer_id
            LEFT JOIN Branch b    ON o.branch_id  = b.branch_id
            GROUP BY o.officer_id, officer_name, b.branch_name
            ORDER BY total_violations DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;

                int officerId      = rs.getInt("officer_id");
                String officerName = rs.getString("officer_name");
                String branchName  = rs.getString("branch_name");
                int totalViol      = rs.getInt("total_violations");
                double totalFines  = rs.getDouble("total_fines");

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                card.setBackground(Color.WHITE);

                JLabel nameLabel  = new JLabel(officerName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                nameLabel.setForeground(new Color(17, 54, 102));

                JLabel idLabel    = new JLabel("Officer ID: " + officerId);
                JLabel branchLbl  = new JLabel("Branch: " + (branchName != null ? branchName : "N/A"));
                JLabel totalLbl   = new JLabel("Total Violations Issued: " + totalViol);
                JLabel finesLbl   = new JLabel("Total Fines Amount: " + totalFines);

                card.add(nameLabel);
                card.add(Box.createVerticalStrut(4));
                card.add(idLabel);
                card.add(branchLbl);
                card.add(totalLbl);
                card.add(finesLbl);

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(16));
            }

            if (!hasRows) {
                listPanel.add(new JLabel("No violations found for any officer."));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            listPanel.add(new JLabel("Error loading violations-by-officer report: " + ex.getMessage()));
        }

        setContent(root);
    }

    // ============================================================
    // 4) OUTSTANDING VIOLATIONS – ONE CARD PER VIOLATION
    // ============================================================
    private void showOutstandingViolationsReport() {
        JPanel root = createReportRootWithBack();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 246, 251));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        String sql = """
            SELECT
                v.violation_id,
                vh.plate_number,
                CONCAT(o.first_name, ' ', o.last_name) AS owner_name,
                b.branch_name,
                v.violation_type,
                v.violation_date,
                v.fine_amount
            FROM Violation v
            JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id
            JOIN Owner   o ON v.owner_id   = o.owner_id
            JOIN Branch  b ON v.branch_id  = b.branch_id
            WHERE v.payment_status = 'Unpaid'
            ORDER BY v.violation_date DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;

                int violationId  = rs.getInt("violation_id");
                String plate     = rs.getString("plate_number");
                String ownerName = rs.getString("owner_name");
                String branch    = rs.getString("branch_name");
                String type      = rs.getString("violation_type");
                java.sql.Date date = rs.getDate("violation_date");
                double fine      = rs.getDouble("fine_amount");

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                card.setBackground(Color.WHITE);

                JLabel header    = new JLabel("Violation ID: " + violationId);
                header.setFont(new Font("Segoe UI", Font.BOLD, 14));
                header.setForeground(new Color(150, 0, 0));

                JLabel plateLbl  = new JLabel("Plate Number: " + plate);
                JLabel ownerLbl  = new JLabel("Owner: " + ownerName);
                JLabel branchLbl = new JLabel("Branch: " + branch);
                JLabel typeLbl   = new JLabel("Violation Type: " + type);
                JLabel dateLbl   = new JLabel("Violation Date: " + date);
                JLabel fineLbl   = new JLabel("Fine Amount: " + fine);

                card.add(header);
                card.add(Box.createVerticalStrut(4));
                card.add(plateLbl);
                card.add(ownerLbl);
                card.add(branchLbl);
                card.add(typeLbl);
                card.add(dateLbl);
                card.add(fineLbl);

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(16));
            }

            if (!hasRows) {
                listPanel.add(new JLabel("No outstanding (unpaid) violations found."));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            listPanel.add(new JLabel("Error loading outstanding violations report: " + ex.getMessage()));
        }

        setContent(root);
    }

    // ============================================================
    // Helper: create a root panel with "Back to Reports Menu" top bar
    // ============================================================
    private JPanel createReportRootWithBack() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setOpaque(false);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> setContent(buildMenuView()));
        top.add(backBtn);

        root.add(top, BorderLayout.NORTH);
        return root;
    }

    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(4, 6, w - 8, h - 8, radius + 4, radius + 4);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w - 8, h - 10, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}





