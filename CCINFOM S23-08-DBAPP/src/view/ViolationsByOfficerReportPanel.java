package view;

import service.reports.ViolationsIssuedByOfficerByDate;
import service.reports.ViolationsIssuedByOfficerByDate.ReportResult;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

        JButton exportCsv = new JButton("Export CSV");
        controls.add(exportCsv);

        JButton back = new JButton("Back");
        controls.add(back);

        add(controls, BorderLayout.PAGE_START);

        // Table
        table = new JTable(new DefaultTableModel(new Object[]{"Officer ID", "Officer Name", "Total Violations Issued"}, 0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Grand Total: 0", SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        footer.add(totalLabel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Actions
        gen.addActionListener((ActionEvent e) -> onGenerate());
        exportCsv.addActionListener(e -> onExportCsv());
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

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void onExportCsv() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data to export. Generate the report first.", "Export CSV", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String monthText = (String) monthCombo.getSelectedItem();
            int month = Integer.parseInt(monthText.substring(0,2));
            String yearText = yearField.getText().trim();
            String suggested = String.format("violations_by_officer_%02d_%s.csv", month, yearText);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save CSV");
            chooser.setSelectedFile(new File(suggested));
            chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
            int sel = chooser.showSaveDialog(this);
            if (sel != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            if (file.exists()) {
                int ok = JOptionPane.showConfirmDialog(this, "File exists. Overwrite?", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                if (ok != JOptionPane.YES_OPTION) return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // header
                bw.write("Officer ID,Officer Name,Total Violations Issued");
                bw.newLine();
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object id = model.getValueAt(i, 0);
                    Object name = model.getValueAt(i, 1);
                    Object total = model.getValueAt(i, 2);
                    bw.write(escapeCsv(String.valueOf(id)) + "," + escapeCsv(String.valueOf(name)) + "," + escapeCsv(String.valueOf(total)));
                    bw.newLine();
                }
            }

            JOptionPane.showMessageDialog(this, "CSV exported to: " + file.getAbsolutePath(), "Export CSV", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ioe.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        String out = s.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\n") || out.contains("\r") || out.contains("\"")) {
            return "\"" + out + "\"";
        }
        return out;
    }
}
