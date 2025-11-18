package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReportsMenuPanel extends JPanel {

    private final MainFrame mainFrame;

    public ReportsMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Gray background of entire page
        setLayout(new GridBagLayout());
        setBackground(new Color(243, 246, 251));

        // === Outer container (centers card) ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // padding

        // === Title ===
        JLabel title = new JLabel("Reports Menu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(17, 54, 102));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose a report to generate");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(120, 120, 130));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Add components ===
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));

        // === Buttons ===
        card.add(createMenuButton(
                "[1] Registrations by Branch",
                e -> mainFrame.showRegistrationsByBranchReport()
        ));
        card.add(Box.createVerticalStrut(10));

        card.add(createMenuButton(
                "[2] Renewal On Time",
                e -> showNotImplemented("Renewal On Time")
        ));
        card.add(Box.createVerticalStrut(10));

        card.add(createMenuButton(
            "[3] Violations Issued by Officer",
            e -> mainFrame.showViolationsByOfficerReport()
        ));
        card.add(Box.createVerticalStrut(10));

        card.add(createMenuButton(
                "[4] Outstanding Violations",
                e -> mainFrame.showOutstandingViolationsReport()
        ));
        card.add(Box.createVerticalStrut(25));

        // === Back button ===
        JButton back = new JButton("Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setBackground(new Color(220, 220, 220));
        back.setForeground(Color.DARK_GRAY);
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        back.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        back.addActionListener(e -> mainFrame.showOfficerMenu());

        card.add(back);

        add(card, gbc);
    }

    private JButton createMenuButton(String text, ActionListener action) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Dimension size = new Dimension(260, 40);
        b.setPreferredSize(size);
        b.setMaximumSize(size);

        b.addActionListener(action);
        return b;
    }

    private void showNotImplemented(String featureName) {
        JOptionPane.showMessageDialog(
                this,
                featureName + " report screen is not yet implemented.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // === Rounded White Card ===
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

            // Shadow
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(4, 6, w - 8, h - 8, radius + 4, radius + 4);

            // White rounded card
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w - 8, h - 10, radius, radius);

            g2.dispose();
        }
    }
}




