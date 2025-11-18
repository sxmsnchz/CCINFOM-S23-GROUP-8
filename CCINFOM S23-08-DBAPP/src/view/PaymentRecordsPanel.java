package view;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class PaymentRecordsPanel extends JPanel {

    private final MainFrame mainFrame;
    private JTable table;
    private JComboBox<String> typeBox;
    private JComboBox<String> branchBox;
    private JComboBox<String> orderBox;
    private DefaultTableModel model;
    private Image bgImage;

    public PaymentRecordsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        bgImage = new ImageIcon(getClass().getResource("/assets/pic.png")).getImage();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // TOP BAR
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topBar.setOpaque(false);

        JButton backBtn = new JButton("← Back");
        styleSecondary(backBtn);
        backBtn.addActionListener(e -> mainFrame.showOfficerMenu());

        JLabel title = new JLabel("Payment Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(20, 50, 100));

        topBar.add(backBtn);
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // CENTER WRAPPER
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);

        // PAGE CARD
        JPanel pageCard = new JPanel();
        pageCard.setBackground(Color.WHITE);
        pageCard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        pageCard.setPreferredSize(new Dimension(900, 600));
        pageCard.setLayout(new BoxLayout(pageCard, BoxLayout.Y_AXIS));

        center.add(pageCard);

        // FILTER BAR
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        filterPanel.setOpaque(false);

        typeBox = new JComboBox<>(new String[]{
                "All Types", "Violation", "Registration", "Renewal"
        });

        branchBox = new JComboBox<>();
        branchBox.addItem("All Branches");
        loadBranches();

        orderBox = new JComboBox<>(new String[]{
                "Payment ID ↑ (Oldest first)",
                "Payment ID ↓ (Newest first)",
                "Amount ↑",
                "Amount ↓",
                "Date Paid ↑ (Oldest first)",
                "Date Paid ↓ (Newest first)",
                "Owner A → Z",
                "Owner Z → A",
                "Officer A → Z",
                "Officer Z → A"
        });

        JButton filterBtn = new JButton("Apply Filter");
        stylePrimary(filterBtn);
        filterBtn.addActionListener(e -> reloadTable());

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeBox);
        filterPanel.add(new JLabel("Branch:"));
        filterPanel.add(branchBox);
        filterPanel.add(new JLabel("Order:"));
        filterPanel.add(orderBox);
        filterPanel.add(filterBtn);

        pageCard.add(filterPanel);

        // TABLE 
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Payment ID", "Owner", "Type", "Amount", "Branch", "Officer", "Date Paid"
        });

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false; // read-only table
            }
        };

        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(850, 450));
        pageCard.add(sp);

        reloadTableDefault();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
    }

    private void loadBranches() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT branch_name FROM branch ORDER BY branch_id"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) branchBox.addItem(rs.getString("branch_name"));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void reloadTableDefault() {
        orderBox.setSelectedItem("Payment ID ↑ (Oldest first)");
        reloadTable();
    }

    public void reloadTable() {

        model.setRowCount(0);

        String typeFilter = typeBox.getSelectedItem().toString();
        String branchFilter = branchBox.getSelectedItem().toString();
        String orderFilter = orderBox.getSelectedItem().toString();

        StringBuilder query = new StringBuilder("""
                SELECT 
                    p.payment_id,
                    CONCAT(o.last_name, ', ', o.first_name) AS owner_name,
                    p.payment_type,
                    p.amount_paid,
                    b.branch_name,
                    CONCAT(f.last_name, ', ', f.first_name) AS officer_name,
                    p.date_paid
                FROM payment p
                JOIN owner o ON o.owner_id = p.owner_id
                JOIN branch b ON b.branch_id = p.branch_id
                JOIN officer f ON f.officer_id = p.officer_id
                WHERE 1=1
                """);

        if (!typeFilter.equals("All Types"))
            query.append(" AND p.payment_type = '").append(typeFilter).append("' ");

        if (!branchFilter.equals("All Branches"))
            query.append(" AND b.branch_name = '").append(branchFilter).append("' ");

        switch (orderFilter) {
            case "Payment ID ↑ (Oldest first)" -> query.append(" ORDER BY p.payment_id ASC");
            case "Payment ID ↓ (Newest first)" -> query.append(" ORDER BY p.payment_id DESC");
            case "Amount ↑" -> query.append(" ORDER BY p.amount_paid ASC");
            case "Amount ↓" -> query.append(" ORDER BY p.amount_paid DESC");
            case "Date Paid ↑ (Oldest first)" -> query.append(" ORDER BY p.date_paid ASC");
            case "Date Paid ↓ (Newest first)" -> query.append(" ORDER BY p.date_paid DESC");

            case "Owner A → Z" ->
                    query.append(" ORDER BY o.last_name ASC, o.first_name ASC");

            case "Owner Z → A" ->
                    query.append(" ORDER BY o.last_name DESC, o.first_name DESC");

            case "Officer A → Z" ->
                    query.append(" ORDER BY f.last_name ASC, f.first_name ASC");

            case "Officer Z → A" ->
                    query.append(" ORDER BY f.last_name DESC, f.first_name DESC");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("payment_id"));
                row.add(rs.getString("owner_name"));
                row.add(rs.getString("payment_type"));
                row.add("₱" + rs.getDouble("amount_paid"));
                row.add(rs.getString("branch_name"));
                row.add(rs.getString("officer_name"));
                row.add(rs.getDate("date_paid"));

                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        updateHeaderSortIcons(orderFilter);
    }

    private void updateHeaderSortIcons(String order) {

        String[] cols = {"Payment ID", "Owner", "Type", "Amount", "Branch", "Officer", "Date Paid"};

        String up = " ▲";
        String down = " ▼";

        // reset headers
        for (int i = 0; i < cols.length; i++)
            table.getColumnModel().getColumn(i).setHeaderValue(cols[i]);

        switch (order) {
            case "Payment ID ↑ (Oldest first)" ->
                    setHeader(0, cols[0] + up);
            case "Payment ID ↓ (Newest first)" ->
                    setHeader(0, cols[0] + down);
            case "Amount ↑" ->
                    setHeader(3, cols[3] + up);
            case "Amount ↓" ->
                    setHeader(3, cols[3] + down);
            case "Date Paid ↑ (Oldest first)" ->
                    setHeader(6, cols[6] + up);
            case "Date Paid ↓ (Newest first)" ->
                    setHeader(6, cols[6] + down);

            case "Owner A → Z" ->
                    setHeader(1, cols[1] + up);
            case "Owner Z → A" ->
                    setHeader(1, cols[1] + down);

            case "Officer A → Z" ->
                    setHeader(5, cols[5] + up);
            case "Officer Z → A" ->
                    setHeader(5, cols[5] + down);
        }

        table.getTableHeader().repaint();
    }

    private void setHeader(int column, String value) {
        table.getColumnModel().getColumn(column).setHeaderValue(value);
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}
