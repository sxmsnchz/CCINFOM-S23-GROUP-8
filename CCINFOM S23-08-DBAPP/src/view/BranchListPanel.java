package view;

import service.BranchDetailsService;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class BranchListPanel extends JPanel {

    private final MainFrame mainFrame;

    public BranchListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ======= Title Bar =======
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);

        JLabel title = new JLabel("LTO Branch Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 50, 100));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.addActionListener(e -> mainFrame.showOfficerMenu());

        topBar.add(backBtn);
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // ======= CONTENT SCROLL AREA =======
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Load data from DB
        loadBranches(content);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }


    // =======================================================
    // LOAD ALL BRANCHES + OFFICERS + REGISTRATIONS
    // =======================================================
    private void loadBranches(JPanel container) {
        try {
            BranchDetailsService service = new BranchDetailsService();
            Connection conn = database.DatabaseConnection.getConnection();

            String branchQuery = "SELECT * FROM branch ORDER BY branch_id";
            PreparedStatement ps = conn.prepareStatement(branchQuery);
            ResultSet rs = ps.executeQuery();

            boolean hasBranches = false;

            while (rs.next()) {
                hasBranches = true;

                int branchId = rs.getInt("branch_id");
                String branchName = rs.getString("branch_name");
                String address = rs.getString("street") + ", " +
                                 rs.getString("barangay") + ", " +
                                 rs.getString("city") + ", " +
                                 rs.getString("province") + " (" +
                                 rs.getString("postal_code") + ")";
                String region = rs.getString("region");
                String contact = rs.getString("contact_number");

                // === Create Branch Card ===
                JPanel card = createBranchCard(
                        branchId, branchName, address, region, contact, conn
                );

                container.add(card);
                container.add(Box.createVerticalStrut(15));
            }

            if (!hasBranches) {
                JLabel none = new JLabel("No branches found in the database.");
                none.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                none.setForeground(Color.GRAY);
                container.add(none);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =======================================================
    // CREATE INDIVIDUAL BRANCH CARD
    // =======================================================
    private JPanel createBranchCard(int branchId, String name, String address,
                                    String region, String contact, Connection conn) {

        JPanel card = new RoundedPanel(20, new Color(255, 255, 255, 230));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel branchLabel = new JLabel(name + " (ID: " + branchId + ")");
        branchLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        branchLabel.setForeground(new Color(20, 50, 100));

        JLabel addressLabel = new JLabel("Address: " + address);
        JLabel regionLabel = new JLabel("Region: " + region);
        JLabel contactLabel = new JLabel("Contact: " + (contact != null ? contact : "N/A"));

        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(branchLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(addressLabel);
        card.add(regionLabel);
        card.add(contactLabel);
        card.add(Box.createVerticalStrut(10));

        // OFFICERS
        card.add(new JLabel("Officers Assigned:"));
        card.add(loadOfficers(branchId, conn));

        card.add(Box.createVerticalStrut(10));

        // REGISTRATIONS
        card.add(new JLabel("Registrations Processed:"));
        card.add(loadRegistrations(branchId, conn));

        return card;
    }


    // =======================================================
    // LOAD OFFICERS IN BRANCH
    // =======================================================
    private JPanel loadOfficers(int branchId, Connection conn) {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        try {
            String query = """
                SELECT first_name, last_name
                FROM officer
                WHERE branch_id = ?
                ORDER BY last_name ASC
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();

            boolean has = false;

            while (rs.next()) {
                has = true;
                String n = rs.getString("last_name") + ", " + rs.getString("first_name");
                JLabel L = new JLabel(" • " + n);
                L.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                list.add(L);
            }

            if (!has) {
                JLabel none = new JLabel(" • No officers recorded.");
                none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                none.setForeground(Color.GRAY);
                list.add(none);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // =======================================================
    // LOAD REGISTRATIONS IN BRANCH
    // =======================================================
    private JPanel loadRegistrations(int branchId, Connection conn) {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        try {
            String query = """
                SELECT r.registration_id, v.plate_number, r.status
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                WHERE r.branch_id = ?
                AND r.status IN ('ACTIVE', 'EXPIRED')
                ORDER BY r.registration_id
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();

            boolean has = false;

            while (rs.next()) {
                has = true;
                int regId = rs.getInt("registration_id");
                String plate = rs.getString("plate_number");
                String status = rs.getString("status");

                JLabel L = new JLabel(" • Reg #" + regId + " | Plate: " + plate + " | " + status);
                L.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                list.add(L);
            }

            if (!has) {
                JLabel none = new JLabel(" • No active/expired registrations.");
                none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                none.setForeground(Color.GRAY);
                list.add(none);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



    // =======================================================
    // ROUNDED PANEL
    // =======================================================
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
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
