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

        choice = input.nextInt();

        switch (choice) {
            case 1 -> System.out.println("\nUser Login");
            case 2 -> System.out.println("\nOfficer Login");
            case 3 -> System.out.println("\nExiting...");
            default -> System.out.println("\nInvalid option. Please try again.");
        }

        input.close();
    }
}

