package view;

import database.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import model.Session;

public class UserLoginPanel extends JPanel {

    private final MainFrame mainFrame;

    private JTextField idOrLicenseField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public UserLoginPanel(MainFrame parentFrame) {
        this.mainFrame = parentFrame;

        // Allow background panel
        setLayout(new BorderLayout());

        // Background Panel with Fade 
        JPanel bgPanel = new JPanel() {
            Image bgImage;
            {
                try {
                    bgImage = new ImageIcon(
                            getClass().getClassLoader().getResource("assets/lto3.jpg")
                    ).getImage();
                } catch (Exception e) {
                    bgImage = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int w = getWidth();
                int h = getHeight();

                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, w, h, this);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, w, h);
                }

                // Faded overlay
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, h, new Color(255, 255, 255, 200)
                ));
                g2.fillRect(0, 0, w, h);
            }
        };

        bgPanel.setLayout(new GridBagLayout());
        add(bgPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel card = new RoundedPanel(25, new Color(255, 255, 255, 230));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 380));

        JLabel title = new JLabel("User Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(20, 50, 100));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter Owner ID or License No.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        idOrLicenseField = new JTextField(18);
        passwordField = new JPasswordField(18);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(180, 0, 0));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 8));
        form.setOpaque(false);
        form.add(new JLabel("Owner ID / License Number:"));
        form.add(idOrLicenseField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginBtn = createPrimaryButton("Login");
        JButton backBtn = createSecondaryButton("Back");

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        backBtn.addActionListener(e -> mainFrame.showHome());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        btnPanel.add(backBtn);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(form);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(btnPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;

        // ADD CARD TO BACKGROUND PANEL
        bgPanel.add(card, gbc);
    }

    // LOGIN LOGIC 
    private void doLogin() {

        String input = idOrLicenseField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (input.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please complete all fields.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            PreparedStatement ps;

            if (input.matches("\\d+")) {
                ps = conn.prepareStatement(
                        "SELECT * FROM owner WHERE owner_id = ? AND password = ?"
                );
                ps.setInt(1, Integer.parseInt(input));
                ps.setString(2, pass);

            } else {
                ps = conn.prepareStatement(
                        "SELECT * FROM owner WHERE license_number = ? AND password = ?"
                );
                ps.setString(1, input);
                ps.setString(2, pass);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Session.loggedInOwnerId = rs.getInt("owner_id");
                Session.loggedInRole = "owner";

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful!\nWelcome, " +
                                rs.getString("first_name") + " " +
                                rs.getString("last_name") + ".",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                mainFrame.showUserMenu();

            } else {
                statusLabel.setText("Invalid ID/license or password.");
            }

        } catch (Exception e) {
            statusLabel.setText("Login error: " + e.getMessage());
        }
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
