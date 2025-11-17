package view;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service.RenewalService;

public class RenewRegistrationPanel extends JPanel {

    private JTextField regIDField;
    private JComboBox<String> branchComboBox;
    private JButton submitButton;
    private RenewalService renewalService;

    private ArrayList<Integer> branchIDs; // map combo box index to branch ID

    public RenewRegistrationPanel(MainFrame mainFrame) {
        renewalService = new RenewalService();
        branchIDs = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TOP BAR =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(243, 246, 251));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Back button
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(10, 60, 120));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        backBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) getParent().getLayout();
            cl.show(getParent(), "userMenu");
        });

        topBar.add(backBtn, BorderLayout.WEST); // top-left
        add(topBar, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Title inside form panel
        JLabel title = new JLabel("Renew Vehicle Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(10, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 50, 0); // space below title
        formPanel.add(title, gbc);

        // Registration ID
        JLabel regLabel = new JLabel("Registration ID:");
        regLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        regLabel.setForeground(new Color(10, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(regLabel, gbc);

        regIDField = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(regIDField, gbc);

        // Branch Dropdown
        JLabel branchLabel = new JLabel("Select Branch:");
        branchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        branchLabel.setForeground(new Color(10, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(branchLabel, gbc);

        branchComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(branchComboBox, gbc);

        loadBranches();

        // ===== Submit Button next to form =====
        submitButton = new JButton("Submit Renewal");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(new Color(10, 60, 120));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        submitButton.addActionListener(e -> handleRenewal());

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(submitButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadBranches() {
        branchComboBox.removeAllItems();
        branchIDs.clear();

        ArrayList<String[]> branches = renewalService.getBranches();

        if (branches.isEmpty()) {
            branchComboBox.addItem("No Branch Available");
            branchComboBox.setEnabled(false);
        } else {
            for (String[] b : branches) {
                branchIDs.add(Integer.parseInt(b[0])); // store branchID
                branchComboBox.addItem(b[1]);          // display branchName
            }
            branchComboBox.setEnabled(true);
        }
    }

    private void handleRenewal() {
        String regText = regIDField.getText().trim();
        if (regText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Registration ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int regID;
        try {
            regID = Integer.parseInt(regText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Registration ID must be a number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int branchIndex = branchComboBox.getSelectedIndex();
        if (branchIDs.isEmpty() || branchIndex < 0) {
            JOptionPane.showMessageDialog(this, "No branch selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int branchID = branchIDs.get(branchIndex);

        // Assign a random officer in the selected branch
        int officerID = getRandomOfficer(branchID);
        if (officerID == -1) {
            JOptionPane.showMessageDialog(this, "No officers available in this branch.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verify renewal eligibility
        boolean upForRenewal = renewalService.verifyRenewal(regID);
        if (!upForRenewal) {
            JOptionPane.showMessageDialog(this, "Registration is not yet up for renewal.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Add renewal to DB
        renewalService.addRenewal(regID, branchID, officerID);
        JOptionPane.showMessageDialog(this,
                "Renewal form submitted successfully!\nTotal fee: ₱1500.00\nPlease proceed to [Settle Payments].",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        // Reset form
        regIDField.setText("");
        branchComboBox.setSelectedIndex(0);
    }

    // Officer assignment for selected branch
    private int getRandomOfficer(int branchID) {
        int officerID = renewalService.getRandomOfficer(branchID);
        if (officerID == -1) {
            JOptionPane.showMessageDialog(this, "No officers available in this branch.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return officerID;
    }
}
