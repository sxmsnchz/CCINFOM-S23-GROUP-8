package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewOwnerListPanel extends JPanel {

    private final MainFrame mainFrame;

    private JPanel cardsContainer;
    private JScrollPane scrollPane;

    private JButton refreshButton;
    private JButton backButton;

    public ViewOwnerListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel mainCard = new RoundedPanel(20, new Color(255, 255, 255, 235));
        mainCard.setLayout(new BorderLayout(10, 10));
        mainCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainCard.setPreferredSize(new Dimension(850, 550));

        // ----- Header -----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Owner List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));

        JLabel subtitle = new JLabel("Showing all registered vehicle owners");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(subtitle);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerButtons.setOpaque(false);

        refreshButton = new JButton("Refresh");
        backButton = new JButton("Back");

        headerButtons.add(refreshButton);
        headerButtons.add(backButton);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);

        mainCard.add(header, BorderLayout.NORTH);

        // ----- Cards container -----
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainCard.add(scrollPane, BorderLayout.CENTER);

        add(mainCard, gbc);

        // Actions
        refreshButton.addActionListener(e -> loadOwners());
        backButton.addActionListener(e -> mainFrame.showOfficerMenu());

        // NOTE: do NOT auto-load here; MainFrame will call loadOwners() when showing
    }

    public void loadOwners() {
        cardsContainer.removeAll();

        String sql =
                "SELECT owner_id, first_name, last_name, license_number, " +
                "       street, barangay, city, province, postal_code, region " +
                "FROM Owner " +
                "ORDER BY last_name, first_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                JPanel card = createOwnerCard(
                        rs.getInt("owner_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("license_number"),
                        rs.getString("street"),
                        rs.getString("barangay"),
                        rs.getString("city"),
                        rs.getString("province"),
                        rs.getInt("postal_code"),
                        rs.getString("region")
                );
                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(10));
            }

            if (!hasResults) {
                JLabel noData = new JLabel("No owners found.", SwingConstants.CENTER);
                noData.setForeground(Color.GRAY);
                noData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cardsContainer.add(noData);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error loading owners:\n" + ex.getMessage());
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createOwnerCard(int ownerId,
                                   String firstName,
                                   String lastName,
                                   String licenseNumber,
                                   String street,
                                   String barangay,
                                   String city,
                                   String province,
                                   int postalCode,
                                   String region) {

        JPanel card = new RoundedPanel(18, new Color(245, 248, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));


        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        String fullName = (firstName + " " + lastName).trim();
        JLabel nameLabel = new JLabel(fullName + "  (ID: " + ownerId + ")");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(new Color(20, 40, 90));

        JLabel licenseLabel = new JLabel("License: " + licenseNumber);
        licenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        licenseLabel.setForeground(new Color(60, 60, 60));

        top.add(nameLabel, BorderLayout.WEST);
        top.add(licenseLabel, BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new GridLayout(3, 1, 4, 2));

        String line1 = street + ", " + barangay;
        String line2 = city + ", " + province + " " + postalCode;
        String line3 = region;

        body.add(makeField("Address: ", line1));
        body.add(makeField("City/Province: ", line2));
        body.add(makeField("Region: ", line3));

        card.add(top, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel makeField(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        p.add(l);
        p.add(v);

        return p;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // rounded panel helper
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

