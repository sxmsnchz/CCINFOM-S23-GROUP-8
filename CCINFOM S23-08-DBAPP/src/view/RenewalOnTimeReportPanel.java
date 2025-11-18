package view;

import database.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class RenewalOnTimeReportPanel extends JPanel {

    private Connection conn;

    private JTextField yearField;
    private JButton generateBtn, backBtn;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private MainFrame mainFrame;

    // Custom blue color
    private final Color customBlue = new Color(10, 60, 120);

    public RenewalOnTimeReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        conn = DatabaseConnection.getConnection();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Title Panel ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(customBlue);

        JLabel titleLabel = new JLabel("RENEWAL ON TIME REPORT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // --- Top Panel for Year input and Back button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(220, 230, 250)); // light background for inputs

        // Left: year input + generate button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftPanel.setOpaque(false);

        JLabel yearLabel = new JLabel("Enter Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        yearField = new JTextField(6);
        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        generateBtn = new JButton("Generate Report");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateBtn.setBackground(customBlue);
        generateBtn.setForeground(Color.WHITE);

        leftPanel.add(yearLabel);
        leftPanel.add(yearField);
        leftPanel.add(generateBtn);

        topPanel.add(leftPanel, BorderLayout.CENTER);

        // Right: back button
        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(customBlue);
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainFrame.showOfficerMenu());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.AFTER_LAST_LINE);

        // --- Table ---
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
                "#", "Renewal ID", "Registration ID", "Expiry Date", "Last Renewal Date", "Status"
        });

        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(25);
        resultTable.setFillsViewportHeight(true);

        // Bold headers
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Striped rows
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255));
                }
                return c;
            }
        });

        // --- Table + Status Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        // Table scroll pane
        JScrollPane scrollPane = new JScrollPane(resultTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Status label at bottom
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 128, 0)); // green
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tablePanel.add(statusLabel, BorderLayout.SOUTH);

        // Add tablePanel to main panel
        add(tablePanel, BorderLayout.CENTER);


        // --- Button Action ---
        generateBtn.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        String year = yearField.getText().trim();

        if (!year.matches("^\\d{4}$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid year format. Please enter a 4-digit year.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = """
                SELECT r.renewal_id, r.registration_id, re.expiry_date, r.last_renewal_date,
                    CASE WHEN r.last_renewal_date <= re.expiry_date THEN 'On Time' ELSE 'Late' END AS renewal_status
                FROM Renewal r
                JOIN Registration re ON r.registration_id = re.registration_id
                WHERE YEAR(r.last_renewal_date) = ?
                ORDER BY r.last_renewal_date ASC
                """;

        tableModel.setRowCount(0); // clear previous results
        int onTimeCount = 0;
        int counter = 1;
        boolean hasResults = false;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hasResults = true;

                int renewalID = rs.getInt("renewal_id");
                int registrationID = rs.getInt("registration_id");
                Date expiryDate = rs.getDate("expiry_date");
                Date lastRenewalDate = rs.getDate("last_renewal_date");
                String status = rs.getString("renewal_status");

                if ("On Time".equalsIgnoreCase(status)) {
                    onTimeCount++;
                }

                tableModel.addRow(new Object[] {
                        counter, renewalID, registrationID, expiryDate, lastRenewalDate, status
                });
                counter++;
            }

            if (!hasResults) {
                statusLabel.setText("No renewal records found for the year " + year);
            } else {
                // Update status label with summary only (no CSV)
                statusLabel.setText(String.format("Total of On-Time Renewals: %d | Overall Total of Renewals: %d",
                        onTimeCount, counter - 1));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
