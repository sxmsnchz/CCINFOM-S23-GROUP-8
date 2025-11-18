package view;

import database.DatabaseConnection;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAllRegistrationsPanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel cardsContainer;
    private JScrollPane scrollPane;
    private JButton refreshButton;
    private JButton backButton;

    public ViewAllRegistrationsPanel(MainFrame mainFrame) {
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

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Registrations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));

        JLabel subtitle = new JLabel("Showing all registrations for your assigned branch");
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

        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainCard.add(scrollPane, BorderLayout.CENTER);

        add(mainCard, gbc);

        refreshButton.addActionListener(e -> loadRegistrations());
        backButton.addActionListener(e -> mainFrame.showOfficerMenu());
    }

    public void loadRegistrations() {
        cardsContainer.removeAll();

        int officerId = Session.loggedInOfficerId;
        if (officerId == -1) {
            showError("Officer is not logged in.");
            cardsContainer.revalidate();
            cardsContainer.repaint();
            return;
        }

        // Show all registrations across the system (no branch filter)
        String sql =
            "SELECT reg.registration_id, reg.owner_id, CONCAT(o.first_name, ' ', o.last_name) AS owner_name, " +
            " reg.vehicle_id, v.plate_number, reg.branch_id, reg.status, reg.first_date_registered, reg.current_date_registered, reg.expiry_date, rct.receipt_number " +
            "FROM Registration reg " +
            "JOIN Owner o ON reg.owner_id = o.owner_id " +
            "JOIN Vehicle v ON reg.vehicle_id = v.vehicle_id " +
            "LEFT JOIN Receipt rct ON reg.payment_id = rct.payment_id " +
            "ORDER BY reg.current_date_registered DESC, reg.registration_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    JPanel card = createRegistrationCard(
                            rs.getInt("registration_id"),
                            rs.getInt("owner_id"),
                            rs.getString("owner_name"),
                            rs.getInt("vehicle_id"),
                            rs.getString("plate_number"),
                            rs.getInt("branch_id"),
                            rs.getString("status"),
                            rs.getDate("first_date_registered"),
                            rs.getDate("current_date_registered"),
                            rs.getDate("expiry_date"),
                            rs.getString("receipt_number")
                    );
                    cardsContainer.add(card);
                    cardsContainer.add(Box.createVerticalStrut(10));
                }

                if (!hasResults) {
                    JLabel noData = new JLabel("No registrations found for your branch.", SwingConstants.CENTER);
                    noData.setForeground(Color.GRAY);
                    noData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    cardsContainer.add(noData);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error loading registrations:\n" + ex.getMessage());
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createRegistrationCard(int regId, int ownerId, String ownerName,
                                           int vehicleId, String plate, int branchId,
                                           String status, java.util.Date firstReg,
                                           java.util.Date currentReg, java.util.Date expiry,
                                           String receiptNumber) {

        JPanel card = new RoundedPanel(18, new Color(245, 248, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel idLabel = new JLabel("Registration #" + regId);
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        idLabel.setForeground(new Color(20, 40, 90));

        JLabel statusLabel = new JLabel(status == null ? "Unknown" : status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

        if (status != null && status.equalsIgnoreCase("Active")) {
            statusLabel.setBackground(new Color(0, 140, 70));
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setBackground(new Color(200, 50, 50));
            statusLabel.setForeground(Color.WHITE);
        }

        top.add(idLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new GridLayout(4, 2, 5, 4));

        body.add(makeField("Owner:", ownerName));
        body.add(makeField("Owner ID:", String.valueOf(ownerId)));
        body.add(makeField("Vehicle ID:", String.valueOf(vehicleId)));
        body.add(makeField("Plate No:", plate));
        body.add(makeField("Branch:", String.valueOf(branchId)));
        body.add(makeField("Receipt No:", receiptNumber == null ? "Not Paid" : receiptNumber));
        body.add(makeField("First Registered:", firstReg != null ? firstReg.toString() : "N/A"));
        body.add(makeField("Expiry:", expiry != null ? expiry.toString() : "N/A"));

        card.add(top, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel makeField(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        p.setOpaque(false);

        JLabel l = new JLabel(label + " ");
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

    private int getOfficerBranch(int officerId) {
        String sql = "SELECT branch_id FROM Officer WHERE officer_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, officerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bid = rs.getInt("branch_id");
                    if (rs.wasNull()) return -1;
                    return bid;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
        return -1;
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
