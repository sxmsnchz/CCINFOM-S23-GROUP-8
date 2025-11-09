package view;

import java.util.List;
import java.util.Scanner;
import model.Session;
import service.PaymentService;
import service.ReceiptService;
import service.RegistrationService;
import service.RenewalService;
import service.ViolationService;

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
                    RegistrationService registrationService = new RegistrationService();
                    registrationService.registerVehicle(scanner);
                    break;
                case "2":
                    RenewalService renewalService = new RenewalService();
                    renewalService.renewRegistration(scanner);
                    break;
                case "3":
                    System.out.println("\n[Feature: View Vehicles] (to be implemented)\n");
                    break;
                case "4":
                    System.out.println("All Violations");
                    try {
                        int ownerId = Session.loggedInOwnerId;
                        ViolationService vs = new ViolationService();
                        List<model.Violation> violations = vs.getViolationsOfOwner(ownerId);

                        if (violations.isEmpty()) {
                            System.out.println("No violations found.");
                            break;
                        }
                        System.out.printf("%-4s %-8s %-9s %-8s %-10s %-22s %-9s %-12s%n",
                                          "ID", "Vehicle", "Owner", "Branch", "Officer", "Type", "Fine", "Date", "Status");
                        System.out.println("---------------------------------------------------------------------------------------------");

                        for(model.Violation v : violations) {
                            System.out.printf("%-4s %-8s %-9s %-8s %-10s %-22s %-9s %-12s%n",
                                              v.getViolationId(),
                                              v.getVehicleId(),
                                              v.getOwnerId(),
                                              v.getBranchId(),
                                              v.getOfficerId(),
                                              truncate(v.getViolationType(), 20),
                                              v.getFineAmount(),
                                              v.getViolationDate(),
                                              v.getPaymentStatus());
                        }
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Error loading violations: " + e.getMessage());
                        e.printStackTrace();
                    }
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

     private static String truncate(String s, int max) {
        if (s == null) 
            return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
