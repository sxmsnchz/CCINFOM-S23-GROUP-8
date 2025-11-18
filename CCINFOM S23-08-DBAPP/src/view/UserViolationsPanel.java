package view;

import database.DatabaseConnection;
import model.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserViolationsPanel extends JPanel {

    private final MainFrame mainFrame;

    private JPanel cardsContainer;
    private JScrollPane scrollPane;
    private JButton backButton;
    private JButton refreshButton;

    public UserViolationsPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Your Violations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));

        JLabel subtitle = new JLabel("List of violations recorded under your name");
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
        backButton = new JButton("Back to Menu");

        headerButtons.add(refreshButton);
        headerButtons.add(backButton);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);

        mainCard.add(header, BorderLayout.NORTH);

        // Cards container
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainCard.add(scrollPane, BorderLayout.CENTER);

        add(mainCard, gbc);

        // Actions
        refreshButton.addActionListener(e -> loadViolations());
        backButton.addActionListener(e -> mainFrame.showUserMenu());
    }

    /** Loads violations for the logged-in owner */
    public void loadViolations() {
        cardsContainer.removeAll();

        int ownerId = Session.loggedInOwnerId;
        if (ownerId <= 0) {
            showError("User is not logged in.");
            cardsContainer.revalidate();
            cardsContainer.repaint();
            return;
        }

        String sql =
                "SELECT v.violation_id, v.vehicle_id, ve.plate_number, " +
                "       v.violation_type, v.fine_amount, v.violation_date, " +
                "       v.payment_status, b.branch_name, " +
                "       CONCAT(ofc.first_name, ' ', ofc.last_name) AS officer_name " +
                "FROM Violation v " +
                "JOIN Vehicle ve ON v.vehicle_id = ve.vehicle_id " +
                "JOIN Branch b   ON v.branch_id = b.branch_id " +
                "JOIN Officer ofc ON v.officer_id = ofc.officer_id " +
                "WHERE v.owner_id = ? " +
                "ORDER BY v.violation_date DESC, v.violation_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResults = false;

                while (rs.next()) {
                    hasResults = true;
                    JPanel card = createViolationCard(
                            rs.getInt("violation_id"),
                            rs.getInt("vehicle_id"),
                            rs.getString("plate_number"),
                            rs.getString("violation_type"),
                            rs.getDouble("fine_amount"),
                            rs.getDate("violation_date"),
                            rs.getString("payment_status"),
                            rs.getString("branch_name"),
                            rs.getString("officer_name")
                    );
                    cardsContainer.add(card);
                    cardsContainer.add(Box.createVerticalStrut(10));
                }

                if (!hasResults) {
                    JLabel noData = new JLabel("You currently have no recorded violations.", SwingConstants.CENTER);
                    noData.setForeground(Color.GRAY);
                    noData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    cardsContainer.add(noData);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error loading violations:\n" + ex.getMessage());
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createViolationCard(int violationId,
                                       int vehicleId,
                                       String plateNumber,
                                       String violationType,
                                       double fineAmount,
                                       java.util.Date violationDate,
                                       String paymentStatus,
                                       String branchName,
                                       String officerName) {

        JPanel card = new RoundedPanel(18, new Color(245, 248, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Top: Violation # + status
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel idLabel = new JLabel("Violation #" + violationId + "  - Plate: " + plateNumber);
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        idLabel.setForeground(new Color(20, 40, 90));

        JLabel statusLabel = new JLabel(paymentStatus == null ? "Unpaid" : paymentStatus);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

        if (paymentStatus != null &&
                (paymentStatus.equalsIgnoreCase("Paid")
                        || paymentStatus.equalsIgnoreCase("Cleared"))) {

            statusLabel.setBackground(new Color(0, 140, 70));
            statusLabel.setForeground(Color.WHITE);

        } else {
            statusLabel.setBackground(new Color(200, 50, 50)); 
            statusLabel.setForeground(Color.WHITE);
        }

        top.add(idLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        // Body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new GridLayout(4, 2, 5, 4));

        body.add(makeField("Vehicle ID: ", String.valueOf(vehicleId)));
        body.add(makeField("Branch: ", branchName));
        body.add(makeField("Violation: ", violationType));
        body.add(makeField("Fine Amount: ", "₱" + fineAmount));
        body.add(makeField("Date: ", violationDate != null ? violationDate.toString() : ""));
        body.add(makeField("Issuing Officer: ", officerName));

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


