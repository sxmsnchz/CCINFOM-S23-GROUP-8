package view;

import database.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import model.Session;

public class UserMenuPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel titleLabel;
    private Image bgImage;

    public UserMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        //load bg img
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/userMenuBG.png"));
        bgImage = icon.getImage();

        setLayout(new BorderLayout());

        //header style formatting
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 92, 175, 180),
                        0, getHeight(), new Color(0, 120, 215, 180)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(800, 90));
        header.setLayout(new BorderLayout());

        titleLabel = new JLabel("Welcome, User", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(titleLabel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        //center panel formatting
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setOpaque(false);

        //menu buttons and icons
        String[] options = {
                "Register a Vehicle",
                "Renew your Registration",
                "Vehicle Registrations",
                "Violations",
                "Payment Hub",
                "Transaction History",
                "LTO Branches",
                "Logout"
        };

        Icon[] icons = {
                UIManager.getIcon("FileView.fileIcon"),
                UIManager.getIcon("FileView.directoryIcon"),
                UIManager.getIcon("FileView.computerIcon"),
                UIManager.getIcon("FileView.hardDriveIcon"),
                UIManager.getIcon("OptionPane.warningIcon"),
                UIManager.getIcon("OptionPane.informationIcon"),
                UIManager.getIcon("FileView.floppyDriveIcon"),
                UIManager.getIcon("OptionPane.errorIcon")
        };

        for (int i = 0; i < options.length; i++) {
            JButton btn = new JButton(options[i], icons[i]);
            styleDashboardButton(btn);
            centerPanel.add(btn);

            final int index = i;
            btn.addActionListener(e -> {
                System.out.println("Clicked: " + options[index]);

                // ====== MENU HANDLERS (ONLY PART UPDATED) ======

                if (options[index].equals("Logout")) {
                    Session.loggedInOwnerId = 0;
                    mainFrame.showHome();
                    return;
                }

                if (options[index].equals("Register a Vehicle")) {
                    mainFrame.showRegistrationPanel();
                    return;
                }

                if (options[index].equals("Renew your Registration")) {
                    mainFrame.showRenewRegistration();
                    return;
                }

                if (options[index].equals("Vehicle Registrations")) {
                    mainFrame.createAndShowViewRegistrationPanel();
                    return;
                }

                if (options[index].equals("LTO Branches")) {
                    mainFrame.showUserBranchDirectory();
                    return;
                }

                if (options[index].equals("Payment Hub")) {
                    mainFrame.showUserPayment();
                    return;
                }

                if (options[index].equals("Transaction History")) {
                    mainFrame.showReceiptHistory();
                    return;
                }

                if (options[index].equals("Violations")) {
                    mainFrame.showUserViolations();
                    return;
                }

                // other buttons not implemented yet
                JOptionPane.showMessageDialog(
                        UserMenuPanel.this,
                        options[index] + " panel is not yet implemented",
                        "To Be Added",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }

        add(centerPanel, BorderLayout.CENTER);
    }

    //bg to the back, buttons on top
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    //update owner name to show in header
    public void setOwnerName(int ownerId) {
        String ownerName = fetchOwnerName(ownerId);
        titleLabel.setText("Welcome, " + ownerName);
    }

    //dashboard like button style formatting
    private void styleDashboardButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 110, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(150, 100));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 140, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 110, 200));
            }
        });
    }

    //fetch owner name frm database
    private String fetchOwnerName(int ownerId) {
        String fullName = "User";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String nameQuery = "SELECT first_name, last_name FROM owner WHERE owner_id = ?";
            PreparedStatement ps = conn.prepareStatement(nameQuery);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                fullName = rs.getString("first_name") + " " + rs.getString("last_name");

        } catch (Exception e) {
            System.out.println("Error fetching owner name: " + e.getMessage());
        }
        return fullName;
    }
}
