package service;

import database.DatabaseConnection;
import java.sql.*;

/**
 * BranchDetailsService.java
 *
 * This class displays all LTO branches in the system,
 * along with the officers assigned to each branch
 * and the registrations processed under them.
 *
 * Used by officers in the Officer Dashboard (option: View Branch List).
 */
public class BranchDetailsService {

    private Connection conn;

    // connects to the database
    public BranchDetailsService() {
        conn = DatabaseConnection.getConnection();
    }

    // DISPLAY ALL BRANCHES WITH OFFICERS AND REGISTRATIONS
    public void viewAllBranchDetails() {
        try {
            System.out.println("==================================================");
            System.out.println("         LTO BRANCH DIRECTORY & DETAILS           ");
            System.out.println("==================================================");

            // get all branches in order
            String branchQuery = "SELECT * FROM branch ORDER BY branch_id";
            PreparedStatement ps = conn.prepareStatement(branchQuery);
            ResultSet branchRS = ps.executeQuery();

            boolean hasBranches = false; //flag to track if any branches exist
            
            // iterate through all branch records
            while (branchRS.next()) {
                hasBranches = true;

                // extract branch info
                int branchId = branchRS.getInt("branch_id");
                String branchName = branchRS.getString("branch_name");
                String street = branchRS.getString("street");
                String barangay = branchRS.getString("barangay");
                String city = branchRS.getString("city");
                String province = branchRS.getString("province");
                String postalCode = branchRS.getString("postal_code");
                String region = branchRS.getString("region");

                // display branch header info
                System.out.println("--------------------------------------------------");
                System.out.println("Branch ID   : " + branchId);
                System.out.println("Branch Name : " + branchName);
                System.out.println("Address     : " + street + ", " + barangay + ", " + city + ", " + province + " (" + postalCode + ")");
                System.out.println("Region      : " + region);

                // OFFICERS ASSIGNED TO THIS BRANCH
                String officerQuery = """
                    SELECT first_name, last_name
                    FROM officer
                    WHERE branch_id = ?
                    ORDER BY last_name ASC;
                    """;
                PreparedStatement ps2 = conn.prepareStatement(officerQuery);
                ps2.setInt(1, branchId);
                ResultSet officerRS = ps2.executeQuery();

                System.out.println("\nOfficers Assigned:");
                boolean hasOfficers = false;

                while (officerRS.next()) {
                    hasOfficers = true;
                    // format name
                    String formattedName = officerRS.getString("last_name") + ", " + officerRS.getString("first_name");
                    System.out.println(" - " + formattedName);
                }

                // if no officers are recorded in that branch
                if (!hasOfficers) {
                    System.out.println(" - No officers recorded for this branch.");
                }

                // REGISTRATIONS PROCESSED IN THIS BRANCH
                String regQuery = """
                    SELECT r.registration_id, v.plate_no, r.status
                    FROM registration r
                    JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                    WHERE r.branch_id = ?
                    ORDER BY r.registration_id ASC;
                    """;
                PreparedStatement ps3 = conn.prepareStatement(regQuery);
                ps3.setInt(1, branchId);
                ResultSet regRS = ps3.executeQuery();

                System.out.println("\nRegistrations Processed:");
                boolean hasRegistrations = false;

                // list each registration record found for this branch
                while (regRS.next()) {
                    hasRegistrations = true;
                    int regId = regRS.getInt("registration_id");
                    String plateNo = regRS.getString("plate_no");
                    String status = regRS.getString("status");

                    System.out.println(" - Reg. ID: " + regId +
                                       " | Plate No: " + plateNo +
                                       " | Status: " + status);
                }

                if (!hasRegistrations) {
                    System.out.println(" - No registrations processed yet for this branch.");
                }

                System.out.println("--------------------------------------------------\n");
            }

            // no branches found case
            if (!hasBranches) {
                System.out.println("No branches found in the database.");
            }

            System.out.println("==================================================");
            System.out.println("         End of Branch Directory.                 ");
            System.out.println("==================================================");

        } catch (Exception e) { // error handling in case of SQL issues
            System.out.println("Error displaying branch details: " + e.getMessage());
        }
    }
}


