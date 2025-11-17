package view;

import model.Violation;
import model.Session;
import service.ViolationService;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RecordViolationPanel extends JPanel {

    private final MainFrame mainFrame;

    private JTextField vehicleIdField;
    private JTextField ownerIdField;
    private JTextField branchIdField;
    private JTextField violationTypeField;
    private JTextField fineAmountField;
    private JTextField dateField; // yyyy-mm-dd

    private JButton saveButton;
    private JButton cancelButton;

    private ViolationService violationService;

    public RecordViolationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.violationService = new ViolationService();

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JPanel card = new RoundedPanel(18, new Color(255, 255, 255, 235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        card.setPreferredSize(new Dimension(480, 420));

        JLabel title = new JLabel("Record New Violation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter violation details below");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(16));

        // Form panel (grid)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints fgc = new GridBagConstraints();
        fgc.insets = new Insets(4, 4, 4, 4);
        fgc.fill = GridBagConstraints.HORIZONTAL;

        vehicleIdField = new JTextField(15);
        ownerIdField = new JTextField(15);
        branchIdField = new JTextField(15);
        violationTypeField = new JTextField(15);
        fineAmountField = new JTextField(15);
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().toString()); // default today

        int row = 0;
        addLabeledField("Vehicle ID:", vehicleIdField, formPanel, fgc, row++);
        addLabeledField("Owner ID:", ownerIdField, formPanel, fgc, row++);
        addLabeledField("Branch ID:", branchIdField, formPanel, fgc, row++);
        addLabeledField("Violation Type:", violationTypeField, formPanel, fgc, row++);
        addLabeledField("Fine Amount:", fineAmountField, formPanel, fgc, row++);
        addLabeledField("Violation Date (YYYY-MM-DD):", dateField, formPanel, fgc, row++);

        card.add(formPanel);
        card.add(Box.createVerticalStrut(16));

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        saveButton = new JButton("Save Violation");
        cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(0, 120, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(180, 40, 40));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        btnPanel.add(saveButton);
        btnPanel.add(cancelButton);

        card.add(btnPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);

        // Actions
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());
    }

    private void addLabeledField(String labelText, JComponent field,
                                 JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void onSave() {
        try {
            int vehicleId = Integer.parseInt(vehicleIdField.getText().trim());
            int ownerId = Integer.parseInt(ownerIdField.getText().trim());
            int branchId = Integer.parseInt(branchIdField.getText().trim());
            String violationType = violationTypeField.getText().trim();
            double fineAmount = Double.parseDouble(fineAmountField.getText().trim());
            String dateStr = dateField.getText().trim();

            if (violationType.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Violation Type is required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate localDate = LocalDate.parse(dateStr); // expects yyyy-MM-dd
            Date sqlDate = Date.valueOf(localDate);

            // Officer who is currently logged in
            int officerId = Session.loggedInOfficerId;

            if (officerId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Officer is not logged in. Cannot record violation.",
                        "Session Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ====== VALIDATION AGAINST DATABASE ======

            if (!vehicleExists(vehicleId)) {
                JOptionPane.showMessageDialog(this,
                        "Vehicle ID " + vehicleId + " does not exist in the database.",
                        "Invalid Vehicle",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!ownerExists(ownerId)) {
                JOptionPane.showMessageDialog(this,
                        "Owner ID " + ownerId + " does not exist in the database.",
                        "Invalid Owner",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!branchExists(branchId)) {
                JOptionPane.showMessageDialog(this,
                        "Branch ID " + branchId + " does not exist in the database.",
                        "Invalid Branch",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int officerBranch = getOfficerBranch(officerId);

            if (officerBranch == -1) {
                JOptionPane.showMessageDialog(this,
                        "You are not assigned to any branch.\n" +
                        "Please contact the system administrator.",
                        "Branch Assignment Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (officerBranch != branchId) {
                JOptionPane.showMessageDialog(this,
                        "You are not assigned to this branch.\n" +
                        "You cannot record violations for other branches.",
                        "Branch Authorization Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            branchId = officerBranch;
            if (!ownerOwnsVehicle(ownerId, vehicleId)) {
                JOptionPane.showMessageDialog(this,
                        "Owner ID " + ownerId + " does not appear to own Vehicle ID " + vehicleId + ".",
                        "Ownership Mismatch",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Violation v = new Violation();
            v.setVehicleId(vehicleId);
            v.setOwnerId(ownerId);
            v.setBranchId(branchId);        
            v.setOfficerId(officerId);
            v.setViolationType(violationType);
            v.setFineAmount(fineAmount);
            v.setViolationDate(sqlDate);

            Violation saved = violationService.addViolationByOfficer(v);

            if (saved != null) {
                JOptionPane.showMessageDialog(this,
                        "Violation recorded successfully! ID: " + saved.getViolationId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                mainFrame.showOfficerMenu();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to record violation.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Check numeric fields (Vehicle ID, Owner ID, Branch ID, Fine Amount).",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        clearForm();
        mainFrame.showOfficerMenu();
    }

    private void clearForm() {
        vehicleIdField.setText("");
        ownerIdField.setText("");
        branchIdField.setText("");
        violationTypeField.setText("");
        fineAmountField.setText("");
        dateField.setText(LocalDate.now().toString());
    }

    // ===================== DB VALIDATION HELPERS =====================

    private boolean vehicleExists(int vehicleId) {
        String sql = "SELECT COUNT(*) FROM Vehicle WHERE vehicle_id = ?";
        return existsSingleId(sql, vehicleId);
    }

    private boolean ownerExists(int ownerId) {
        String sql = "SELECT COUNT(*) FROM Owner WHERE owner_id = ?";
        return existsSingleId(sql, ownerId);
    }

    private boolean branchExists(int branchId) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE branch_id = ?";
        return existsSingleId(sql, branchId);
    }

    private boolean ownerOwnsVehicle(int ownerId, int vehicleId) {
        String sql = "SELECT COUNT(*) FROM Registration WHERE owner_id = ? AND vehicle_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            ps.setInt(2, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error checking ownership: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    /**
     * Get the branch_id where this officer is assigned.
     * Returns:
     *   - branch_id   → if assigned
     *   - -1          → if NULL / not assigned / not found
     */
    private int getOfficerBranch(int officerId) {
        String sql = "SELECT branch_id FROM Officer WHERE officer_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, officerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bid = rs.getInt("branch_id");
                    if (rs.wasNull()) {
                        return -1; // branch_id is NULL
                    }
                    return bid;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return -1; // not found
    }

    private boolean existsSingleId(String sql, int id) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
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
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
