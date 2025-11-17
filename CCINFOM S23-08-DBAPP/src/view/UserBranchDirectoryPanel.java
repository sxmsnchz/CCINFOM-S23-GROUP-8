package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserBranchDirectoryPanel extends JPanel {

    private final MainFrame mainFrame;

    public UserBranchDirectoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 251)); // soft background

        // ====== TOP BAR (Back + Search + Region Filter + Search Button) ======
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        top.setBackground(new Color(243, 246, 251));

        // Back Button
        JButton backBtn = new JButton("← Back");
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> mainFrame.showUserMenu());
        top.add(backBtn);

        // Search Field
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 28));
        top.add(searchField);

        // Region Filter
        String[] regions = {
                "All", "NCR", "Region I", "Region II", "Region III", "Region IV-A",
                "Region IV-B", "Region V", "Region VI", "Region VII", "Region VIII",
                "Region IX", "Region X", "Region XI", "Region XII", "CAR", "CARAGA", "BARMM"
        };
        JComboBox<String> regionFilter = new JComboBox<>(regions);
        regionFilter.setPreferredSize(new Dimension(140, 28));
        top.add(regionFilter);

        // Search Button
        JButton searchBtn = new JButton("Search");
        stylePrimaryButton(searchBtn);
        top.add(searchBtn);

        add(top, BorderLayout.NORTH);

        // ====== CONTENT SCROLL AREA ======
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        // Load initial
        loadBranchData(content, "", "All");

        // Search Action
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            String region = regionFilter.getSelectedItem().toString();
            refresh(content, keyword, region);
        });
    }


    // Refresh results without recreating the whole panel
    private void refresh(JPanel content, String keyword, String region) {
        content.removeAll();
        loadBranchData(content, keyword, region);
        content.revalidate();
        content.repaint();
    }


    // ======== LOAD BRANCH DATA ========
    private void loadBranchData(JPanel content, String keyword, String regionFilter) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM branch
                WHERE 1=1
                """);

            if (!keyword.isEmpty()) {
                sql.append(" AND (branch_name LIKE ? OR city LIKE ? OR province LIKE ?)");
            }

            if (!regionFilter.equals("All")) {
                sql.append(" AND region = ?");
            }

            sql.append(" ORDER BY branch_name ASC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int idx = 1;
            if (!keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (!regionFilter.equals("All")) {
                ps.setString(idx++, regionFilter);
            }

            ResultSet rs = ps.executeQuery();
            boolean found = false;

            while (rs.next()) {
                found = true;
                JPanel card = createBranchCard(rs);
                content.add(card);
                content.add(Box.createVerticalStrut(16));
            }

            if (!found) {
                JLabel empty = new JLabel("No branches match your search.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                empty.setForeground(Color.DARK_GRAY);
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(Box.createVerticalStrut(20));
                content.add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ======== CREATE BRANCH CARD (User Version) ========
    private JPanel createBranchCard(ResultSet rs) throws Exception {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        int branchId = rs.getInt("branch_id");
        String name = rs.getString("branch_name");

        String fullAddress = rs.getString("street") + ", "
                + rs.getString("barangay") + ", "
                + rs.getString("city") + ", "
                + rs.getString("province") + ", "
                + rs.getString("postal_code") + ", "
                + rs.getString("region");

        String contact = rs.getString("contact_number");
        if (contact == null || contact.isBlank()) contact = "N/A";

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(20, 50, 100));

        JLabel idLabel = new JLabel("Branch ID: " + branchId);
        JLabel addrLabel = new JLabel("Address: " + fullAddress);
        JLabel contactLabel = new JLabel("Contact: " + contact);

        Font f = new Font("Segoe UI", Font.PLAIN, 13);
        idLabel.setFont(f);
        addrLabel.setFont(f);
        contactLabel.setFont(f);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(idLabel);
        card.add(addrLabel);
        card.add(contactLabel);

        return card;
    }


    // ======== BUTTON STYLE HELPERS ========
    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(0, 85, 180));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}
