package view;

import java.util.Scanner;

public class OfficerMenu {

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
			System.out.println("[4] View Officer List");
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
					//recordNewViolation();
					break;
				case "2":
					//viewAllViolations();
					break;
				case "3":
					//viewAllRegistrations();
					break;
				case "4":
					//viewOfficerList();
					break;
				case "5":
					//viewOwnerList();
					break;
				case "6":
					//viewVehicleList();
					break;
				case "7":
					//viewBranchList();
					break;
				case "8":
					//generateReports();
					break;
				case "9":
					System.out.println("Logging out...");
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

		// Do not close System.in scanner here; let caller manage lifecycle if needed.
	}
}
