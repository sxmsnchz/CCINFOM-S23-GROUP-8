
package service.reports;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ViolationsIssuedByOfficerByDate
 *
 * Report: total number of violations issued by each officer for a selected month and year.
 * Produces console output
 */
public class ViolationsIssuedByOfficerByDate {

    private Connection conn;

    public ViolationsIssuedByOfficerByDate() {
        conn = DatabaseConnection.getConnection();
    }

    public void viewViolationsByOfficerByMonth(Scanner scanner) {
        try {
            System.out.println("==================================================");
            System.out.println("     VIOLATIONS ISSUED BY OFFICER (BY MONTH)      ");
            System.out.println("==================================================");

            System.out.print("Enter month (1-12): ");
            String monthInput = scanner.nextLine().trim();
            if (!monthInput.matches("^(0?[1-9]|1[0-2])$")) {
                System.out.println("Invalid month. Please enter a number between 1 and 12.");
                return;
            }
            int month = Integer.parseInt(monthInput);

            System.out.print("Enter year (e.g. 2025): ");
            String yearInput = scanner.nextLine().trim();
            if (!yearInput.matches("^\\d{4}$")) {
                System.out.println("Invalid year format. Please enter a 4-digit year.");
                return;
            }
            int year = Integer.parseInt(yearInput);

            System.out.println("\nGenerating report for " + getMonthName(month) + " " + year + "...\n");

            String sql = """
                SELECT o.officer_id,
                       CONCAT(o.first_name, ' ', o.last_name) AS officer_name,
                       COUNT(v.violation_id) AS total_violations
                FROM officer o
                LEFT JOIN violation v
                  ON o.officer_id = v.officer_id
                  AND MONTH(v.violation_date) = ?
                  AND YEAR(v.violation_date) = ?
                GROUP BY o.officer_id, officer_name
                ORDER BY total_violations DESC, officer_name ASC
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, month);
                ps.setInt(2, year);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("---------------------------------------------------------------");
                    System.out.printf("%-10s %-35s %s%n", "Officer ID", "Officer Name", "Total Violations");
                    System.out.println("---------------------------------------------------------------");

                    boolean hasRows = false;
                    int grandTotal = 0;

                    while (rs.next()) {
                        hasRows = true;
                        int officerId = rs.getInt("officer_id");
                        String name = rs.getString("officer_name");
                        int total = rs.getInt("total_violations");

                        System.out.printf("%-10d %-35s %d%n", officerId, name, total);

                        grandTotal += total;
                    }

                    if (!hasRows) {
                        System.out.println("No officers found in the system.");
                    }

                    System.out.println("---------------------------------------------------------------");
                    System.out.println("GRAND TOTAL: " + grandTotal + " violation(s)");
                    System.out.println("===============================================================");
                }
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Programmatic report generator for GUI use. Returns a ReportResult containing rows, grand total and CSV filename.
     */
    public ReportResult generateReport(int month, int year) throws Exception {
        String sql = """
                SELECT o.officer_id,
                       CONCAT(o.first_name, ' ', o.last_name) AS officer_name,
                       COUNT(v.violation_id) AS total_violations
                FROM officer o
                LEFT JOIN violation v
                  ON o.officer_id = v.officer_id
                  AND MONTH(v.violation_date) = ?
                  AND YEAR(v.violation_date) = ?
                GROUP BY o.officer_id, officer_name
                ORDER BY total_violations DESC, officer_name ASC
                """;

        List<String[]> rows = new ArrayList<>();
        int grandTotal = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int officerId = rs.getInt("officer_id");
                    String name = rs.getString("officer_name");
                    int total = rs.getInt("total_violations");
                    rows.add(new String[]{String.valueOf(officerId), name, String.valueOf(total)});
                    grandTotal += total;
                }
            }
        }

        return new ReportResult(rows, grandTotal);
    }

    public static class ReportResult {
        private final List<String[]> rows;
        private final int grandTotal;

        public ReportResult(List<String[]> rows, int grandTotal) {
            this.rows = rows;
            this.grandTotal = grandTotal;
        }

        public List<String[]> getRows() { return rows; }
        public int getGrandTotal() { return grandTotal; }
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
            default -> "Unknown";
        };
    }
}