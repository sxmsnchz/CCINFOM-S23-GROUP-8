package service.reports;

import database.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * RegistrationsByBranch.java
 *
 * This report displays the total number of vehicles newly registered
 * per LTO branch for a selected month and year.
 *
 * It counts only *first-time* registration records (not renewals),
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

            if (!monthInput.matches("^(0?[1-9]|1[0-2])$")) {
                System.out.println("Invalid month. Please enter a number between 1 and 12.");
                return;
            }

            int month = Integer.parseInt(monthInput);

            // ask for year
            System.out.print("Enter year (e.g., 2025): ");
            String yearInput = scanner.nextLine().trim();

            if (!yearInput.matches("^\\d{4}$")) {
                System.out.println("Invalid year format. Please enter a 4-digit year.");
                return;
            }

            int year = Integer.parseInt(yearInput);

            System.out.println("\nGenerating report for " + getMonthName(month) + " " + year + "...\n");

            // summary query: total valid first-time registrations per branch
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
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();

            boolean hasResults = false;
            int grandTotal = 0;

            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-10s %-45s %s%n", "Branch ID", "Branch Name", "Total Registrations");
            System.out.println("----------------------------------------------------------------------------------");

            // create CSV file
            String fileName = String.format("registrations-by-branch-%02d-%d.csv", month, year);
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("Branch ID,Branch Name,Total Registrations");

                while (rs.next()) {
                    hasResults = true;
                    int branchId = rs.getInt("branch_id");
                    String branchName = rs.getString("branch_name");
                    int total = rs.getInt("total_registrations");

                    System.out.printf("%-10d %-45s %d%n", branchId, branchName, total);
                    writer.printf("%d,%s,%d%n", branchId, branchName, total);

                    grandTotal += total;
                }

                if (!hasResults) {
                    System.out.println("No branches found in the system.");
                }

                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("GRAND TOTAL: " + grandTotal + " registration(s)");
                System.out.println("==================================================================================");
                System.out.println("End of Report for " + getMonthName(month) + " " + year);
                System.out.println("==================================================================================");

                writer.printf("%nGrand Total,,%d%n", grandTotal);
                System.out.println("\nReport successfully saved as CSV file: " + fileName);
            }

            // ask if user wants detailed records
            System.out.print("\nWould you like to see detailed records per branch? (Y/N): ");
            String choice = scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("Y")) {
                System.out.println("\n==================================================");
                System.out.println("          DETAILED REGISTRATION LIST              ");
                System.out.println("==================================================");

                String detailedQuery = """
                    SELECT 
                        b.branch_name,
                        v.plate_number,
                        CONCAT(o.first_name, ' ', o.last_name) AS owner_name,
                        r.first_date_registered,
                        r.status
                    FROM registration r
                    JOIN branch b ON r.branch_id = b.branch_id
                    JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                    JOIN owner o ON r.owner_id = o.owner_id
                    WHERE r.first_date_registered IS NOT NULL
                      AND MONTH(r.first_date_registered) = ?
                      AND YEAR(r.first_date_registered) = ?
                    ORDER BY b.branch_name, r.first_date_registered;
                """;

                PreparedStatement ps2 = conn.prepareStatement(detailedQuery);
                ps2.setInt(1, month);
                ps2.setInt(2, year);
                ResultSet rs2 = ps2.executeQuery();

                String currentBranch = "";
                boolean hasDetails = false;

                while (rs2.next()) {
                    hasDetails = true;
                    String branchName = rs2.getString("branch_name");

                    if (!branchName.equals(currentBranch)) {
                        currentBranch = branchName;
                        System.out.println("\n--------------------------------------------------");
                        System.out.println("Branch: " + branchName);
                        System.out.println("--------------------------------------------------");
                        System.out.printf("%-10s %-25s %-15s %-10s%n", 
                            "Plate No", "Owner", "Date Registered", "Status");
                    }

                    System.out.printf("%-10s %-25s %-15s %-10s%n",
                            rs2.getString("plate_number"),
                            rs2.getString("owner_name"),
                            rs2.getDate("first_date_registered"),
                            rs2.getString("status"));
                }

                if (!hasDetails) {
                    System.out.println("No detailed registrations found for this period.");
                }

                System.out.println("==================================================");
                System.out.println("End of Detailed Records for " + getMonthName(month) + " " + year);
                System.out.println("==================================================");
            }

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
