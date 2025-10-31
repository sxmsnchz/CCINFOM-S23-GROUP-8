import java.util.Scanner;

public class VehicleRegistrationHome {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        int choice;

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
            continue;
        }

        choice = input.nextInt();
        input.nextLine();

        switch (choice) {
            case 1 -> {
                System.put.println("\nRedirecting to User Login...\n");
                UserMenu userMenu = new userMenu();
                userMenu.login() // handle logic here 
            }
            case 2 -> {
                System.put.println("\nRedirecting to Officer Login...\n");
                OfficerMenu officerMenu = new officerMenu();
                officerMenu.login(); // handle logic here
            }
            case 3 -> {
                System.out.println("\nThank you! Exiting system... Goodbye!");
                input.close();
                System.exit(0);
            }
            default -> System.out.println("\nInvalid option. Please try again.\n");
            }
        }
    }
}
