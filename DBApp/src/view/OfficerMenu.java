package view;

import java.util.Scanner;
import service.BranchDetailsService;
import service.ReportService;
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
                    System.out.println("\n[Feature: Record New Violation] (to be implemented)\n");
                    break;

                case "2":
                    System.out.println("\n[Feature: View All Violations] (to be implemented)\n");
                    break;

                case "3":
                    System.out.println("\n[Feature: View All Registrations] (to be implemented)\n");
                    break;

                case "4":
                    officerRecordsPage.viewOfficerPage();
                    break;

                case "5":
                    System.out.println("\n[Feature: View Owner List] (to be implemented)\n");
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
}
