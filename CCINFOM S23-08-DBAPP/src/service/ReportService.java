package service;

import java.util.Scanner;
import service.reports.RegistrationsByBranch;
// import service.reports.RenewalOnTime;
// import service.reports.ViolationsByOfficer;
// import service.reports.OutstandingViolations;

/**
 * ReportService.java
 *
 * This class provides a central menu for generating LTO system reports.
 * It allows officers to choose and view any of the four available reports:
 *
 *   [1] Registrations by Branch
 *   [2] Renewal On Time
 *   [3] Violations by Officer
 *   [4] Outstanding Violations
 *
 * Used under Officer Dashboard → Generate Reports.
 */
public class ReportService {

    private Scanner scanner;

    // constructor
    public ReportService() {
        scanner = new Scanner(System.in);
    }

    // ==============================================================
    // MAIN REPORTS MENU
    // ==============================================================
    public void viewReportsMenu() {
        boolean running = true;

        while (running) {
            System.out.println("==================================================");
            System.out.println("              REPORTS MANAGEMENT MENU             ");
            System.out.println("==================================================");
            System.out.println("[1] Registrations by Branch");
            System.out.println("[2] Renewal On Time");
            System.out.println("[3] Violations by Officer");
            System.out.println("[4] Outstanding Violations");
            System.out.println("[5] Back to Officer Menu");
            System.out.println("==================================================");
            System.out.print("Select a report option (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\nGenerating 'Registrations by Branch' report...\n");
                    RegistrationsByBranch report1 = new RegistrationsByBranch();
                    report1.viewRegistrationsByBranch(scanner);
                }

                case "2" -> {
                    System.out.println("\nGenerating 'Renewal On Time' report...\n");
                    System.out.println("(Feature to be implemented soon.)");
                    // RenewalOnTime report2 = new RenewalOnTime();
                    // report2.viewRenewalOnTime(scanner);
                }

                case "3" -> {
                    System.out.println("\nGenerating 'Violations by Officer' report...\n");
                    System.out.println("(Feature to be implemented soon.)");
                    // ViolationsByOfficer report3 = new ViolationsByOfficer();
                    // report3.viewViolationsByOfficer(scanner);
                }

                case "4" -> {
                    System.out.println("\nGenerating 'Outstanding Violations' report...\n");
                    System.out.println("(Feature to be implemented soon.)");
                    // OutstandingViolations report4 = new OutstandingViolations();
                    // report4.viewOutstandingViolations(scanner);
                }

                case "5" -> {
                    System.out.println("Returning to Officer Dashboard...");
                    running = false;
                }

                default -> {
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
                }
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
}
