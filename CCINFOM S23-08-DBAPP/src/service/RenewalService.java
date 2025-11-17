package service;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;

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
     * Retrieves all branches from db
     * @return list of branches (e.g. idx 0 = ID, idx 1 = name)
     */
    public ArrayList<String[]> getBranches() {
    ArrayList<String[]> branches = new ArrayList<>();
    try {
        String query = "SELECT branch_id, branch_name FROM Branch ORDER BY branch_id;";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            // store branch_id and branch_name as String[]
            branches.add(new String[]{String.valueOf(rs.getInt("branch_id")), rs.getString("branch_name")});
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return branches;

    }

    /*
     * Returns a random officer ID for a given branch
     * @param branchID = the ID record of the branch
     * @return random officer from given branch or -1 if no officers exist in that branch
     */
    public int getRandomOfficer(int branchID) {
        try {
            String query = "SELECT officer_id FROM Officer WHERE branch_id = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, branchID);
            ResultSet rs = ps.executeQuery();

            ArrayList<Integer> officers = new ArrayList<>();
            while (rs.next()) {
                officers.add(rs.getInt("officer_id"));
            }

            if (officers.isEmpty()) return -1;

            Random r = new Random();
            return officers.get(r.nextInt(officers.size()));

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
     * Verifies whether registration ID input exists and if it is up for renewal
     * @param regID = registration ID input
     * @return true if exists and up for renewal, false otherwise
     * @throws Exception if error occurs while registration info from database
     */
    public boolean verifyRenewal(int regID){

        boolean contRenewalFlag = false;
        String status;

        try {

            String renewalQuery = """
                                    SELECT reg.expiry_date,
                                        CASE 
                                            WHEN CURDATE() > reg.expiry_date THEN 'VALID FOR RENEWAL'
                                            WHEN CURDATE() BETWEEN DATE_SUB(reg.expiry_date, INTERVAL 2 MONTH) AND reg.expiry_date THEN 'VALID FOR RENEWAL'
                                            ELSE 'NOT YET FOR RENEWAL'
                                        END AS status
                                    FROM Registration reg
                                    WHERE reg.registration_id = ?;
                                 """;

            PreparedStatement ps = conn.prepareStatement(renewalQuery);
            ps.setInt(1, regID);
            ResultSet renewalRS = ps.executeQuery();

            if(renewalRS.next()){

                status = renewalRS.getString("status");
                contRenewalFlag = "VALID FOR RENEWAL".equals(status);

                if (!contRenewalFlag) 
                    System.out.println("This registration ID is not yet up for renewal. Please re-enter ID.");
            }else 
                System.out.println("No registration found with this ID. Please re-enter ID."); 

                
        } catch (Exception e) {
            System.out.println("Error retrieving registration info: " + e.getMessage());
        }

        return contRenewalFlag;
    }

    /*
     * This will display branches the user can choose from and assign an officer
     * - if there is no branch, no officer will be assigned
     * @param scanner = collects user input
     * @return officer id and branch id
     * @throws Exception if error occurs while trying to retrieve branch/officer info from database
    
    public int[] selectBranch(Scanner scanner){

        int branchInput = -1, officerID = -1;
        boolean contFunc = true; //will continue officer assign process if there are branches

        //display branch details
        try {

            String branchQuery = """
                                    SELECT branch_id, branch_name FROM Branch
                                    ORDER BY branch_id;
                                 """;

            PreparedStatement ps = conn.prepareStatement(branchQuery);
            ResultSet branchRS = ps.executeQuery();

            boolean hasBranches = false; //flag to track if any branches exist

            System.out.println("List of Branches");
            
            // iterate through all branch records
            while (branchRS.next()) {
                hasBranches = true;

                // extract branch info
                int branchID = branchRS.getInt("branch_id");
                String branchName = branchRS.getString("branch_name");
     
                // display branch list
                System.out.printf("ID %d - %s%n", branchID, branchName);
              
            }

            if(!hasBranches){
                System.out.println("No branches in the database.");
                contFunc = false; // will not proceed to officer assigning and return no branch, no officer
            }
                
        } catch (Exception e) {
            System.out.println("Error retrieving branch info: " + e.getMessage());
        }

        if(contFunc){

            //user input for chosen branch
            System.out.print("Location Branch ID: ");
            branchInput = scanner.nextInt();
            scanner.nextLine();

            try {

                //get officers in branch input
                String officerQuery = """
                                        SELECT officer_id
                                            FROM Officer    
                                        WHERE branch_id = ?;
                                    """;

                PreparedStatement ps2 = conn.prepareStatement(officerQuery);
                ps2.setInt(1, branchInput);
                ResultSet officerRS = ps2.executeQuery();

                boolean hasOfficers = false;
                ArrayList<Integer> officerIDs = new ArrayList<>(); //store office ids to assign later on

                while(officerRS.next()){
            
                    hasOfficers = true;
                    officerIDs.add(officerRS.getInt("officer_id")); //add officer id to arraylist
                }

                if(!hasOfficers){
                    System.out.println("No officers in this branch.");
                } else {

                    //assign random officer based on branch into renewal;
                    Random r = new Random();
                    int randNum = r.nextInt(officerIDs.size());
                    
                    officerID = officerIDs.get(randNum);
                }
                
            } catch (Exception e) {
                System.out.println("Error retrieving officer info: " + e.getMessage());
            }
        }

        return new int[]{branchInput, officerID};

    }
     */
    
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
     *
    public void renewRegistration(Scanner scanner) {

        boolean confirmDetails = false, upForRenewal; //confirm details checker

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

                int[] neededIDs = selectBranch(scanner); //user input branch and assigns officer

                if (neededIDs[0] == -1 || neededIDs[1] == -1) // if no branch/officer, restart renewal
                    System.out.println("Cannot proceed without branch/officer. Restarting renewal process.");  
                else {
                    branchID = neededIDs[0];
                    officerID = neededIDs[1];

                    System.out.println("--------------------------------------------------");
                    System.out.println("Are the following details correct?\n");
                    System.out.println("Registration ID    : " + registrationID);
                    System.out.println("Branch ID          : " + branchID);

                    do { //loops until user select valid input of (Y, y, N, n)
                        System.out.print("Enter (Y/N): ");
                        choice = scanner.nextLine();

                        //if details correct, will check if up for regID exists or up for renewal, else will loop confirm details
                        switch (choice) {
                            case "Y" -> {
                                
                                upForRenewal = verifyRenewal(registrationID); // checks if regID is up for renewal

                                if(upForRenewal){ //if true adds renewal to database
                                    confirmDetails = true; 
                                    addRenewal(registrationID, branchID, officerID);
                                    System.out.println("--Form Complete--");
                                    System.out.println("The total renewal fee is ₱1500.00");
                                    System.out.println("Please Proceed to [Settle Payments] to fully renew registration.");
                                }
                            }
                            case "N" -> System.out.println("Details not confirmed. Please re-enter the information.");
                            default -> System.out.println("Invalid User Input. Please try again");
                        }
                        
                    } while (!choice.equalsIgnoreCase("Y") && !choice.equalsIgnoreCase("N"));
                }

            }
    
        } catch (Exception e) {
            System.out.println("Error proceeding to renewal process: " + e.getMessage());
        }
        
    }
     */
}
