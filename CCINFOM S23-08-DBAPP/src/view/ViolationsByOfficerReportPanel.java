package view;

import service.reports.ViolationsIssuedByOfficerByDate;
import service.reports.ViolationsIssuedByOfficerByDate.ReportResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class ViolationsByOfficerReportPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ViolationsIssuedByOfficerByDate reportService;

    private JComboBox<String> monthCombo;
    private JTextField yearField;
    private JTable table;
    private JLabel totalLabel;

    public ViolationsByOfficerReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.reportService = new ViolationsIssuedByOfficerByDate();

        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 92, 175));
        header.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("Violations Issued By Officer", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Controls panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        monthCombo = new JComboBox<>(new String[]{"01 - January","02 - February","03 - March","04 - April","05 - May","06 - June","07 - July","08 - August","09 - September","10 - October","11 - November","12 - December"});
        controls.add(new JLabel("Month:"));
        controls.add(monthCombo);

        yearField = new JTextField(6);
        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        controls.add(new JLabel("Year:"));
        controls.add(yearField);

        JButton gen = new JButton("Generate Report");
        controls.add(gen);

        JButton back = new JButton("Back");
        controls.add(back);

        add(controls, BorderLayout.PAGE_START);

        // Table
        table = new JTable(new DefaultTableModel(new Object[]{"Officer ID", "Officer Name", "Total Violations"}, 0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Grand Total: 0", SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        footer.add(totalLabel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Actions
        gen.addActionListener((ActionEvent e) -> onGenerate());
        back.addActionListener(e -> mainFrame.showReportsMenu());
    }

    private void onGenerate() {
        try {
            String monthText = (String) monthCombo.getSelectedItem();
            int month = Integer.parseInt(monthText.substring(0,2));
            String yearText = yearField.getText().trim();
            if (!yearText.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 4-digit year.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int year = Integer.parseInt(yearText);

            ReportResult result = reportService.generateReport(month, year);

            // populate table
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (String[] row : result.getRows()) {
                model.addRow(new Object[]{row[0], row[1], Integer.parseInt(row[2])});
            }
            totalLabel.setText("Grand Total: " + result.getGrandTotal());

            JOptionPane.showMessageDialog(this, "Report generated and saved as: " + result.getFileName(), "Report Saved", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
