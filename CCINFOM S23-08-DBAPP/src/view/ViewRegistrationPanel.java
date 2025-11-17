package view;

import database.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import model.Session;

public class ViewRegistrationPanel extends JPanel {

    private DefaultListModel<String> masterListModel;
    private JList<String> vehicleList;
    private JPanel detailPanel;
    private MainFrame mainFrame;

    public ViewRegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header with Title and Back Btn =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(10, 102, 194)); // modern blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title in center
        JLabel title = new JLabel("View Vehicle Registrations", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.CENTER);

        // Back button on top-right
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(10, 60, 120));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        backBtn.addActionListener(e -> mainFrame.showUserMenu());
        topPanel.add(backBtn, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER: List + Details =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);

        // Vehicle list (left sidee)
        masterListModel = new DefaultListModel<>();
        vehicleList = new JList<>(masterListModel);
        vehicleList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleList.setSelectionBackground(new Color(0, 102, 194));
        vehicleList.setSelectionForeground(Color.WHITE);
        vehicleList.setBackground(Color.WHITE);
        vehicleList.setFixedCellHeight(60);
        vehicleList.setCellRenderer(new VehicleCardRenderer());
        JScrollPane listScroll = new JScrollPane(vehicleList);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setLeftComponent(listScroll);

        // Detail panel (right side)
        detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(new Color(245, 245, 245));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane detailScroll = new JScrollPane(detailPanel);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setRightComponent(detailScroll);

        add(splitPane, BorderLayout.CENTER);

        // ===== EVENTS =====
        vehicleList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selected = vehicleList.getSelectedValue();
                if (selected != null && !selected.startsWith("No")) {
                    String plate = selected.split("\\s+")[0].trim();
                    if (!plate.isEmpty()) {
                        loadDetails(plate);
                    }
                }
            }
        });
    }

    // ===== CUSTOM RENDERER FOR VEHICLE CARDS =====
    private class VehicleCardRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel textLabel;

        public VehicleCardRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setBackground(Color.WHITE);
            textLabel = new JLabel();
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            add(textLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            textLabel.setText("<html><b>" + value.split("–")[0].trim() + "</b> – " +
                    value.split("–")[1].trim() + "</html>");
            setBackground(isSelected ? new Color(10, 102, 194) : Color.WHITE);
            textLabel.setForeground(isSelected ? Color.WHITE : Color.BLACK);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            return this;
        }
    }

    public void loadVehicleList() {
        masterListModel.clear();
        detailPanel.removeAll();
        detailPanel.revalidate();
        detailPanel.repaint();

        int ownerID = Session.loggedInOwnerId;

        if (ownerID <= 0) {
            masterListModel.addElement("No vehicles found.");
            return;
        }

        String query = """
                SELECT v.plate_number, v.make, v.series, reg.status
                FROM Registration reg
                JOIN Vehicle v ON reg.vehicle_id = v.vehicle_id
                WHERE reg.owner_id = ?
                ORDER BY reg.registration_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, ownerID);
            ResultSet rs = ps.executeQuery();

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                String plate = nullSafe(rs.getString("plate_number"));
                String make = nullSafe(rs.getString("make"));
                String series = nullSafe(rs.getString("series"));
                String status = nullSafe(rs.getString("status"));

                String entry = plate + "   " + make + " " + series + " – " + status;
                masterListModel.addElement(entry);
            }

            if (!hasRows) {
                masterListModel.addElement("No vehicles registered under this account.");
            }

            vehicleList.setModel(masterListModel);

        } catch (SQLException e) {
            masterListModel.addElement("Database error: " + e.getMessage());
        }
    }

    private void loadDetails(String plate) {
        detailPanel.removeAll();
        JLabel loading = new JLabel("Loading details for: " + plate + "...");
        loading.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        detailPanel.add(loading);
        detailPanel.revalidate();
        detailPanel.repaint();

        String query = """
                SELECT reg.registration_id, reg.first_date_registered, reg.current_date_registered, reg.expiry_date,
                       reg.status, v.plate_number, v.make, v.series,
                       o.officer_id, b.branch_name, rct.receipt_number
                FROM Registration reg
                JOIN Vehicle v ON reg.vehicle_id = v.vehicle_id
                JOIN Officer o ON reg.officer_id = o.officer_id
                JOIN Branch b ON reg.branch_id = b.branch_id
                LEFT JOIN Receipt rct ON reg.payment_id = rct.payment_id
                WHERE v.plate_number = ?
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, plate);
            ResultSet rs = ps.executeQuery();

            detailPanel.removeAll();

            if (!rs.next()) {
                JLabel notFound = new JLabel("No details found for plate: " + plate);
                notFound.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                detailPanel.add(notFound);
            } else {
                detailPanel.setLayout(new GridLayout(0, 1, 0, 6)); // vertical list
                addDetail("Plate Number", rs.getString("plate_number"));
                addDetail("Make", rs.getString("make"));
                addDetail("Series", rs.getString("series"));
                addDetail("Registration ID", String.valueOf(rs.getInt("registration_id")));
                addDetail("Status", rs.getString("status"));
                addDetail("Receipt Number", rs.getString("receipt_number") == null ? "Not Paid Yet" : rs.getString("receipt_number"));
                addDetail("First Registered", rs.getDate("first_date_registered") == null ? "N/A" : rs.getDate("first_date_registered").toString());
                addDetail("Current Reg Date", rs.getDate("current_date_registered") == null ? "N/A" : rs.getDate("current_date_registered").toString());
                addDetail("Expiry Date", rs.getDate("expiry_date") == null ? "N/A" : rs.getDate("expiry_date").toString());
                addDetail("Branch Name", rs.getString("branch_name"));
                addDetail("Officer ID", String.valueOf(rs.getInt("officer_id")));
            }

            detailPanel.revalidate();
            detailPanel.repaint();

        } catch (SQLException e) {
            detailPanel.removeAll();
            JLabel error = new JLabel("Database error: " + e.getMessage());
            error.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            detailPanel.add(error);
            detailPanel.revalidate();
            detailPanel.repaint();
        }
    }

    private void addDetail(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lbl = new JLabel(label + ": ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(50, 50, 50));
        row.add(lbl, BorderLayout.WEST);

        JLabel val = new JLabel(value == null ? "N/A" : value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(new Color(30, 30, 30));
        row.add(val, BorderLayout.CENTER);

        detailPanel.add(row);
    }

    private String nullSafe(String s) {
        return (s == null) ? "N/A" : s;
    }
}
