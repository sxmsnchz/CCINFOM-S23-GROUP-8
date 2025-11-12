package service;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BranchDetailsService.java
 *
 * This class displays all LTO branches in the system,
 * along with the officers assigned to each branch
 * and the registrations processed under them.
 *
 * Used by officers in the Officer Dashboard (option: View Branch List)
 * and by users for viewing available branches.
 */
public class BranchDetailsService {

    private Connection conn;

    // connects to the database
    public BranchDetailsService() {
        conn = DatabaseConnection.getConnection();
    }

    // ============================================================
    // OFFICER: DISPLAY ALL BRANCHES WITH OFFICERS AND REGISTRATIONS
    // ============================================================
    public void viewAllBranchDetails() {
        try {
            System.out.println("==================================================");
            System.out.println("         LTO BRANCH DIRECTORY & DETAILS           ");
            System.out.println("==================================================");

            // get all branches in order
            String branchQuery = "SELECT * FROM branch ORDER BY branch_id";
            PreparedStatement ps = conn.prepareStatement(branchQuery);
            ResultSet branchRS = ps.executeQuery();

            boolean hasBranches = false; // flag to track if any branches exist

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
                String contactNumber = branchRS.getString("contact_number");

                // display branch header info
                System.out.println("--------------------------------------------------");
                System.out.println("Branch ID   : " + branchId);
                System.out.println("Branch Name : " + branchName);
                System.out.println("Address     : " + street + ", " + barangay + ", " + city + ", " + province + " (" + postalCode + ")");
                System.out.println("Region      : " + region);
                System.out.println("Contact No. : " + (contactNumber != null ? contactNumber : "N/A"));

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
                    String formattedName = officerRS.getString("last_name") + ", " + officerRS.getString("first_name");
                    System.out.println(" - " + formattedName);
                }

                if (!hasOfficers) {
                    System.out.println(" - No officers recorded for this branch.");
                }

                // REGISTRATIONS PROCESSED IN THIS BRANCH (exclude INACTIVE)
                String regQuery = """
                    SELECT r.registration_id, v.plate_number, r.status
                    FROM registration r
                    JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                    WHERE r.branch_id = ?
                      AND r.status IN ('ACTIVE', 'EXPIRED')  -- only show completed/processed
                    ORDER BY r.registration_id ASC;
                """;
                PreparedStatement ps3 = conn.prepareStatement(regQuery);
                ps3.setInt(1, branchId);
                ResultSet regRS = ps3.executeQuery();

                System.out.println("\nRegistrations Processed:");
                boolean hasRegistrations = false;

                while (regRS.next()) {
                    hasRegistrations = true;
                    int regId = regRS.getInt("registration_id");
                    String plateNo = regRS.getString("plate_number");
                    String status = regRS.getString("status");

                    System.out.println(" - Reg. ID: " + regId +
                                       " | Plate No: " + plateNo +
                                       " | Status: " + status);
                }

                if (!hasRegistrations) {
                    System.out.println(" - No active or expired registrations recorded for this branch.");
                }

                System.out.println("--------------------------------------------------\n");
            }

            if (!hasBranches) {
                System.out.println("No branches found in the database.");
            }

            System.out.println("==================================================");
            System.out.println("         End of Branch Directory.                 ");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("Error displaying branch details: " + e.getMessage());
        }
    }

    // ============================================================
    // FETCH ALL BRANCHES (for list population)
    // ============================================================
    public List<model.Branch> getAllBranches() {
        List<model.Branch> branches = new ArrayList<>();
        if (conn == null) return branches;

        String query = """
            SELECT b.branch_name, b.branch_id
            FROM branch b
            ORDER BY b.branch_name
        """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("branch_id");
                String branchName = rs.getString("branch_name");
                model.Branch branch = new model.Branch(id, branchName);
                branches.add(branch);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load branches: " + e.getMessage());
            e.printStackTrace();
        }
        return branches;
    }

    // ============================================================
    // DISPLAY BASIC BRANCH LIST (used for reference in forms)
    // ============================================================
    public void viewAllBranches() {
        List<model.Branch> branches = getAllBranches();

        if (branches.isEmpty()) {
            System.out.println("No branches found.");
        } else {
            System.out.println("--------------------------------------------------");
            System.out.printf("%-9s %-30s%n", "Branch ID", "Branch Name");
            System.out.println("--------------------------------------------------");
            for (model.Branch b : branches) {
                String name = b.getBranchName() != null ? b.getBranchName() : "N/A";
                System.out.printf("%-9d %-30s%n", b.getBranchId(), name.trim());
            }
            System.out.println("--------------------------------------------------");
        }
    }

    // ============================================================
    // USER: SIMPLIFIED DIRECTORY VIEW
    // ============================================================
    public void viewAllBranchesForUsers() {
        try {
            System.out.println("==================================================");
            System.out.println("               LTO BRANCH DIRECTORY               ");
            System.out.println("==================================================");

            String query = """
                SELECT branch_id, branch_name, street, barangay, city, province, postal_code, region, contact_number
                FROM branch
                ORDER BY branch_id ASC;
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            boolean hasBranches = false;

            while (rs.next()) {
                hasBranches = true;
                int branchId = rs.getInt("branch_id");
                String branchName = rs.getString("branch_name");
                String address = rs.getString("street") + ", " +
                                 rs.getString("barangay") + ", " +
                                 rs.getString("city") + ", " +
                                 rs.getString("province") + " (" + rs.getString("postal_code") + ")";
                String region = rs.getString("region");
                String contact = rs.getString("contact_number");

                System.out.println("--------------------------------------------------");
                System.out.println("Branch ID   : " + branchId);
                System.out.println("Branch Name : " + branchName);
                System.out.println("Address     : " + address);
                System.out.println("Region      : " + region);
                System.out.println("Contact No. : " + (contact != null ? contact : "N/A"));
            }

            if (!hasBranches) {
                System.out.println("No branches found in the database.");
            }

            System.out.println("==================================================");
            System.out.println("            End of Branch Directory.              ");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("Error displaying branches: " + e.getMessage());
        }
    }
}
