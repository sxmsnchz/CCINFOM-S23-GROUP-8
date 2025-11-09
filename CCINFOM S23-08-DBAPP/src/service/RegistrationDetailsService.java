package service;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import model.Session;

public class RegistrationDetailsService {
    
    private Connection conn;

    //connects to our database
    public RegistrationDetailsService() {
        conn = DatabaseConnection.getConnection();
    }

    /*
     * This displays registrations under the ownerID logged in.
     * - it will display Not Paid Yet if receipt num null
     * - it will set status to INACTIVE (not paid) if  null
     * - set N/A to first_date, current_date, expiry_date if null
     * @param scanner = collects user input
     * @throws Exception if error occurs while trying to display registration info
     */
    public void viewVehicles(Scanner scanner) {

        int ownerID = Session.loggedInOwnerId;

        try {
            System.out.println("==================================================");
            System.out.println("     LIST OF REGISTRATIONS UNDER YOUR ACCOUNT     ");
            System.out.println("==================================================");

            // get all registration info under specific owner
            String vehicleQuery = """
                                    SELECT 
                                        reg.registration_id, reg.vehicle_id, reg.first_date_registered, 
                                        reg.current_date_registered, reg.expiry_date, reg.status,
                                        v.plate_number, v.make, v.series,
                                        o.officer_id, b.branch_name, rct.receipt_number
                                    FROM Registration reg
                                        JOIN Vehicle v ON reg.vehicle_id = v.vehicle_id
                                        JOIN Officer o ON reg.officer_id = o.officer_id
                                        JOIN Branch b ON reg.branch_id = b.branch_id 
                                        LEFT JOIN Receipt rct ON reg.payment_id = rct.payment_id
                                    WHERE reg.owner_id = ?
                                    ORDER BY reg.registration_id
                                   """;


            PreparedStatement ps = conn.prepareStatement(vehicleQuery);
            ps.setInt(1, ownerID);
            ResultSet vehicleRS = ps.executeQuery();

            boolean hasVehicles = false; //flag to track if any vehicles exist

            // loop through all vehicle records under owner id
            while (vehicleRS.next()) { 
                hasVehicles = true;
                
                // extract vehicle info
                int registrationID = vehicleRS.getInt("registration_id");
                int vehicleID = vehicleRS.getInt("vehicle_id");
                String plateNum = vehicleRS.getString("plate_number");
                String make = vehicleRS.getString("make");
                String series = vehicleRS.getString("series");
                String receiptNum = vehicleRS.getString("receipt_number");
                String branchName  = vehicleRS.getString("branch_name");
                int officerID = vehicleRS.getInt("officer_id");
                Date firstDateRegistered  = vehicleRS.getDate("first_date_registered");
                Date currentDateRegistered  = vehicleRS.getDate("current_date_registered");
                Date expiryDate  = vehicleRS.getDate("expiry_date");
                String status = vehicleRS.getString("status");

                System.out.println("\n--------------------------------------------------");
                System.out.println("Registration ID   : " + registrationID);
                System.out.println("Receipt Number    : " + 
                        (receiptNum == null ? "Not Paid Yet" : receiptNum)); // if null set to Not Paid Yet

                System.out.println("Status            : " + (receiptNum == null ? "INACTIVE (not paid)" : status) + "\n");  //if receiptNum null, set to INACTIVE (not paid)

                System.out.println("Vehicle ID                : " + vehicleID);
                System.out.println("Plate Number              : " + plateNum);
                System.out.println("Make                      : " + make);
                System.out.println("Series                    : " + series + "\n");

                System.out.println("Branch Name               : " + branchName);
                System.out.println("Officer ID                : " + officerID);
                System.out.println("First Date Registered     : " + 
                        (firstDateRegistered == null ? "N/A" : firstDateRegistered)); //if null then N/A
                System.out.println("Current Date Registered   : " + 
                        (currentDateRegistered == null ? "N/A" : currentDateRegistered)); //if null then N/A
                System.out.println("Expiry Date               : " + 
                        (expiryDate == null ? "N/A" : expiryDate)); //if null then N/A


            }

            if (!hasVehicles) {
                System.out.println("No registrations found under your account.");
            }

            System.out.println("=======================END========================");

        } catch (SQLException e) {
            System.out.println("Error displaying registration details: " + e.getMessage());
        }
    }
}
