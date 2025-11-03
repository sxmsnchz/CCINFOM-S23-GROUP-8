package view;

import java.util.Scanner;
import service.PaymentService;
import service.ReceiptService;
import model.Session;

/**
 * UserMenu.java
 *
 * Displays the main dashboard for vehicle owners.
 * Allows them to manage their registrations, view vehicles, view violations,
 * make payments, and view payment history.
 */
public class UserMenu {

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
            System.out.println("[6] View Receipts");
            System.out.println("[7] Logout");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("\n[Feature: Register Vehicle] (to be implemented)\n");
                    break;
                case "2":
                    System.out.println("\n[Feature: Renew Registration] (to be implemented)\n");
                    break;
                case "3":
                    System.out.println("\n[Feature: View Vehicles] (to be implemented)\n");
                    break;
                case "4":
                    System.out.println("\n[Feature: View Violations] (to be implemented)\n");
                    break;
                case "5":
                    PaymentService paymentService = new PaymentService();
                    paymentService.settlePayment(scanner);
                    break;
                case "6":
                    ReceiptService receiptService = new ReceiptService();
                    receiptService.viewReceipts();
                    break;
                case "7":
                    System.out.println("Logging out...");
                    Session.clear();
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

        // note: do not close System.in-scanner here to avoid closing System.in for callers
    }
}
