package service.reports;

import database.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;

/**
 * RegistrationsByBranch.java
 *
 * This report displays the total number of vehicles registered
 * per LTO branch for a selected month and year.
 *
 * It counts all registration records (both new and renewal),
 * showing 0 if no registrations were made in that branch for that period.
 *
 * Used in Officer Dashboard → Generate Reports → "Registrations by Branch".
 */
public class RegistrationsByBranch {

    private Connection conn;

    // connects to our database
    public RegistrationsByBranch() {
        conn = DatabaseConnection.getConnection();
    }

    // VIEW REGISTRATION REPORT BY BRANCH
    public void viewRegistrationsByBranch(Scanner scanner) {
        try {
            System.out.println("==================================================");
            System.out.println("         REGISTRATIONS BY BRANCH REPORT           ");
            System.out.println("==================================================");

            // ask for month
            System.out.print("Enter month (1-12): ");
            String monthInput = scanner.nextLine().trim();

            if (!monthInput.matches("^(0?[1-9]|1[0-2])$")) { // check if valid
                System.out.println("Invalid month. Please enter a number between 1 and 12.");
                return;
            }

            int month = Integer.parseInt(monthInput);

            // ask for year
            System.out.print("Enter year (e.g., 2025): ");
            String yearInput = scanner.nextLine().trim();

            if (!yearInput.matches("^\\d{4}$")) { // check if valid
                System.out.println("Invalid year format. Please enter a 4-digit year.");
                return;
            }

            int year = Integer.parseInt(yearInput);

            System.out.println("\nGenerating report for " + getMonthName(month) + " " + year + "...\n");

            // query: total registrations per branch for selected month/year
            // LEFT JOIN ensures we still see branches even if they have 0 registrations in that month/year
            // The month/year filters are in the JOIN condition so the COUNT() returns 0 (not NULL) for branches with no rows
            String query = """
                SELECT
                    b.branch_id,
                    b.branch_name,
                    COUNT(r.registration_id) AS total_registrations
                FROM branch b
                LEFT JOIN registration r
                    ON b.branch_id = r.branch_id
                    AND MONTH(r.first_date_registered) = ?
                    AND YEAR(r.first_date_registered) = ?
                GROUP BY b.branch_id, b.branch_name
                ORDER BY b.branch_id ASC;
                """;

            // create a PreparedStatement from the query string
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, month); // bind month to the 1st ?
            ps.setInt(2, year); // bind year  to the 2nd ?
            ResultSet rs = ps.executeQuery();

            boolean hasResults = false;
            int grandTotal = 0;

            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-10s %-45s %s%n", "Branch ID", "Branch Name", "Total Registrations");
            System.out.println("----------------------------------------------------------------------------------");

            while (rs.next()) { // moves cursor to next row; returns false when no more rows
                hasResults = true;
                int branchId = rs.getInt("branch_id");
                String branchName = rs.getString("branch_name");
                int total = rs.getInt("total_registrations"); // alias from COUNT(...)

                System.out.printf("%-10d %-45s %d%n", branchId, branchName, total);
                grandTotal += total;
            }

            // even if no results, we show 0 for all
            if (!hasResults) {
                System.out.println("No branches found in the system.");
            }

            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("GRAND TOTAL: " + grandTotal + " registration(s)");
            System.out.println("==================================================================================");
            System.out.println("End of Report for " + getMonthName(month) + " " + year);
            System.out.println("==================================================================================");

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    // helper: convert month number to name
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
