package view;
import java.util.Scanner;
import service.PaymentService;

public class UserMenu {
    private PaymentService paymentService;

    public UserMenu() {
        this.paymentService = new PaymentService();
    }

    public void viewUserMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("--------------------------------------------------");
            System.out.println("                   USER DASHBOARD                 ");
            System.out.println("--------------------------------------------------");
            System.out.println("[1] Register Vehicle");
            System.out.println("[2] Renew Registration");
            System.out.println("[3] View Vehicles");
            System.out.println("[4] View Violations");
            System.out.println("[5] Settle Payment");
            System.out.println("[6] View Payment History");
            System.out.println("[7] Logout");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                   // registerVehicle();
                    break;
                case "2":
                    //renewRegistration();
                    break;
                case "3":
                    //viewVehicles();
                    break;
                case "4":
                    //viewViolations();
                    break;
                case "5":
                    paymentService.settlePayment(scanner);
                    break;
                case "6":
                    paymentService.viewPaymentHistory(scanner);
                    break;
                case "7":
                    System.out.println("Logging out...");
                    model.Session.clear();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 7.");
            }

            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        // note: dont add close System.in-scanner here to avoid closing System.in for callers
    }
}
