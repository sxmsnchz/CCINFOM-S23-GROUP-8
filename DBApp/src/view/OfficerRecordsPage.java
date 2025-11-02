package view;

import java.util.List;
import java.util.Scanner;

import model.Officer;
import model.Registration;
import service.OfficerService;

public class OfficerRecordsPage {

    public void viewOfficerPage() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("--------------------------------------------------");
            System.out.println("                    OFFICER LIST                  ");
            System.out.println("--------------------------------------------------");
            System.out.println("[1] View All Officers");
            System.out.println("[2] View Registrations Processed by an Officer");
            System.out.println("[3] View Violations Issued by an Officer");
            System.out.println("[4] Go Back");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    viewAllOfficers();
                    break;
                case "2":
                    viewRegistrationsProcessedByOfficer(scanner);
                    break;
                case "3":
                    viewViolationsIssuedByOfficer(scanner);
                    break;
                case "4":
                    System.out.println("Going back...");
                    model.Session.clear();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }

            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    public void viewAllOfficers() {
        OfficerService officerService = new OfficerService();
        List<Officer> officers = officerService.getAllOfficers();

        if (officers.isEmpty()) {
            System.out.println("No officers found.");
        } else {
            System.out.println("--------------------------------------------------");
            System.out.printf("%-6s %-25s%n", "ID", "    Name");
            System.out.println("--------------------------------------------------");
            for (Officer o : officers) {
                // Handle null name parts defensively
                String name = (o.getFirstName() == null ? "" : o.getFirstName())
                            + " "
                            + (o.getLastName() == null ? "" : o.getLastName());
                System.out.printf("%-6d %-25s%n", o.getOfficerId(), name.trim());
            }
            System.out.println("--------------------------------------------------");
        }
    }

    public void viewRegistrationsProcessedByOfficer(Scanner scanner) {
        OfficerService officerService = new OfficerService();
        System.out.println("---------------------------------");
        System.out.print("Please enter an officer ID: ");
        String line = scanner.nextLine().trim();
        int officerId;
        try {
            officerId = Integer.parseInt(line);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid officer ID. Returning to menu.");
            return;
        }

        List<Registration> regs = officerService.getRegistrationsByOfficer(officerId);
        if (regs == null || regs.isEmpty()) {
            System.out.println("No registrations found for officer " + officerId + ".");
            return;
        }

    // Print table header (Officer name instead of ID)
    System.out.println("-------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-10s %-10s %-10s %-12s %-12s %-12s %-10s%n",
        "RegID", "VehID", "OwnerID", "FirstReg", "CurrentReg", "Expiry", "Status");
    System.out.println("-------------------------------------------------------------------------------------------------------------");

    for (Registration r : regs) {
        String first = r.getFirstDateRegistered() == null ? "" : r.getFirstDateRegistered().toString();
        String current = r.getCurrentDateRegistered() == null ? "" : r.getCurrentDateRegistered().toString();
        String expiry = r.getExpiryDate() == null ? "" : r.getExpiryDate().toString();

        String status = r.getStatus() == null ? "" : r.getStatus();

        // print: RegID, VehID, OwnerID, FirstReg, CurrentReg, Expiry, Status
        System.out.printf("%-10d %-10d %-10d %-12s %-12s %-12s %-10s%n",
            r.getRegistrationId(), r.getVehicleId(), r.getOwnerId(),
            first, current, expiry, status);
    }

    System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    public void viewViolationsIssuedByOfficer(Scanner scanner) {
        OfficerService officerService = new OfficerService();
        System.out.println("---------------------------------");
        System.out.print("Please enter an officer ID: ");
        String line = scanner.nextLine().trim();
        int officerId;
        try {
            officerId = Integer.parseInt(line);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid officer ID. Returning to menu.");
            return;
        }

        List<model.Violation> violations = officerService.getViolationsByOfficer(officerId);
        if (violations == null || violations.isEmpty()) {
            System.out.println("No violations found for officer " + officerId + ".");
            return;
        }

        // Print header
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-10s %-20s %-12s %-10s %-10s%n",
                "ViolationID", "VehicleID", "OwnerID", "Type", "Date", "Fine", "Status");
        System.out.println("---------------------------------------------------------------------------------------");

        for (model.Violation v : violations) {
            String date = v.getViolationDate() == null ? "" : v.getViolationDate().toString();
            String type = v.getViolationType() == null ? "" : v.getViolationType();
            String status = v.getStatus() == null ? "" : v.getStatus();
            System.out.printf("%-12d %-12d %-10d %-20s %-12s %-10.2f %-10s%n",
                    v.getViolationId(), v.getVehicleId(), v.getOwnerId(), type, date, v.getFineAmount(), status);
        }

        System.out.println("----------------------------------------------------------------------------------------");
    }
}
