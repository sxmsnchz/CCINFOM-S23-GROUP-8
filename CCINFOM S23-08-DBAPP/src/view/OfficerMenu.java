package view;

import java.util.Scanner;
import java.util.Date;
import java.util.List;

import database.DatabaseConnection;
import service.BranchDetailsService;
import service.OwnerService;
import service.ReportService;
import service.ViolationService;
import model.Session;

/**
 * OfficerMenu.java
 *
 * Displays the main dashboard for officers and provides access
 * to branch records, reports, and other administrative features.
 */
public class OfficerMenu {
	OfficerRecordsPage officerRecordsPage = new OfficerRecordsPage();
    public void viewOfficerMenu() {
		
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("--------------------------------------------------");
            System.out.println("                   OFFICER DASHBOARD              ");
            System.out.println("--------------------------------------------------");
            System.out.println("[1] Record New Violation");
            System.out.println("[2] View All Violations");
            System.out.println("[3] View All Registrations");
            System.out.println("[4] View Officer Records");
            System.out.println("[5] View Owner List");
            System.out.println("[6] View Vehicle List");
            System.out.println("[7] View Branch List");
            System.out.println("[8] Generate Reports");
            System.out.println("[9] Logout");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Record New Violation");
                    try {
                        System.out.print("Enter Vehicle ID: ");
                        int vehicleId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Owner ID: ");
                        int ownerId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Branch ID: ");
                        int branchId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Officer ID: ");
                        int officerId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Violation Type: ");
                        String violationType = scanner.nextLine();
                        System.out.print("Enter Fine Amount: ");
                        double fineAmount = Double.parseDouble(scanner.nextLine());
                        java.sql.Date violationDate = new java.sql.Date(System.currentTimeMillis());

                        model.Violation v = new model.Violation(0, vehicleId, ownerId, branchId, officerId, violationType, violationDate, fineAmount, "Unpaid");
    
                        ViolationService service = new ViolationService();
                        model.Violation insert = service.addViolationByOfficer(v);

                        if (insert != null) {
                            System.out.println("Violation recorded successfully! Violation ID: " + insert.getViolationId());
                        }
                        else {
                            System.out.println("Failed to record violation.");
                        }
                    }
                    catch(Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("All Violations");
                    try {
                        ViolationService vs = new ViolationService();
                        List<model.Violation> violations = vs.getAllViolations();

                        if (violations.isEmpty()) {
                            System.out.println("No violations found.");
                            break;
                        }
                        System.out.printf("%-4s %-8s %-9s %-8s %-10s %-22s %-9s %-12s%n",
                                          "ID", "Vehicle", "Owner", "Branch", "Officer", "Type", "Fine", "Date", "Status");
                        System.out.println("---------------------------------------------------------------------------------------------");

                        for(model.Violation v : violations) {
                            System.out.printf("%-4s %-8s %-9s %-8s %-10s %-22s %-9s %-12s%n",
                                              v.getViolationId(),
                                              v.getVehicleId(),
                                              v.getOwnerId(),
                                              v.getBranchId(),
                                              v.getOfficerId(),
                                              truncate(v.getViolationType(), 20),
                                              v.getFineAmount(),
                                              v.getViolationDate(),
                                              v.getPaymentStatus());
                        }
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Error loading violations: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "3":
                    System.out.println("\n[Feature: View All Registrations] (to be implemented)\n");
                    break;

                case "4":
                    officerRecordsPage.viewOfficerPage();
                    break;

                case "5":
                    System.out.println("View Owner Record and Related Information");
                    try {
                        OwnerService ownerService = new OwnerService();
                        System.out.print("Enter Owner ID or press Enter to use License Number: ");
                        String input = scanner.nextLine().trim();

                        if (input.isEmpty()) {
                            System.out.print("Enter License Number: ");
                            String licenseNumber = scanner.nextLine().trim();
                            ownerService.viewOwnerByLicenseDetails(licenseNumber);
                        } else {
                            int ownerId = Integer.parseInt(input);
                            ownerService.viewOwnerDetails(ownerId);
                        }
                    } catch (Exception e) {
                        System.out.println("Error viewing owner details. " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case "6":
                    System.out.println("\n[Feature: View Vehicle List] (to be implemented)\n");
                    break;

                case "7":
                    BranchDetailsService branchService = new BranchDetailsService();
                    branchService.viewAllBranchDetails();
                    break;

                case "8":
                    ReportService reportService = new ReportService();
                    reportService.viewReportsMenu();
                    break;

                case "9":
                    System.out.println("Logging out...");
                    Session.clear();
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice — please enter a number from 1 to 9.");
            }

            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        // note: do not close System.in-scanner here to avoid closing System.in for callers
    }

    private static String truncate(String s, int max) {
        if (s == null) 
            return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
