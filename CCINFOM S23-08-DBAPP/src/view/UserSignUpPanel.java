package view;

import database.DatabaseConnection;
import model.Session;

import javax.print.attribute.standard.JobState;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserSignUpPanel extends JPanel {
    private final MainFrame mainFrame;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField streetField;
    private JTextField barangayField;
    private JTextField cityField;
    private JTextField provinceField;
    private JTextField regionField;
    private JTextField postalField;
    private JPasswordField passwordField;
    private JPasswordField confirmPassField;
    private JTextField licenseField;
    private JLabel statusLabel;

    public UserSignUpPanel(MainFrame parentFrame) {
        this.mainFrame = parentFrame;

        // ===== Background of the whole page =====
        setLayout(new GridBagLayout());
        setBackground(new Color(243, 246, 251));
        setOpaque(true);

        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;
        rootGbc.anchor = GridBagConstraints.CENTER;

        // ===== Card panel =====
        JPanel card = new RoundedPanel(25, new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        card.setMaximumSize(new Dimension(620, Integer.MAX_VALUE));

        // ===== Title =====
        JLabel title = new JLabel("User Sign Up");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(17, 54, 102));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Create your account to access the LTO Vehicle Registration Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(120, 120, 130));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== Fields =====
        firstNameField   = new JTextField(20);
        lastNameField    = new JTextField(20);
        streetField      = new JTextField(20);
        barangayField    = new JTextField(20);
        cityField        = new JTextField(20);
        provinceField    = new JTextField(20);
        regionField      = new JTextField(20);
        postalField      = new JTextField(20);
        passwordField    = new JPasswordField(20);
        confirmPassField = new JPasswordField(20);
        licenseField     = new JTextField(20);

        Dimension fieldSize = new Dimension(260, 28);
        JTextField[] textFields = {
                firstNameField, lastNameField, streetField, barangayField,
                cityField, provinceField, regionField, postalField, licenseField
        };
        for (JTextField tf : textFields) tf.setPreferredSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);
        confirmPassField.setPreferredSize(fieldSize);

        // ===== Form (GridBag) =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(4, 0, 4, 0);
        fg.fill = GridBagConstraints.HORIZONTAL;
        fg.anchor = GridBagConstraints.WEST;

        int row = 0;
        row = addFieldRow(form, fg, row, "First Name:", firstNameField);
        row = addFieldRow(form, fg, row, "Last Name:", lastNameField);
        row = addFieldRow(form, fg, row, "Street:", streetField);
        row = addFieldRow(form, fg, row, "Barangay:", barangayField);
        row = addFieldRow(form, fg, row, "City:", cityField);
        row = addFieldRow(form, fg, row, "Province:", provinceField);
        row = addFieldRow(form, fg, row, "Region:", regionField);
        row = addFieldRow(form, fg, row, "Postal Code:", postalField);

        JLabel sectionLabel = new JLabel("Account & License Details");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sectionLabel.setForeground(new Color(80, 80, 95));

        fg.gridx = 0; fg.gridy = row++; fg.gridwidth = 2;
        fg.insets = new Insets(12, 0, 4, 0);
        form.add(sectionLabel, fg);

        fg.insets = new Insets(4, 0, 4, 0);
        fg.gridwidth = 1;

        row = addFieldRow(form, fg, row, "Password:", passwordField);
        row = addFieldRow(form, fg, row, "Confirm Password:", confirmPassField);
        row = addFieldRow(form, fg, row, "License Number:", licenseField);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(180, 0, 0));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signUpButton = createPrimaryButton("Sign Up");
        JButton backButton   = createSecondaryButton("Back");

        signUpButton.addActionListener(e -> doSignUp());
        backButton.addActionListener(e -> {
            clearForm();
            mainFrame.showHome();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        // ===== Assemble card =====
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(form);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(buttonPanel);

        add(card, rootGbc);
    }

    // helper for form layout
    private int addFieldRow(JPanel panel, GridBagConstraints fg, int row,
                            String labelText, JComponent field) {

        fg.gridx = 0; fg.gridy = row; fg.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, fg);

        fg.gridx = 1; fg.weightx = 1.0;
        panel.add(field, fg);

        return row + 1;
    }

    // ========================================================
    // SIGN UP LOGIC (with formatting rules added)
    // ========================================================
    private void doSignUp() {
        String fNameString = firstNameField.getText().trim();
        String lNameString = lastNameField.getText().trim();
        String streetString = streetField.getText().trim();
        String brgyString = barangayField.getText().trim();
        String cityString = cityField.getText().trim();
        String provinceString = provinceField.getText().trim();
        String regionString = regionField.getText().trim();
        String postalString = postalField.getText().trim();
        String password1 = new String(passwordField.getPassword()).trim();
        String password2 = new String(confirmPassField.getPassword()).trim();
        String licenseString = licenseField.getText().trim();

        // ================================================
        // AUTO FORMATTING RULES (Your Request)
        // ================================================

        // Capitalize City
        cityString = capitalizeWords(cityString);

        // Capitalize Province
        provinceString = capitalizeWords(provinceString);

        // NCR → Metro Manila
        if (provinceString.equalsIgnoreCase("NCR")) {
            provinceString = "Metro Manila";
        }

        // Capitalize Barangay
        brgyString = capitalizeWords(brgyString);

        // ================================================
        // VALIDATIONS (unchanged all good)
        // ================================================

        if (fNameString.isEmpty() || lNameString.isEmpty() || streetString.isEmpty() ||
                brgyString.isEmpty() || cityString.isEmpty() || provinceString.isEmpty() ||
                regionString.isEmpty() || postalString.isEmpty() || password1.isEmpty() ||
                password2.isEmpty() || licenseString.isEmpty()) {
            statusLabel.setForeground(new Color(180, 0, 0));
            statusLabel.setText("Please complete all fields.");
            return;
        }

        if (!fNameString.matches("^[A-Za-z ]+$")) {
            statusLabel.setText("Invalid. First name must contain letters only.");
            return;
        }
        if (!lNameString.matches("^[A-Za-z ]+$")) {
            statusLabel.setText("Invalid. Last name must contain letters only.");
            return;
        }
        if (!brgyString.matches("^[A-Za-z0-9 ]+$")) {
            statusLabel.setText("Invalid. Barangay must contain letters and numbers only.");
            return;
        }
        if (!cityString.matches("^[A-Za-z ]+$")) {
            statusLabel.setText("Invalid. City must contain letters only.");
            return;
        }
        if (!provinceString.matches("^[A-Za-z ]+$")) {
            statusLabel.setText("Invalid. Province must contain letters only.");
            return;
        }
        if (!regionString.matches("^[A-Za-z0-9 ]+$")) {
            statusLabel.setText("Invalid. Region must contain letters/numbers only.");
            return;
        }
        if (!postalString.matches("^\\d{4}$")) {
            statusLabel.setText("Invalid. Postal code must be 4 digits.");
            return;
        }
        if (password1.length() < 8) {
            statusLabel.setText("Invalid. Password must be at least 8 characters.");
            return;
        }
        if (!password1.equals(password2)) {
            statusLabel.setText("Invalid. Passwords do not match.");
            return;
        }
        if (!licenseString.matches("[A-Z][0-9]{2}-[0-9]{2}-[0-9]{6}$")) {
            statusLabel.setText("Invalid. License must follow A00-00-000000.");
            return;
        }

        // ================================================
        // INSERT INTO DB
        // ================================================
        String query = """
                INSERT INTO Owner
                (first_name, last_name, street, barangay, city, province, region, postal_code, password, license_number)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, fNameString);
            ps.setString(2, lNameString);
            ps.setString(3, streetString);
            ps.setString(4, brgyString);
            ps.setString(5, cityString);
            ps.setString(6, provinceString);
            ps.setString(7, regionString);
            ps.setString(8, postalString);
            ps.setString(9, password1);
            ps.setString(10, licenseString);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                int newUserId = -1;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        newUserId = rs.getInt(1);
                    }
                }

                String successMsg = "Account created successully. Your User ID is: " + newUserId;

                clearForm();
                statusLabel.setForeground(new Color(0,128,0));
                statusLabel.setText(successMsg);

                JOptionPane.showMessageDialog(this, successMsg, "Sign Up Successful", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showHome();
            }
            else {
                statusLabel.setText("Sign Up Failed. Try again.");
            }
    } catch (Exception ex) {
        ex.printStackTrace();
        statusLabel.setText("Database error.");
    }
    }
    
    public void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        streetField.setText("");
        barangayField.setText("");
        cityField.setText("");
        provinceField.setText("");
        regionField.setText("");
        postalField.setText("");
        passwordField.setText("");
        confirmPassField.setText("");
        licenseField.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(new Color(180, 0, 0));
    }

    // ========================================================
    // CAPITALIZATION HELPER
    // ========================================================
    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] parts = input.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();

        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)))
                  .append(p.substring(1));
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    /** Rounded card with soft shadow */
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
