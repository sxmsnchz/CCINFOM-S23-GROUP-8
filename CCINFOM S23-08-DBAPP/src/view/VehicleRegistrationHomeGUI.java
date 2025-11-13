package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VehicleRegistrationHomeGUI extends JFrame {

    public VehicleRegistrationHomeGUI() {
        setTitle("LTO Vehicle Registration Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==============================================================
        // BACKGROUND PANEL (Loads assets/background.jpg)
        // ==============================================================
        JPanel backgroundPanel = new JPanel() {
            Image bgImage;

            {
                try {
                    // ★ CORRECT RELATIVE PATH ★
                    String path = "assets/background.jpg";
                    File file = new File(path);

                    System.out.println("Background exists? " + file.exists());
                    System.out.println("Loaded from: " + file.getAbsolutePath());

                    bgImage = new ImageIcon(path).getImage();
                } catch (Exception e) {
                    e.printStackTrace();
                    bgImage = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // ==============================================================
        // WHITE ROUNDED CARD
        // ==============================================================
        JPanel card = new RoundedPanel(30, new Color(255, 255, 255, 235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 40, 40, 40));

        // TITLE
        JLabel title = new JLabel("Land Transportation Office");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(10, 60, 130));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vehicle Registration Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(60, 60, 60));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // BUTTONS
        JButton userLoginBtn = createButton("User Login");
        JButton officerLoginBtn = createButton("Officer Login");
        JButton signUpBtn = createButton("Create Account");
        JButton exitBtn = createDangerButton("Exit");

        // TEMP ACTIONS (placeholder)
        userLoginBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "User Login Placeholder"));
        officerLoginBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Officer Login Placeholder"));
        signUpBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Create Account Placeholder"));
        exitBtn.addActionListener(e -> System.exit(0));

        // ADD TO CARD
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(userLoginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(officerLoginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(signUpBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(exitBtn);

        backgroundPanel.add(card);

        setVisible(true);
    }

    // ==============================================================
    // BUTTON STYLES
    // ==============================================================
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setMaximumSize(new Dimension(250, 45));

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBackground(new Color(0, 90, 200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 80, 170));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 90, 200));
            }
        });

        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = createButton(text);
        btn.setBackground(new Color(170, 35, 35));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(150, 25, 25));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(170, 35, 35));
            }
        });

        return btn;
    }

    // ==============================================================
    // ROUNDED PANEL CLASS
    // ==============================================================
    class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    // ==============================================================
    public static void main(String[] args) {
        new VehicleRegistrationHomeGUI();
    }
}
