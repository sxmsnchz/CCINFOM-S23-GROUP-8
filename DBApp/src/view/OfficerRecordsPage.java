package view;

import java.util.List;
import java.util.Scanner;

import model.Officer;
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
            System.out.println("[2] Registrations Processed by an Officer");
            System.out.println("[3] Violations Issued by an Officer");
            System.out.println("[4] Go Back");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    printAllOfficers();
                    break;
                case "2":
                    // viewRegistrationsProcessedByOfficer
                    break;
                case "3":
                    //viewViolationsIssuedByOfficer
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
    public void printAllOfficers() {
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
}
