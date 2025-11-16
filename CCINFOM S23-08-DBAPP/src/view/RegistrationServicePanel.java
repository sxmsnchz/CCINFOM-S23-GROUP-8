package view;

import model.Branch;
import model.Session;
import service.BranchDetailsService;
import service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class RegistrationServicePanel extends JPanel {

    private final MainFrame mainFrame;
    private JTextField plateField, makeField, seriesField, yearField, mvFileField, chassisField, engineField, colorField;
    private JComboBox<Branch> branchCombo;
    private RegistrationService registrationService;

    public RegistrationServicePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.registrationService = new RegistrationService();

        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 92, 175));
        header.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("Vehicle Registration", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setLayout(new BorderLayout());
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        plateField = addLabeledField(form, gbc, y++, "Plate Number:");
        makeField = addLabeledField(form, gbc, y++, "Make:");
        seriesField = addLabeledField(form, gbc, y++, "Series:");
        yearField = addLabeledField(form, gbc, y++, "Manufacture Year (YYYY):");
        mvFileField = addLabeledField(form, gbc, y++, "MV File No:");
        chassisField = addLabeledField(form, gbc, y++, "Chassis No:");
        engineField = addLabeledField(form, gbc, y++, "Engine No:");
        colorField = addLabeledField(form, gbc, y++, "Color:");

        // Branch combo
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        form.add(new JLabel("Branch:"), gbc);
        branchCombo = new JComboBox<>();
        branchCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Branch) setText(((Branch) value).getBranchName());
                return this;
            }
        });
        gbc.gridx = 1; gbc.gridy = y++; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(branchCombo, gbc);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        buttons.add(backBtn);
        buttons.add(submitBtn);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 3; gbc.weightx = 1.0;
        form.add(buttons, gbc);

        add(new JScrollPane(form), BorderLayout.CENTER);

        // Load branches
        loadBranches();
        yearField.setText(String.valueOf(LocalDate.now().getYear()));

        // Actions
        backBtn.addActionListener((ActionEvent e) -> mainFrame.showUserMenu());

        submitBtn.addActionListener((ActionEvent e) -> {
            try {
                String plate = plateField.getText().trim().toUpperCase();
                String make = makeField.getText().trim();
                String series = seriesField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                long mvFile = Long.parseLong(mvFileField.getText().trim());
                String chassis = chassisField.getText().trim();
                String engine = engineField.getText().trim();
                String color = colorField.getText().trim();

                Branch selectedBranch = (Branch) branchCombo.getSelectedItem();
                if (selectedBranch == null) {
                    JOptionPane.showMessageDialog(this, "Please select a branch.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int branchId = selectedBranch.getBranchId();

                int ownerId = Session.loggedInOwnerId;
                if (ownerId <= 0) {
                    JOptionPane.showMessageDialog(this, "No logged-in owner found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Date manufactureDate = Date.valueOf(LocalDate.of(year, 1, 1));

                int vehicleId = registrationService.addVehicle(plate, manufactureDate, mvFile, chassis, engine, make, series, color);
                if (vehicleId == 0) {
                    JOptionPane.showMessageDialog(this, "Failed to insert vehicle. Possibly duplicate plate.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer officerId = registrationService.getOfficerIdForBranch(branchId);
                if (officerId == null) {
                    int res = JOptionPane.showConfirmDialog(this, "No officer assigned to selected branch. Proceed with NULL officer?","No Officer", JOptionPane.YES_NO_OPTION);
                    if (res != JOptionPane.YES_OPTION) return;
                }

                int registrationId = registrationService.addRegistration(vehicleId, ownerId, branchId, officerId);
                if (registrationId == 0) {
                    JOptionPane.showMessageDialog(this, "Failed to create registration.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String info = "Registration created (ID: " + registrationId + ")";
                if (officerId != null) info += "\nAssigned Officer ID: " + officerId;

                JOptionPane.showMessageDialog(this, info, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for year and MV file number.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadBranches() {
        BranchDetailsService bds = new BranchDetailsService();
        List<Branch> list = bds.getAllBranches();
        DefaultComboBoxModel<Branch> model = new DefaultComboBoxModel<>();
        for (Branch b : list) model.addElement(b);
        branchCombo.setModel(model);
    }

    private JTextField addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        JTextField tf = new JTextField();
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(tf, gbc);
        return tf;
    }

    private void clearForm() {
        plateField.setText("");
        makeField.setText("");
        seriesField.setText("");
        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        mvFileField.setText("");
        chassisField.setText("");
        engineField.setText("");
        colorField.setText("");
        if (branchCombo.getItemCount() > 0) branchCombo.setSelectedIndex(0);
    }
}
