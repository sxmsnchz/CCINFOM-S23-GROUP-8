package service.reports;

import database.DatabaseConnection;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class RenewalOnTime {
    private Connection conn;

    // connects to our database
    public RenewalOnTime() {
        conn = DatabaseConnection.getConnection();
    }

    /*
     * This generates and displays all renewal records with the status "On time" or "Late"
     * - includes total renewals and total on time renewals
     * - it will print renewal data into csv file
     * @param scanner = collects user input
     * @throws Exception if error occurs while trying to generate report or print report into csv
     */
    public void createRenewalOnTimeReport(Scanner scanner) {
        try {
            System.out.println("==================================================");
            System.out.println("      RENEWAL OF REGISTRATION ON TIME REPORT      ");
            System.out.println("==================================================");

            boolean yearFlag = false, hasResults = false; // checker for year and results
            String year = null; //stores year input

            while (!yearFlag) { //loops while year input is incorrect format
                System.out.println("Enter year to be reviewed: ");
                year = scanner.nextLine().trim();

                if (year.matches("^\\d{4}$"))
                    yearFlag = true;
                else
                    System.out.println("Invalid year format. Please try again.");
            }

            String renewalYearQuery = """
                                        SELECT 
                                            r.renewal_id,
                                            r.registration_id,
                                            re.expiry_date,
                                            r.last_renewal_date,
                                            CASE 
                                                WHEN r.last_renewal_date <= re.expiry_date THEN 'On Time'
                                                ELSE 'Late'
                                            END AS renewal_status
                                        FROM Renewal r
                                            JOIN Registration re ON r.registration_id = re.registration_id
                                        WHERE YEAR(r.last_renewal_date) = ?
                                        ORDER BY r.last_renewal_date ASC;
                                      """;

            PreparedStatement ps = conn.prepareStatement(renewalYearQuery);
            ps.setString(1, year);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%n---------------- Renewal on Time Report for YEAR %s ----------------%n%n", year);

            String filename = String.format("renewal-on-time-report-%s.csv", year);

            //prints out renewal data from database into terminal and saving it in csv file
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

                int counter = 1; //number ascending counter
                int onTimeCount = 0; //counts renewals on time

                writer.println("#,Renewal ID,Registration ID,Expiry Date,Last Renewal Date,Status"); //print column titles into csv

                while (rs.next()) {

                    hasResults = true; 

                    int renewalID = rs.getInt("renewal_id");
                    int registrationID = rs.getInt("registration_id");
                    Date expiryDate = rs.getDate("expiry_date");
                    Date lastRenewalDate = rs.getDate("last_renewal_date");
                    String status = rs.getString("renewal_status");

                    if ("On Time".equalsIgnoreCase(status)) //counts up for on time renewal status
                        onTimeCount++;

                    System.out.printf("%d) Renewal ID: %d  |  Registration ID: %d%n", counter, renewalID, registrationID);
                    System.out.printf("    Registration Expiry            : %s%n", expiryDate);
                    System.out.printf("    Last Renewal of Registration   : %s%n", lastRenewalDate);
                    System.out.printf("    Status                         : %s%n%n", status);

                    writer.printf("%d,%d,%d,%s,%s,%s%n", counter, renewalID, registrationID, expiryDate, lastRenewalDate, status); // writes renewal data into csv

                    counter++; //counts up per renewal data
                }

                if (!hasResults)
                    System.out.println("No renewal records found for the selected year.");
                else {
                    System.out.println("\n\nTOTAL RENEWALS COMPLETED ON TIME : " + onTimeCount + " renewal(s)");
                    System.out.println("TOTAL OVERALL RENEWALS           : " + (counter - 1) + " renewal(s)");
                    System.out.println("\n----------------------------------- END OF REPORT -----------------------------------");
                    System.out.println("\nReport successfully saved as CSV file: " + filename);
                }

            } catch (Exception e) {
                System.out.println("Error printing report to CSV: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}
