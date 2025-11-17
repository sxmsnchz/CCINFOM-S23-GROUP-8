package view;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OutstandingViolationsReportPanel extends JPanel {

    private final MainFrame mainFrame;

    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JLabel totalLabel;
    private JPanel resultPanel;

    public OutstandingViolationsReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 251));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildReportCard(), BorderLayout.CENTER);

        // Auto-load all unpaid violations (Month = All, Year = All)
        loadReportData();
    }

    // ============================================================
    // TOP BAR: Back + Month + Year + Search
    // ============================================================
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(new Color(243, 246, 251));

        JButton back = new JButton("Back");
        back.addActionListener(e -> mainFrame.showReportsMenu());

        monthCombo = new JComboBox<>(new String[]{
                "All", "January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December"
        });

        yearCombo = new JComboBox<>(new String[]{
                "All", "2023", "2024", "2025", "2026"
        });

        JButton search = new JButton("Search");
        search.setBackground(new Color(0, 90, 200));
        search.setForeground(Color.WHITE);
        search.setFocusPainted(false);
        search.addActionListener(e -> loadReportData());

        top.add(back);
        top.add(monthCombo);
        top.add(yearCombo);
        top.add(search);

        return top;
    }

    // ============================================================
    // MAIN ROUNDED REPORT CARD
    // ============================================================
    private JPanel buildReportCard() {
        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header: title + total
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Outstanding Violations Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(17, 54, 102));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalLabel = new JLabel("Total Unpaid Violations: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalLabel.setForeground(Color.DARK_GRAY);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(totalLabel);
        header.add(Box.createVerticalStrut(10));

        card.add(header, BorderLayout.NORTH);

        // Scrollable results panel
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    // ============================================================
    // LOAD DATA FROM DB USING SQL
    // ============================================================
    private void loadReportData() {
        resultPanel.removeAll();

        String selectedMonth = (String) monthCombo.getSelectedItem();
        String selectedYear  = (String) yearCombo.getSelectedItem();

        boolean filterMonth = selectedMonth != null && !"All".equals(selectedMonth);
        boolean filterYear  = selectedYear  != null && !"All".equals(selectedYear);

        StringBuilder sql = new StringBuilder("""
            SELECT
                v.violation_id,
                vh.plate_number,
                CONCAT(o.last_name, ', ', o.first_name) AS owner_name,
                b.branch_name,
                v.violation_type,
                v.violation_date,
                v.fine_amount
            FROM Violation v
            JOIN Vehicle  vh ON v.vehicle_id = vh.vehicle_id
            JOIN Owner    o  ON v.owner_id   = o.owner_id
            JOIN Branch   b  ON v.branch_id  = b.branch_id
            WHERE TRIM(UPPER(v.payment_status)) = 'UNPAID'
        """);

        if (filterMonth) {
            sql.append(" AND MONTH(v.violation_date) = ? ");
        }
        if (filterYear) {
            sql.append(" AND YEAR(v.violation_date) = ? ");
        }

        sql.append(" ORDER BY owner_name, v.violation_date");

        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) {
                resultPanel.add(new JLabel("Database connection error."));
                refreshResultPanel();
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;

                if (filterMonth) {
                    ps.setInt(paramIndex++, getMonthNumber(selectedMonth));
                }
                if (filterYear) {
                    ps.setInt(paramIndex++, Integer.parseInt(selectedYear));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    Map<String, java.util.List<String>> data = new LinkedHashMap<>();
                    int total = 0;

                    while (rs.next()) {
                        total++;

                        String ownerName = rs.getString("owner_name");
                        String plate     = rs.getString("plate_number");
                        String branch    = rs.getString("branch_name");
                        java.sql.Date date = rs.getDate("violation_date");
                        String type      = rs.getString("violation_type");
                        double fine      = rs.getDouble("fine_amount");

                        String detail = String.format(
                                "%s – %s – %s – ₱%.2f (%s)",
                                date,
                                plate,
                                type,
                                fine,
                                branch
                        );

                        data.computeIfAbsent(ownerName, k -> new java.util.ArrayList<>())
                                .add(detail);
                    }

                    System.out.println("[OutstandingViolations] rows found = " + total);
                    totalLabel.setText("Total Unpaid Violations: " + total);

                    if (total == 0) {
                        resultPanel.add(new JLabel("No unpaid violations for the selected period."));
                    } else {
                        renderOwnerGroups(data);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            resultPanel.add(new JLabel("Error loading data: " + ex.getMessage()));
        }

        refreshResultPanel();
    }

    // Render grouped violations per owner
    private void renderOwnerGroups(Map<String, java.util.List<String>> data) {
        for (Map.Entry<String, java.util.List<String>> entry : data.entrySet()) {
            String owner = entry.getKey();
            java.util.List<String> violations = entry.getValue();

            JPanel ownerCard = new JPanel();
            ownerCard.setLayout(new BoxLayout(ownerCard, BoxLayout.Y_AXIS));
            ownerCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            ownerCard.setBackground(new Color(250, 250, 250));

            JLabel ownerLabel = new JLabel(owner);
            ownerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            ownerLabel.setForeground(new Color(30, 60, 100));

            ownerCard.add(ownerLabel);
            ownerCard.add(Box.createVerticalStrut(5));

            for (String vText : violations) {
                JLabel vLabel = new JLabel("• " + vText);
                vLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                ownerCard.add(vLabel);
            }

            resultPanel.add(ownerCard);
            resultPanel.add(Box.createVerticalStrut(15));
        }
    }

    private void refreshResultPanel() {
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    // ============================================================
    // Helper: convert month name to number (1–12)
    // ============================================================
    private int getMonthNumber(String monthName) {
        return switch (monthName) {
            case "January" -> 1;
            case "February" -> 2;
            case "March" -> 3;
            case "April" -> 4;
            case "May" -> 5;
            case "June" -> 6;
            case "July" -> 7;
            case "August" -> 8;
            case "September" -> 9;
            case "October" -> 10;
            case "November" -> 11;
            case "December" -> 12;
            default -> 0;
        };
    }

    // ============================================================
    // Rounded card class
    // ============================================================
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

            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(4, 6, w - 8, h - 8, radius + 4, radius + 4);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w - 8, h - 10, radius, radius);

            g2.dispose();
        }
    }
}

