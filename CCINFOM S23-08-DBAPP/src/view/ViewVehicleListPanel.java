package view;

import database.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class ViewVehicleListPanel extends JPanel {

    private final MainFrame mainFrame;

    private JPanel cardsContainer;
    private JScrollPane scrollPane;

    private JButton refreshButton;
    private JButton backButton;

    public ViewVehicleListPanel(MainFrame mainFrame) {
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

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Vehicle List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 100));

        JLabel subtitle = new JLabel("Showing all registered vehicles in the system");
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

        // ===== CARD LIST AREA =====
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainCard.add(scrollPane, BorderLayout.CENTER);

        add(mainCard, gbc);

        // Actions
        refreshButton.addActionListener(e -> loadVehicles());
        backButton.addActionListener(e -> mainFrame.showOfficerMenu());

        // Load vehicles immediately
        loadVehicles();
    }

    // =================================================
    // LOAD VEHICLES
    // =================================================
    public void loadVehicles() {
        cardsContainer.removeAll();

        String sql = """
                        SELECT 
                            v.vehicle_id,
                            v.plate_number,
                            v.manufacture_date,
                            v.mv_file_no,
                            v.chassis_no,
                            v.engine_no,
                            v.make,
                            v.series,
                            v.color,
                            o.owner_id,
                            o.first_name,
                            o.last_name
                        FROM Registration r
                        JOIN Vehicle v ON r.vehicle_id = v.vehicle_id
                        JOIN Owner o ON r.owner_id = o.owner_id
                        ORDER BY v.vehicle_id ASC;
                    """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;

                int vehicleId = rs.getInt("vehicle_id");

                ArrayList<Integer> violations = loadViolationIDs(vehicleId);

                JPanel card = createVehicleCard(
                                vehicleId,
                                rs.getString("plate_number"),
                                rs.getDate("manufacture_date"),
                                rs.getLong("mv_file_no"),
                                rs.getString("chassis_no"),
                                rs.getString("engine_no"),
                                rs.getString("make"),
                                rs.getString("series"),
                                rs.getString("color"),
                                rs.getInt("owner_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                violations
                            );

                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(10));
            }

            if (!hasResults) {
                JLabel noData = new JLabel("No vehicles found.", SwingConstants.CENTER);
                noData.setForeground(Color.GRAY);
                noData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cardsContainer.add(noData);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error loading vehicles:\n" + ex.getMessage());
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // =================================================
    // LOAD VIOLATIONS FOR VEHICLE
    // =================================================
    private ArrayList<Integer> loadViolationIDs(int vehicleId) {
        ArrayList<Integer> list = new ArrayList<>();

        String sql = "SELECT violation_id FROM Violation WHERE vehicle_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("violation_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // =================================================
    // CREATE UI CARD
    // =================================================
    private JPanel createVehicleCard(int vehicleId,
                                 String plate,
                                 Date manufactureDate,
                                 long mvFileNo,
                                 String chassisNo,
                                 String engineNo,
                                 String make,
                                 String series,
                                 String color,
                                 int ownerId,
                                 String firstName,
                                 String lastName,
                                 ArrayList<Integer> violations) {

        JPanel card = new RoundedPanel(18, new Color(245, 248, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // --- TOP LINE: Plate + Owner Name ---
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel plateLabel = new JLabel("Plate: " + plate);
        plateLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        plateLabel.setForeground(new Color(20, 40, 90));

        String ownerFullName = firstName + " " + lastName;
        JLabel ownerLabel = new JLabel("ID "+ ownerId + " - " + ownerFullName);
        ownerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ownerLabel.setForeground(new Color(60, 60, 60));

        top.add(plateLabel, BorderLayout.WEST);
        top.add(ownerLabel, BorderLayout.EAST);

        // --- BODY: Vehicle info + Violations ---
        JPanel body = new JPanel(new GridLayout(0, 2, 10, 5)); // 0 rows, 2 columns, 10px horizontal gap, 5px vertical gap
        body.setOpaque(false);

        // Add all vehicle attributes
        body.add(makeField("Manufacture Date: ", manufactureDate.toString()));
        body.add(makeField("MV File No: ", String.valueOf(mvFileNo)));
        body.add(makeField("Chassis No: ", chassisNo));
        body.add(makeField("Engine No: ", engineNo));
        body.add(makeField("Make: ", make));
        body.add(makeField("Series: ", series));
        body.add(makeField("Color: ", color));

        // Violations in full-width row
        String violationText = (violations.isEmpty())
                ? "None"
                : violations.toString().replace("[", "").replace("]", "");

        JPanel violationPanel = makeField("Violation IDs: ", violationText);
        violationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2)); // keep it aligned
        body.add(violationPanel);

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

    // Rounded panel helper
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
