package view;
import java.util.Scanner;

public class VehicleRegistrationHome {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int choice;
        boolean running = true;

        while (running) {
            System.out.println("==========================================");
            System.out.println("        VEHICLE REGISTRATION SYSTEM       ");
            System.out.println("==========================================");
            System.out.println("[1] User Login");
            System.out.println("[2] Officer Login");
            System.out.println("[3] Exit");
            System.out.println("==========================================");
            System.out.print("Select an option: ");

            if (!input.hasNextInt()) {
                System.out.println("\nInvalid input. Please enter a number.\n");
                input.nextLine();
                continue; // re-display menu
            }

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nRedirecting to User Login...\n");
                    UserMenu userMenu = new UserMenu();
                    userMenu.viewUserMenu(); // handle logic here
                    break;
                case 2:
                    System.out.println("\nRedirecting to Officer Login...\n");
                    OfficerMenu officerMenu = new OfficerMenu();
                    officerMenu.viewOfficerMenu(); // handle logic here
                    break;
                case 3:
                    System.out.println("\nThank you! Exiting system... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid option. Please try again.\n");
            }

            // if still running, provide a small pause before re-displaying
            if (running) {
                System.out.println();
            }
        }
        input.close();
        System.exit(0);
    }
}

