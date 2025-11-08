package service;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/*
 * RenewalService.java
 * 
 * This handles renewal of vehicle registration. 
 * - You can re-enter details if you have mistyped information.
 * - This will insert renewal data into renewal table with renewal date and payment id as NULL
 * - Owner will need to proceed to [Settle Payments] to fully renew their registration.
 */
public class RenewalService {
    private Connection conn;

    // connects to our database
    public RenewalService() {
        conn = DatabaseConnection.getConnection();
    }

    /*
     * Inserts renewal data into renewal table in database.
     * - payment id and renewal date are set as null
     * @param registrationID = ID record of registration
     * @param branchID = the ID record of the branch
     * @param officerID = = the ID record of the officer
     * @throws Exception if error occurs while inserting renewal data to database
     */
    public void addRenewal(int registrationID, int branchID, int officerID){

        try {

            String vehicleQuery = """
                                    INSERT INTO Renewal(registration_id, branch_id, officer_id)
                                    VALUES (?, ?, ?);
                                  """;

            PreparedStatement ps = conn.prepareStatement(vehicleQuery);
            ps.setInt(1, registrationID);
            ps.setInt(2, branchID);
            ps.setInt(3, officerID);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.out.println("Error insert renewal to database: " + e.getMessage());
        }
    }

    /*
     * Main renewal frontend and gathers data from user for renewal record.
     * - user can re-enter details if incorrect
     * @param scanner = collects user input
     * @throws Exception if error occurs while trying to process renewal 
     */
    public void renewRegistration(Scanner scanner) {

        boolean confirmDetails = false; //confirm details checker

        int registrationID, officerID, branchID;

        String choice; //confirm details user input

        try {

            System.out.println("==================================================");
            System.out.println("            RENEW REGISTRATION FORM               ");
            System.out.println("==================================================");

            while (!confirmDetails) { //if details input are incorrect, then will loop to re-enter details

                System.out.print("Registration ID : ");
                registrationID = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Handling Officer ID: ");
                officerID = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Location Branch ID: ");
                branchID = scanner.nextInt();
                scanner.nextLine();

                System.out.println("--------------------------------------------------");
                System.out.println("Are the following details correct?\n");
                System.out.println("Registration ID    : " + registrationID);
                System.out.println("Officer ID         : " + officerID);
                System.out.println("Branch ID          : " + branchID);

                do { //loops until user select valid input of (Y, y, N, n)
                    System.out.print("Enter (Y/N): ");
                    choice = scanner.nextLine();

                    //if details correct, will add renewal data to database and exit confirm details loop
                    switch (choice) {
                        case "Y" -> {
                            confirmDetails = true;
                            addRenewal(registrationID, branchID, officerID);
                            System.out.println("--Form Complete--");
                            System.out.println("The total renewal fee is ₱1500.00");
                            System.out.println("Please Proceed to [Settle Payments] to fully renew registration.");
                        }
                        case "N" -> System.out.println("Details not confirmed. Please re-enter the information.");
                        default -> System.out.println("Invalid User Input. Please try again");
                    }
                    
                } while (!choice.equalsIgnoreCase("Y") && !choice.equalsIgnoreCase("N"));
            }
    
        } catch (Exception e) {
            System.out.println("Error proceeding to renewal process: " + e.getMessage());
        }
        
    }
}
