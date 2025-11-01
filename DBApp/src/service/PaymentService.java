package service;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;
import view.UserMenu;

public class PaymentService {

    private Connection conn;

    // connects to our database
    public PaymentService() {
        conn = DatabaseConnection.getConnection();
    }

    // ==============================================================
    // 1. SETTLE PAYMENT  (used by user to pay for unpaid violations)
    // ==============================================================
    public void settlePayment(Scanner scanner) {
        try {
            System.out.println("--------------------------------------------------");
            System.out.println("                 SETTLE PAYMENT                   ");
            System.out.println("--------------------------------------------------");

            // ask for id (make sure numeric)
            System.out.print("Enter your ID: ");
            String inputId = scanner.nextLine().trim();

            // check if id is numeric
            if (!inputId.matches("\\d+")) {
                System.out.println("Invalid ID. Please enter numbers only.");
                redirectToMenu(scanner);
                return;
            }

            int ownerId = Integer.parseInt(inputId);

            // check if id exists in owner table
            PreparedStatement checkOwner = conn.prepareStatement(
                    "SELECT owner_id FROM owner WHERE owner_id = ?");
            checkOwner.setInt(1, ownerId);
            ResultSet ownerResult = checkOwner.executeQuery();

            if (!ownerResult.next()) {
                System.out.println("ID does not exist. Redirecting back to user page...");
                redirectToMenu(scanner);
                return;
            }

            // show unpaid violations
            String query = """
                SELECT v.violation_id, v.violation_type, v.fine_amount, v.status
                FROM violation v
                WHERE v.owner_id = ? AND v.status = 'Unpaid';
                """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            List<Integer> unpaidList = new ArrayList<>();
            System.out.println("\nUnpaid Violations:");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                int vid = rs.getInt("violation_id");
                unpaidList.add(vid);
                System.out.println("Violation ID: " + vid +
                        " | Type: " + rs.getString("violation_type") +
                        " | Fine: Php " + rs.getDouble("fine_amount"));
            }

            // if no unpaid violations
            if (unpaidList.isEmpty()) {
                System.out.println("You have no unpaid violations.");
                redirectToMenu(scanner);
                return;
            }

            System.out.println("--------------------------------------------------");
            System.out.print("Enter violation ID to pay: ");
            String inputVid = scanner.nextLine().trim();

            // check if violation id numeric
            if (!inputVid.matches("\\d+")) {
                System.out.println("Invalid violation ID format.");
                redirectToMenu(scanner);
                return;
            }

            int chosenId = Integer.parseInt(inputVid);

            // check if violation exists in the unpaid list
            if (!unpaidList.contains(chosenId)) {
                System.out.println("Invalid violation ID selected.");
                redirectToMenu(scanner);
                return;
            }

            // get violation info
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT fine_amount, branch_id, officer_id FROM violation WHERE violation_id = ?");
            ps2.setInt(1, chosenId);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            double amount = rs2.getDouble("fine_amount");
            int branchId = rs2.getInt("branch_id");
            int officerId = rs2.getInt("officer_id");

            System.out.println("\nTotal to pay: Php " + amount);

            // ask user how much they will pay
            System.out.print("Enter amount you will pay: ");
            String inputAmount = scanner.nextLine().trim();

            // make sure it's a valid number
            double userPayment;
            try {
                userPayment = Double.parseDouble(inputAmount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            // if less than required fine
            if (userPayment < amount) {
                System.out.println("Insufficient payment. Please pay the full amount.");
                System.out.println("Redirecting back to unpaid violations...");
                redirectToMenu(scanner);
                return;
            }

            // show change if payment is more
            double change = 0;
            if (userPayment > amount) {
                change = userPayment - amount;
            }

            System.out.println("Payment accepted. Processing...");

            // auto-generate next receipt
            String latestReceipt = getLastReceiptNumber();
            String nextReceipt = generateNextReceipt(latestReceipt, "V");

            // insert new payment record
            PreparedStatement ps3 = conn.prepareStatement(
                    "INSERT INTO payment (officer_id, branch_id, amount_paid, date_paid, receipt_number) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps3.setInt(1, officerId);
            ps3.setInt(2, branchId);
            ps3.setDouble(3, amount);
            ps3.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps3.setString(5, nextReceipt);
            ps3.executeUpdate();

            // get payment id
            ResultSet genKeys = ps3.getGeneratedKeys();
            int paymentId = 0;
            if (genKeys.next()) {
                paymentId = genKeys.getInt(1);
            }

            // update violation
            PreparedStatement ps4 = conn.prepareStatement(
                    "UPDATE violation SET status = 'Cleared', payment_id = ? WHERE violation_id = ?");
            ps4.setInt(1, paymentId);
            ps4.setInt(2, chosenId);
            ps4.executeUpdate();

            // fetch info for the receipt
            PreparedStatement ps5 = conn.prepareStatement("""
                SELECT o.first_name, o.last_name, v.violation_type, b.branch_name,
                       f.first_name AS off_fn, f.last_name AS off_ln
                FROM violation v
                JOIN owner o ON v.owner_id = o.owner_id
                JOIN branch b ON v.branch_id = b.branch_id
                JOIN officer f ON v.officer_id = f.officer_id
                WHERE v.violation_id = ?;
                """);
            ps5.setInt(1, chosenId);
            ResultSet receiptInfo = ps5.executeQuery();

            String ownerName = "", violationType = "", branchName = "", officerName = "";
            if (receiptInfo.next()) {
                ownerName = receiptInfo.getString("first_name") + " " + receiptInfo.getString("last_name");
                violationType = receiptInfo.getString("violation_type");
                branchName = receiptInfo.getString("branch_name");
                officerName = receiptInfo.getString("off_fn") + " " + receiptInfo.getString("off_ln");
            }

            // print final receipt layout
            System.out.println("\n==================================================");
            System.out.println("                 LTO PAYMENT RECEIPT              ");
            System.out.println("==================================================");
            System.out.println("Receipt no.     : " + nextReceipt);
            System.out.println("Payment id      : " + paymentId);
            System.out.println("Owner name      : " + ownerName);
            System.out.println("Violation type  : " + violationType);
            System.out.println("Amount paid     : PHP " + amount);
            System.out.println("Date paid       : " + java.time.LocalDate.now());
            System.out.println("Processed by    : " + officerName);
            System.out.println("Branch          : " + branchName);
            System.out.println("Status          : CLEARED");
            if (change > 0) {
                System.out.println("Change given    : PHP " + change);
            }
            System.out.println("==================================================");
            System.out.println("      Thank you for settling your payment!        ");
            System.out.println("==================================================\n");

        } catch (Exception e) {
            System.out.println("Error settling payment: " + e.getMessage());
        }
    }

    // sends user back to menu smoothly
    private void redirectToMenu(Scanner scanner) {
        System.out.println("\nPress Enter to return to the user menu...");
        scanner.nextLine();
        new UserMenu().viewUserMenu();
    }

    // ==============================================================
    // 2. VIEW PAYMENT HISTORY
    // ==============================================================
    public void viewPaymentHistory(Scanner scanner) {
        try {
            System.out.println("--------------------------------------------------");
            System.out.println("              VIEW PAYMENT HISTORY                ");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your ID: ");
            String inputId = scanner.nextLine().trim();

            if (!inputId.matches("\\d+")) {
                System.out.println("Invalid ID. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            int ownerId = Integer.parseInt(inputId);

            String query = """
                SELECT p.payment_id, p.amount_paid, p.date_paid, p.receipt_number,
                       f.first_name, f.last_name, b.branch_name
                FROM payment p
                JOIN officer f ON p.officer_id = f.officer_id
                JOIN branch b ON p.branch_id = b.branch_id
                JOIN violation v ON p.payment_id = v.payment_id
                JOIN owner o ON v.owner_id = o.owner_id
                WHERE o.owner_id = ?;
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nYour payment records:");
            System.out.println("--------------------------------------------------");
            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                System.out.println("Payment ID   : " + rs.getInt("payment_id"));
                System.out.println("Amount Paid  : PHP " + rs.getDouble("amount_paid"));
                System.out.println("Date Paid    : " + rs.getDate("date_paid"));
                System.out.println("Receipt No.  : " + rs.getString("receipt_number"));
                System.out.println("Processed By : " + rs.getString("first_name") + " " + rs.getString("last_name"));
                System.out.println("Branch       : " + rs.getString("branch_name"));
                System.out.println("--------------------------------------------------");
            }

            if (!hasResults) {
                System.out.println("No payments found for this ID.");
            }

        } catch (Exception e) {
            System.out.println("Error fetching payment history: " + e.getMessage());
        }
    }

    // helper: get last receipt number
    private String getLastReceiptNumber() throws SQLException {
        String last = null;
        PreparedStatement ps = conn.prepareStatement(
                "SELECT receipt_number FROM payment ORDER BY payment_id DESC LIMIT 1");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            last = rs.getString("receipt_number");
        }
        return last;
    }

    // helper: generate next receipt number
    private String generateNextReceipt(String lastReceipt, String prefix) {
        if (lastReceipt == null) {
            return prefix + "001";
        }
        try {
            int lastNum = Integer.parseInt(lastReceipt.substring(1));
            return prefix + String.format("%03d", lastNum + 1);
        } catch (Exception e) {
            return prefix + "001";
        }
    }
}
