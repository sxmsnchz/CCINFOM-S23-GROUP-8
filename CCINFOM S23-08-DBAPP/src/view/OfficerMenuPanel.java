package view;

import javax.swing.*;
import java.awt.*;

public class OfficerMenuPanel extends JPanel {

    private final MainFrame mainFrame;

    public OfficerMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JPanel card = new RoundedPanel(18, new Color(255, 255, 255, 235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        card.setPreferredSize(new Dimension(420, 480));

        JLabel title = new JLabel("Officer Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage violations, records, and reports");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(16));

        card.add(createMenuButton("Record New Violation", e -> mainFrame.showRecordViolation()));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View All Violations", e -> mainFrame.showAllViolations()));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View All Registrations", e -> showNotImplemented("View All Registrations")));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View Officer Records", e -> mainFrame.showOfficerRecords()));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View Owner List", e -> showNotImplemented("View Owner List")));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View Vehicle List", e -> showNotImplemented("View Vehicle List")));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("View Branch List", e -> mainFrame.showBranchList()));
        card.add(Box.createVerticalStrut(8));
        card.add(createMenuButton("Generate Reports", e -> mainFrame.showReportsMenu()));
        card.add(Box.createVerticalStrut(14));

        JButton logout = createDangerButton("Logout");
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> doLogout());

        card.add(logout);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener l) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(340, 44));
        btn.setMaximumSize(new Dimension(340, 44));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(0, 85, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(l);
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(180, 40, 40));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return b;
    }

    private void showNotImplemented(String feature) {
        JOptionPane.showMessageDialog(this,
                feature + " is not yet implemented in the GUI.",
                "Not Implemented",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void doLogout() {
        // clear session if exists and return to home
        model.Session.clear();
        mainFrame.showHome();
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

