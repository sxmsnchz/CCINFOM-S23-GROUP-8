package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BranchListPanel extends JPanel {

    private final MainFrame mainFrame;

    public BranchListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 251));

        // TOP BAR (Back + Search + Filter)
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // SCROLL CONTENT
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        // Initial load
        loadBranchData(content, "", "All");
    }

    // ==========================================
    // TOP BAR: BACK BUTTON + SEARCH + FILTER
    // ==========================================
    private JPanel createTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(new Color(243, 246, 251));

        // BACK BUTTON
        JButton backBtn = new JButton("Back");
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> mainFrame.showOfficerMenu());
        top.add(backBtn);

        // SEARCH BAR
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 28));
        top.add(searchField);

        // REGION FILTER
        String[] regions = {
                "All",
                "NCR",
                "CAR",
                "Region I",
                "Region II",
                "Region III",
                "Region IV-A",
                "MIMAROPA",
                "Region V",
                "Region VI",
                "Region VII",
                "Region VIII",
                "Region IX",
                "Region X",
                "Region XI",
                "Region XII",
                "Region XIII",
                "BARMM"
            };

        JComboBox<String> regionFilter = new JComboBox<>(regions);
        regionFilter.setPreferredSize(new Dimension(130, 28));
        top.add(regionFilter);

        // Search Button
        JButton searchBtn = new JButton("Search");
        stylePrimaryButton(searchBtn);
        top.add(searchBtn);

        // Reload the panel on click
        searchBtn.addActionListener(e ->
                refreshList(searchField.getText().trim(), (String) regionFilter.getSelectedItem())
        );

        return top;
    }

    private void refreshList(String keyword, String region) {
        remove(1); // remove scroll pane

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        loadBranchData(content, keyword, region);

        revalidate();
        repaint();
    }

    // ==========================================
    // LOAD BRANCH DATA + FILTERS
    // ==========================================
    private void loadBranchData(JPanel content, String keyword, String regionFilter) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            StringBuilder sql = new StringBuilder("SELECT * FROM branch WHERE 1=1");

            // SEARCH KEYWORD
            if (!keyword.isEmpty()) {
                sql.append(" AND (branch_name LIKE ? OR city LIKE ?)");
            }

            // REGION FILTER
            if (!regionFilter.equals("All")) {
                sql.append(" AND region = ?");
            }

            sql.append(" ORDER BY branch_id");

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int idx = 1;
            if (!keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (!regionFilter.equals("All")) {
                ps.setString(idx++, regionFilter);
            }

            ResultSet rs = ps.executeQuery();

            boolean hasBranches = false;

            while (rs.next()) {
                hasBranches = true;
                JPanel branchCard = createBranchCard(rs, conn);
                content.add(branchCard);
                content.add(Box.createVerticalStrut(16));
            }

            if (!hasBranches) {
                JLabel noData = new JLabel("No branches match your filters.");
                noData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noData.setForeground(Color.DARK_GRAY);
                noData.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(noData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load branches:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // BRANCH CARD with collapsible sections
    // ==========================================
    private JPanel createBranchCard(ResultSet rs, Connection conn) throws Exception {

        JPanel card = new RoundedPanel(18, new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        int branchId = rs.getInt("branch_id");
        String name = rs.getString("branch_name");

        // OPTION B FORMAT — FIXED
        String address =
                rs.getString("street") + ", " +
                rs.getString("barangay") + ", " +
                rs.getString("city") + ", " +
                rs.getString("postal_code") + ", " +
                rs.getString("province");

        String region = rs.getString("region");
        String contact = rs.getString("contact_number");
        if (contact == null || contact.isEmpty()) contact = "N/A";

        JLabel title = new JLabel(name);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(20, 50, 100));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("Branch ID: " + branchId);
        JLabel addrLabel = new JLabel("Address: " + address);
        JLabel regLabel = new JLabel("Region: " + region);
        JLabel contactLabel = new JLabel("Contact No: " + contact);

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 13);
        idLabel.setFont(infoFont);
        addrLabel.setFont(infoFont);
        regLabel.setFont(infoFont);
        contactLabel.setFont(infoFont);

        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addrLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        regLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(idLabel);
        card.add(addrLabel);
        card.add(regLabel);
        card.add(contactLabel);
        card.add(Box.createVerticalStrut(12));

        // == Officers ==
        JPanel officersContent = new JPanel();
        officersContent.setLayout(new BoxLayout(officersContent, BoxLayout.Y_AXIS));
        officersContent.setOpaque(false);
        officersContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadOfficers(officersContent, conn, branchId);

        card.add(createCollapsible("Assigned Officers", officersContent));

        // == Registrations ==
        JPanel regContent = new JPanel();
        regContent.setLayout(new BoxLayout(regContent, BoxLayout.Y_AXIS));
        regContent.setOpaque(false);
        regContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadRegistrations(regContent, conn, branchId);

        card.add(Box.createVerticalStrut(6));
        card.add(createCollapsible("Registrations Processed", regContent));

        return card;
    }

    private void loadOfficers(JPanel panel, Connection conn, int branchId) {
        try {
            String sql = """
                    SELECT first_name, last_name
                    FROM officer
                    WHERE branch_id = ?
                    ORDER BY last_name ASC
                    """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();

            boolean has = false;

            while (rs.next()) {
                has = true;
                JLabel l = new JLabel("• " + rs.getString("last_name") + ", " + rs.getString("first_name"));
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(l);
            }

            if (!has) {
                JLabel l = new JLabel("No officers assigned.");
                l.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(l);
            }

        } catch (Exception e) {
            panel.add(new JLabel("Error loading officers."));
        }
    }

    private void loadRegistrations(JPanel panel, Connection conn, int branchId) {
        try {
            String sql = """
                    SELECT r.registration_id, v.plate_number, r.status
                    FROM registration r
                    JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                    WHERE r.branch_id = ?
                    ORDER BY r.registration_id ASC
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();

            boolean has = false;

            while (rs.next()) {
                has = true;
                JLabel l = new JLabel("• Reg ID " + rs.getInt("registration_id") +
                        " | Plate " + rs.getString("plate_number") +
                        " | Status: " + rs.getString("status"));
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(l);
            }

            if (!has) {
                JLabel l = new JLabel("No registrations recorded.");
                l.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(l);
            }

        } catch (Exception e) {
            panel.add(new JLabel("Error loading registrations."));
        }
    }

    // COLLAPSIBLE (no symbols!)
    private JPanel createCollapsible(String title, JPanel contentPanel) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton toggle = new JButton(title);
        toggle.setFocusPainted(false);
        toggle.setBackground(new Color(230, 230, 230));
        toggle.setForeground(Color.BLACK);
        toggle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toggle.setHorizontalAlignment(SwingConstants.LEFT);

        contentPanel.setVisible(false);

        toggle.addActionListener(e -> {
            boolean show = !contentPanel.isVisible();
            contentPanel.setVisible(show);
            revalidate();
            repaint();
        });

        wrapper.add(toggle, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        return wrapper;
    }

    // BUTTON STYLE HELPERS
    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(0, 85, 180));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // ROUNDED PANEL
    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
