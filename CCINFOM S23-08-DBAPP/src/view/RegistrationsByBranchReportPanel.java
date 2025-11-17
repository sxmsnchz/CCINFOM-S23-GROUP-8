package view;
import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistrationsByBranchReportPanel extends JPanel {

    private final MainFrame mainFrame;


    private JComboBox<String> monthBox;
    private JComboBox<String> yearBox;
    private JButton generateBtn;
    private JPanel resultCard;
    private JScrollPane scrollPane;

    private Image bgImage; // faded LTO background


    public RegistrationsByBranchReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;


        bgImage = new ImageIcon(getClass().getResource("/assets/lto.jpg")).getImage();


        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        // ===== TOP BAR =====
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


        // ===== FILTER CARD =====
        JPanel filterCard = new JPanel();
        filterCard.setLayout(new BoxLayout(filterCard, BoxLayout.Y_AXIS));
        filterCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        filterCard.setBackground(new Color(255, 255, 255, 200));
        filterCard.setPreferredSize(new Dimension(450, 170));


        monthBox = new JComboBox<>(new String[]{
                "01 - January", "02 - February", "03 - March", "04 - April",
                "05 - May", "06 - June", "07 - July", "08 - August",
                "09 - September", "10 - October", "11 - November", "12 - December"
        });


        yearBox = new JComboBox<>(new String[]{
                "2020", "2021", "2022", "2023", "2024", "2025"
        });


        generateBtn = new JButton("Generate Report");
        stylePrimary(generateBtn);
        generateBtn.addActionListener(e -> generateReport());


        filterCard.add(new JLabel("Select Month:"));
        filterCard.add(monthBox);
        filterCard.add(Box.createVerticalStrut(10));
        filterCard.add(new JLabel("Select Year:"));
        filterCard.add(yearBox);
        filterCard.add(Box.createVerticalStrut(15));
        filterCard.add(generateBtn);


        add(filterCard, BorderLayout.WEST);


        // ===== RESULT CARD =====
        resultCard = new JPanel();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        resultCard.setBackground(new Color(255, 255, 255, 230));


        scrollPane = new JScrollPane(resultCard);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);


        add(scrollPane, BorderLayout.CENTER);
    }


    // ===== FADED BACKGROUND IMAGE =====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
    }


    // ===== RUN THE REPORT =====
    private void generateReport() {


        resultCard.removeAll();


        String monthVal = monthBox.getSelectedItem().toString().substring(0, 2);
        int yearVal = Integer.parseInt(yearBox.getSelectedItem().toString());


        int month = Integer.parseInt(monthVal);


        JLabel header = new JLabel(
                "Registrations for " + getMonthName(month) + " " + yearVal,
                SwingConstants.LEFT
        );
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);


        resultCard.add(header);
        resultCard.add(Box.createVerticalStrut(15));


        try (Connection conn = DatabaseConnection.getConnection()) {


            String query = """
                SELECT
                    b.branch_id,
                    b.branch_name,
                    COUNT(r.registration_id) AS total_registrations
                FROM branch b
                LEFT JOIN registration r
                    ON b.branch_id = r.branch_id
                    AND r.first_date_registered IS NOT NULL
                    AND MONTH(r.first_date_registered) = ?
                    AND YEAR(r.first_date_registered) = ?
                GROUP BY b.branch_id, b.branch_name
                ORDER BY b.branch_id ASC;
            """;


            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, month);
            ps.setInt(2, yearVal);


            ResultSet rs = ps.executeQuery();


            int grandTotal = 0;


            // Table-like output
            JLabel headerRow = makeRow("Branch ID", "Branch Name", "Total");
            headerRow.setFont(new Font("Segoe UI", Font.BOLD, 14));
            resultCard.add(headerRow);
            resultCard.add(makeLine());


            while (rs.next()) {


                int id = rs.getInt("branch_id");
                String name = rs.getString("branch_name");
                int total = rs.getInt("total_registrations");


                grandTotal += total;


                resultCard.add(makeRow(
                        String.valueOf(id),
                        name,
                        String.valueOf(total)
                ));
            }


            resultCard.add(Box.createVerticalStrut(15));
            resultCard.add(makeLine());


            JLabel grand = new JLabel("Grand Total: " + grandTotal + " registration(s)");
            grand.setFont(new Font("Segoe UI", Font.BOLD, 14));
            grand.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultCard.add(grand);


        } catch (Exception ex) {
            ex.printStackTrace();
            resultCard.add(new JLabel("Error generating report."));
        }


        resultCard.revalidate();
        resultCard.repaint();
    }


    private JLabel makeRow(String col1, String col2, String col3) {
        JLabel row = new JLabel(String.format(
                "%-12s %-45s %s", col1, col2, col3
        ));
        row.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }


    private JLabel makeLine() {
        JLabel line = new JLabel("--------------------------------------------------------------------------");
        line.setFont(new Font("Monospaced", Font.PLAIN, 12));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
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
    }


    private void styleSecondary(JButton b) {
        b.setBackground(new Color(230, 230, 230));
        b.setForeground(Color.darkGray);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}

