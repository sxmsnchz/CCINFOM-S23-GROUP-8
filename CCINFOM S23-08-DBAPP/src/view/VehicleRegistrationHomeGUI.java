package view;

import javax.swing.*;
import java.awt.*;

public class VehicleRegistrationHomeGUI extends JFrame {

    public VehicleRegistrationHomeGUI() {
        setTitle("LTO Vehicle Registration Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==============================================================
        // FULLSCREEN BACKGROUND PANEL WITH FADE OVERLAY
        // ==============================================================
        JPanel backgroundPanel = new JPanel() {
            Image bgImage;

            {
                try {
                    // Load background from classpath
                    bgImage = new ImageIcon(
                            getClass().getClassLoader().getResource("assets/background.jpg")
                    ).getImage();

                } catch (Exception e) {
                    System.out.println("Failed to load background image.");
                    bgImage = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int w = getWidth();
                int h = getHeight();

                if (bgImage != null) {
                    // Draw background FULLY stretched to window
                    g.drawImage(bgImage, 0, 0, w, h, this);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, w, h);
                }

                // Fade overlay (white gradient top → bottom)
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, h, new Color(255, 255, 255, 200)
                ));
                g2.fillRect(0, 0, w, h);
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // ==============================================================
        // CENTERED ROUNDED CARD PANEL
        // ==============================================================
        JPanel card = new RoundedPanel(25, new Color(255, 255, 255, 235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        JLabel title = new JLabel("Land Transportation Office");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(20, 50, 100));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vehicle Registration Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(60, 60, 60));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton userLoginBtn = createButton("User Login");
        JButton officerLoginBtn = createButton("Officer Login");
        JButton signUpBtn = createButton("Create Account");
        JButton exitBtn = createDangerButton("Exit");

        // TEMP actions
        userLoginBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "User Login Placeholder"));
        officerLoginBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Officer Login Placeholder"));
        signUpBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Sign Up Placeholder"));
        exitBtn.addActionListener(e -> System.exit(0));

        // Add components to the card
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

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 45));
        btn.setMaximumSize(new Dimension(260, 45));

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBackground(new Color(0, 85, 180));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 70, 150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 85, 180));
            }
        });

        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = createButton(text);
        btn.setBackground(new Color(180, 40, 40));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(150, 25, 25));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(180, 40, 40));
            }
        });

        return btn;
    }

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

    public static void main(String[] args) {
        new VehicleRegistrationHomeGUI();
    }
}
