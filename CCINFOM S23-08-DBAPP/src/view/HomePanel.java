package view;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    private final MainFrame mainFrame;

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        // === Background Panel ===
        JPanel bgPanel = new JPanel() {
            Image bgImage;
            {
                try {
                    bgImage = new ImageIcon(
                            getClass().getClassLoader().getResource("assets/background.jpg")
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

        // === Card Panel ===
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

        // For now, buttons just open empty message dialogs
        userLoginBtn.addActionListener(e -> mainFrame.showUserLogin());
        officerLoginBtn.addActionListener(e -> mainFrame.showOfficerLogin());
        signUpBtn.addActionListener(e -> mainFrame.showSignUp());;
        exitBtn.addActionListener(e -> System.exit(0));

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

        bgPanel.add(card);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 45));
        btn.setMaximumSize(new Dimension(260, 45));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBackground(new Color(0, 85, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = createButton(text);
        btn.setBackground(new Color(180, 40, 40));
        return btn;
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
