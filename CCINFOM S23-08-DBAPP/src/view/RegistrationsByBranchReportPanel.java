package view;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.io.*;

public class RegistrationsByBranchReportPanel extends JPanel {

    private final MainFrame mainFrame;
    private JComboBox<String> monthBox;
    private JComboBox<String> yearBox;
    private JButton generateBtn;
    private JPanel reportCard;
    private JScrollPane scrollPane;

    private Image logoImage;
    private boolean showingDetails = false;

    public RegistrationsByBranchReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        logoImage = new ImageIcon(getClass().getResource("/assets/logo.svg_.png")).getImage();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 251));

        // TOP BAR
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topBar.setOpaque(false);

        JButton backBtn = new JButton("← Back");
        styleSecondary(backBtn);
        backBtn.addActionListener(e -> mainFrame.showReportsMenu());

        JLabel title = new JLabel("Registrations by Branch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(20, 50, 100));

        topBar.add(backBtn);
        topBar.add(title);
        add(topBar, BorderLayout.NORTH);

        // CENTER WRAPPER
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        add(center, BorderLayout.CENTER);

        // FILTER PANEL
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setOpaque(false);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMonth = new JLabel("Select Month:");
        lblMonth.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMonth.setAlignmentX(Component.CENTER_ALIGNMENT);

        monthBox = new JComboBox<>(new String[]{
                "[Select Month]",
                "01 - January", "02 - February", "03 - March", "04 - April",
                "05 - May", "06 - June", "07 - July", "08 - August",
                "09 - September", "10 - October", "11 - November", "12 - December"
        });
        monthBox.setMaximumSize(new Dimension(220, 35));
        monthBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblYear = new JLabel("Select Year:");
        lblYear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblYear.setAlignmentX(Component.CENTER_ALIGNMENT);

        yearBox = new JComboBox<>(new String[]{
                "[Select Year]", "2020", "2021", "2022", "2023", "2024", "2025"
        });
        yearBox.setMaximumSize(new Dimension(220, 35));
        yearBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        generateBtn = new JButton("Generate Report");
        stylePrimary(generateBtn);
        generateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateBtn.addActionListener(e -> generateReport());

        JButton exportCsvBtn = new JButton("Export CSV");
        styleSecondary(exportCsvBtn);
        exportCsvBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportCsvBtn.addActionListener(e -> exportCSV());

        filterPanel.add(lblMonth);
        filterPanel.add(monthBox);
        filterPanel.add(Box.createVerticalStrut(5));
        filterPanel.add(lblYear);
        filterPanel.add(yearBox);
        filterPanel.add(Box.createVerticalStrut(5));
        filterPanel.add(generateBtn);
        filterPanel.add(Box.createVerticalStrut(5));
        filterPanel.add(exportCsvBtn);
        filterPanel.add(Box.createVerticalStrut(8));

        center.add(filterPanel);

        JPanel pageWrapper = new JPanel(new GridBagLayout());
        pageWrapper.setOpaque(false);

        reportCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    int w = 60, h = 60;
                    int x = getWidth() - w - 20;
                    int y = 20;
                    g.drawImage(logoImage, x, y, w, h, null);
                }
            }
        };

        reportCard.setLayout(new BoxLayout(reportCard, BoxLayout.Y_AXIS));
        reportCard.setBackground(Color.WHITE);
        reportCard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        reportCard.setPreferredSize(new Dimension(550, 450));

        scrollPane = new JScrollPane(reportCard);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        pageWrapper.add(scrollPane);
        center.add(pageWrapper);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int targetWidth = Math.min(550, Math.max(420, w - 80));

                reportCard.setPreferredSize(new Dimension(targetWidth, 450));
                scrollPane.setPreferredSize(new Dimension(targetWidth + 40, 500));
                center.revalidate();
            }
        });
    }

    // SUMMARY REPORT
    public void generateReport() {

        showingDetails = false;
        reportCard.removeAll();

        if (monthBox.getSelectedIndex() == 0 || yearBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select both month and year.",
                    "Missing Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String monthVal = monthBox.getSelectedItem().toString().substring(0, 2);
        int month = Integer.parseInt(monthVal);
        int year = Integer.parseInt(yearBox.getSelectedItem().toString());

        JLabel header = new JLabel("Registrations for " + getMonthName(month) + " " + year);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        reportCard.add(Box.createVerticalStrut(20));
        reportCard.add(header);
        reportCard.add(Box.createVerticalStrut(10));
        reportCard.add(makeLine());

        JLabel columns = new JLabel(String.format("%-12s %-40s %s",
                "Branch ID", "Branch Name", "Total"));
        columns.setFont(new Font("Monospaced", Font.BOLD, 15));
        columns.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportCard.add(columns);
        reportCard.add(makeLine());

        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = """
                SELECT b.branch_id, b.branch_name,
                       COUNT(r.registration_id) AS total_registrations
                FROM branch b
                LEFT JOIN registration r
                    ON b.branch_id = r.branch_id
                    AND MONTH(r.first_date_registered) = ?
                    AND YEAR(r.first_date_registered) = ?
                GROUP BY b.branch_id, b.branch_name
                ORDER BY b.branch_id;
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();
            int grandTotal = 0;

            while (rs.next()) {
                int id = rs.getInt("branch_id");
                String name = rs.getString("branch_name");
                int total = rs.getInt("total_registrations");

                grandTotal += total;

                JLabel row = new JLabel(String.format("%-12s %-40s %d",
                        id, name, total));
                row.setFont(new Font("Monospaced", Font.PLAIN, 14));
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportCard.add(row);
            }

            reportCard.add(Box.createVerticalStrut(10));
            reportCard.add(makeLine());

            JLabel grand = new JLabel("Grand Total: " + grandTotal + " registration(s)");
            grand.setFont(new Font("Segoe UI", Font.BOLD, 16));
            grand.setAlignmentX(Component.CENTER_ALIGNMENT);
            reportCard.add(grand);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JButton nextBtn = new JButton(">");
        styleSecondary(nextBtn);
        nextBtn.addActionListener(e -> showDetailsPage());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        navPanel.setOpaque(false);
        navPanel.add(nextBtn);

        reportCard.add(Box.createVerticalStrut(10));
        reportCard.add(navPanel);

        reportCard.revalidate();
        reportCard.repaint();
    }

    // DETAILS PAGE
    private void showDetailsPage() {

        showingDetails = true;
        reportCard.removeAll();

        String monthVal = monthBox.getSelectedItem().toString().substring(0, 2);
        int month = Integer.parseInt(monthVal);
        int year = Integer.parseInt(yearBox.getSelectedItem().toString());

        JLabel header = new JLabel("Detailed Registrations — " + getMonthName(month) + " " + year);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        reportCard.add(Box.createVerticalStrut(20));
        reportCard.add(header);
        reportCard.add(Box.createVerticalStrut(10));
        reportCard.add(makeLine());

        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = """
                SELECT
                    v.plate_number,
                    b.branch_name,
                    r.first_date_registered
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                JOIN branch b ON r.branch_id = b.branch_id
                WHERE MONTH(r.first_date_registered) = ?
                  AND YEAR(r.first_date_registered) = ?
                ORDER BY b.branch_id, r.first_date_registered;
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            JLabel col = new JLabel(String.format(
                    "%-12s %-40s %-15s",
                    "Plate No", "Branch", "Date"));
            col.setFont(new Font("Monospaced", Font.BOLD, 14));
            col.setAlignmentX(Component.CENTER_ALIGNMENT);
            reportCard.add(col);
            reportCard.add(makeLine());

            while (rs.next()) {
                JLabel row = new JLabel(String.format(
                        "%-12s %-40s %-15s",
                        rs.getString("plate_number"),
                        rs.getString("branch_name"),
                        rs.getDate("first_date_registered").toString()
                ));
                row.setFont(new Font("Monospaced", Font.PLAIN, 13));
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportCard.add(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JButton backBtn = new JButton("<");
        styleSecondary(backBtn);
        backBtn.addActionListener(e -> generateReport());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        navPanel.setOpaque(false);
        navPanel.add(backBtn);

        reportCard.add(Box.createVerticalStrut(10));
        reportCard.add(navPanel);

        reportCard.revalidate();
        reportCard.repaint();
    }

    // EXPORT CSV
    private void exportCSV() {
        if (monthBox.getSelectedIndex() == 0 || yearBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Select month and year first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String monthVal = monthBox.getSelectedItem().toString().substring(0, 2);
        int month = Integer.parseInt(monthVal);
        int year = Integer.parseInt(yearBox.getSelectedItem().toString());

        // Hardcoded EXACT path:
        String dirPath = "CCINFOM S23-08-DBAPP/src/view/generatedreports/";
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String fileName = String.format("registrations_by_branch_%02d_%d.csv", month, year);
        File file = new File(dir, fileName);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            pw.println("Summary Report");
            pw.println("Branch ID,Branch Name,Total Registrations");

            String summaryQuery = """
                SELECT b.branch_id, b.branch_name,
                       COUNT(r.registration_id) AS total_registrations
                FROM branch b
                LEFT JOIN registration r
                    ON b.branch_id = r.branch_id
                    AND MONTH(r.first_date_registered) = ?
                    AND YEAR(r.first_date_registered) = ?
                GROUP BY b.branch_id, b.branch_name
                ORDER BY b.branch_id;
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(summaryQuery)) {

                ps.setInt(1, month);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    pw.printf("%d,%s,%d%n",
                            rs.getInt("branch_id"),
                            rs.getString("branch_name"),
                            rs.getInt("total_registrations"));
                }
            }

            pw.println();
            pw.println("Detailed Registrations");
            pw.println("Plate Number,Branch,Date");

            String detailsQuery = """
                SELECT v.plate_number, b.branch_name, r.first_date_registered
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                JOIN branch b ON r.branch_id = b.branch_id
                WHERE MONTH(r.first_date_registered) = ?
                  AND YEAR(r.first_date_registered) = ?
                ORDER BY b.branch_id, r.first_date_registered;
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(detailsQuery)) {

                ps.setInt(1, month);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    pw.printf("%s,%s,%s%n",
                            rs.getString("plate_number"),
                            rs.getString("branch_name"),
                            rs.getDate("first_date_registered").toString());
                }
            }

            JOptionPane.showMessageDialog(this,
                    "CSV saved at:\n" + file.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting CSV:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JLabel makeLine() {
        JLabel line = new JLabel("--------------------------------------------------------------------------");
        line.setFont(new Font("Monospaced", Font.PLAIN, 12));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);
        return line;
    }

    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "";
        };
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(0, 90, 200));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }
}
