package service;

import database.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import view.UserMenu;

/**
 * PaymentService.java
 *
 * This class handles all payment-related functions for the Vehicle Registration System.
 * It allows users to:
 *   1. Settle unpaid transactions (either violations or registration/renewal).
 *   2. View their complete payment history.
 *
 * It connects directly to the database, performs validation,
 * auto-generates receipts, and updates records accordingly.
 */

public class PaymentService {

    private Connection conn; // connection object for MySQL database

    // connects to our database
    public PaymentService() {
        conn = DatabaseConnection.getConnection();
    }

    // ==============================================================
    // 1. SETTLE PAYMENT  (used by user to pay for unpaid violations or registrations)
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

            // if ID not found, redirect user back
            if (!ownerResult.next()) {
                System.out.println("ID does not exist. Redirecting back to user page...");
                redirectToMenu(scanner);
                return;
            }

            // fetch all UNPAID transactions (violations + registrations)
            String query = """
                SELECT 'Violation' AS type, v.violation_id AS id, v.violation_type AS description, v.fine_amount AS amount
                FROM violation v
                WHERE v.owner_id = ? AND v.status = 'Unpaid'
                UNION
                SELECT 'Registration' AS type, r.registration_id AS id,
                       CASE
                           WHEN r.payment_id IS NULL AND r.expiry_date IS NULL THEN 'New Registration'
                           WHEN r.payment_id IS NOT NULL AND r.expiry_date IS NOT NULL THEN 'Renewal'
                           ELSE 'Registration Pending'
                       END AS description,
                       CASE
                           WHEN r.payment_id IS NULL AND r.expiry_date IS NULL THEN 7410
                           WHEN r.payment_id IS NOT NULL AND r.expiry_date IS NOT NULL THEN 2500
                           ELSE 0
                       END AS amount
                FROM registration r
                WHERE r.owner_id = ? AND (r.payment_id IS NULL OR r.expiry_date < CURDATE());
                """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();

            // display all unpaid transactions
            System.out.println("\nUnpaid Transactions:");
            System.out.println("--------------------------------------------------");
            boolean hasUnpaid = false;

            while (rs.next()) {
                hasUnpaid = true;
                System.out.println("[" + rs.getString("type") + "] ID: " + rs.getInt("id") +
                        " | " + rs.getString("description") +
                        " | Amount: Php " + rs.getDouble("amount"));
            }

            if (!hasUnpaid) {
                System.out.println("You have no unpaid violations or registrations.");
                redirectToMenu(scanner);
                return;
            }

            // ask user which type and ID they want to pay
            System.out.println("--------------------------------------------------");
            System.out.print("Enter transaction type (Violation / Registration): ");
            String chosenType = scanner.nextLine().trim();

            if (!chosenType.equalsIgnoreCase("Violation") && !chosenType.equalsIgnoreCase("Registration")) {
                System.out.println("Invalid type. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            System.out.print("Enter ID to pay: ");
            String inputTid = scanner.nextLine().trim();
            if (!inputTid.matches("\\d+")) {
                System.out.println("Invalid ID format.");
                redirectToMenu(scanner);
                return;
            }

            int chosenId = Integer.parseInt(inputTid);

            // define variables to store transaction info
            double amount = 0;
            int branchId = 0;
            int officerId = 0;
            String plateNo = null;
            String transactionDesc = "";

            // if it's a violation transaction
            if (chosenType.equalsIgnoreCase("Violation")) {
                PreparedStatement ps2 = conn.prepareStatement("""
                    SELECT fine_amount, branch_id, officer_id, v.vehicle_id, ve.plate_no, v.violation_type
                    FROM violation v
                    JOIN vehicle ve ON v.vehicle_id = ve.vehicle_id
                    WHERE violation_id = ?;
                """);
                ps2.setInt(1, chosenId);
                ResultSet rs2 = ps2.executeQuery();

                if (!rs2.next()) {
                    System.out.println("Violation ID not found.");
                    redirectToMenu(scanner);
                    return;
                }

                amount = rs2.getDouble("fine_amount");
                branchId = rs2.getInt("branch_id");
                officerId = rs2.getInt("officer_id");
                plateNo = rs2.getString("plate_no");
                transactionDesc = rs2.getString("violation_type");

            // if it's a registration transaction (registration or renewal)
            } else if (chosenType.equalsIgnoreCase("Registration")) {
                PreparedStatement ps3 = conn.prepareStatement("""
                    SELECT r.branch_id, r.officer_id, r.vehicle_id, r.payment_id, r.expiry_date, v.plate_no
                    FROM registration r
                    JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                    WHERE r.registration_id = ?;
                """);
                ps3.setInt(1, chosenId);
                ResultSet rs3 = ps3.executeQuery();

                if (!rs3.next()) {
                    System.out.println("Registration ID not found.");
                    redirectToMenu(scanner);
                    return;
                }

                branchId = rs3.getInt("branch_id");
                officerId = rs3.getInt("officer_id");
                plateNo = rs3.getString("plate_no");
                int prevPay = rs3.getInt("payment_id");
                Date expiry = rs3.getDate("expiry_date");

                // determine registration type
                if (prevPay != 0 && expiry != null) {
                    transactionDesc = "Renewal";
                    amount = 2500.00;
                } else {
                    transactionDesc = "New Registration";
                    amount = 7410.00;
                }
            }

            // process payment
            System.out.println("\nTotal to pay: Php " + amount);
            System.out.print("Enter amount you will pay: ");
            String inputAmount = scanner.nextLine().trim();

            double userPayment;
            try {
                userPayment = Double.parseDouble(inputAmount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            if (userPayment < amount) {
                System.out.println("Insufficient payment. Please pay the full amount.");
                redirectToMenu(scanner);
                return;
            }

            double change = userPayment > amount ? userPayment - amount : 0;
            System.out.println("Payment accepted. Processing...");

            // generate next receipt number automatically
            String latestReceipt = getLastReceiptNumber();
            String nextReceipt = generateNextReceipt(latestReceipt,
                    chosenType.equalsIgnoreCase("Violation") ? "V" : "R");

            // insert payment record into payment table
            PreparedStatement ps4 = conn.prepareStatement(
                    "INSERT INTO payment (officer_id, branch_id, amount_paid, date_paid, receipt_number) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps4.setInt(1, officerId);
            ps4.setInt(2, branchId);
            ps4.setDouble(3, amount);
            ps4.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps4.setString(5, nextReceipt);
            ps4.executeUpdate();

            ResultSet genKeys = ps4.getGeneratedKeys();
            int paymentId = 0;
            if (genKeys.next()) {
                paymentId = genKeys.getInt(1);
            }

            // update relevant table (violation or registration)
            if (chosenType.equalsIgnoreCase("Violation")) {
                PreparedStatement updateV = conn.prepareStatement(
                        "UPDATE violation SET status = 'Cleared', payment_id = ? WHERE violation_id = ?");
                updateV.setInt(1, paymentId);
                updateV.setInt(2, chosenId);
                updateV.executeUpdate();
            } else {
                PreparedStatement updateR = conn.prepareStatement("""
                        UPDATE registration
                        SET payment_id = ?, 
                            current_date_registered = CURDATE(),
                            expiry_date = DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
                            status = 'ACTIVE'
                        WHERE registration_id = ?;
                        """);
                updateR.setInt(1, paymentId);
                updateR.setInt(2, chosenId);
                updateR.executeUpdate();
            }

            // fetch data for receipt display
            PreparedStatement ps5 = conn.prepareStatement("""
                SELECT o.first_name, o.last_name, b.branch_name, f.first_name AS off_fn, f.last_name AS off_ln
                FROM owner o
                JOIN branch b ON b.branch_id = ?
                JOIN officer f ON f.officer_id = ?
                WHERE o.owner_id = ?;
            """);
            ps5.setInt(1, branchId);
            ps5.setInt(2, officerId);
            ps5.setInt(3, ownerId);
            ResultSet receiptInfo = ps5.executeQuery();

            String ownerName = "", branchName = "", officerName = "";
            if (receiptInfo.next()) {
                ownerName = receiptInfo.getString("first_name") + " " + receiptInfo.getString("last_name");
                branchName = receiptInfo.getString("branch_name");
                officerName = receiptInfo.getString("off_fn") + " " + receiptInfo.getString("off_ln");
            }

            // display final receipt for user
            System.out.println("\n==================================================");
            System.out.println("                 LTO PAYMENT RECEIPT              ");
            System.out.println("==================================================");
            System.out.println("Receipt no.     : " + nextReceipt);
            System.out.println("Payment ID      : " + paymentId);
            System.out.println("Owner name      : " + ownerName);
            System.out.println("Transaction     : " + transactionDesc);
            if (plateNo != null)
                System.out.println("Plate Number    : " + plateNo);
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

    // send user back to menu
    private void redirectToMenu(Scanner scanner) {
        System.out.println("\nPress Enter to return to the user menu...");
        scanner.nextLine();
        new UserMenu().viewUserMenu();
    }

    // ==============================================================
    // 2. VIEW PAYMENT HISTORY (shows all payments)
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

            // query that shows both violation and registration payments
            String query = """
                SELECT p.payment_id, p.amount_paid, p.date_paid, p.receipt_number,
                       f.first_name AS officer_fn, f.last_name AS officer_ln, b.branch_name,
                       'Violation' AS type
                FROM payment p
                JOIN officer f ON p.officer_id = f.officer_id
                JOIN branch b ON p.branch_id = b.branch_id
                JOIN violation v ON p.payment_id = v.payment_id
                WHERE v.owner_id = ?
                UNION
                SELECT p.payment_id, p.amount_paid, p.date_paid, p.receipt_number,
                       f.first_name AS officer_fn, f.last_name AS officer_ln, b.branch_name,
                       'Registration' AS type
                FROM payment p
                JOIN officer f ON p.officer_id = f.officer_id
                JOIN branch b ON p.branch_id = b.branch_id
                JOIN registration r ON p.payment_id = r.payment_id
                WHERE r.owner_id = ?
                ORDER BY date_paid DESC;
                """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nYour payment records:");
            System.out.println("--------------------------------------------------");
            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                System.out.println("[" + rs.getString("type") + "]");
                System.out.println("Payment ID   : " + rs.getInt("payment_id"));
                System.out.println("Amount Paid  : PHP " + rs.getDouble("amount_paid"));
                System.out.println("Date Paid    : " + rs.getDate("date_paid"));
                System.out.println("Receipt No.  : " + rs.getString("receipt_number"));
                System.out.println("Processed By : " + rs.getString("officer_fn") + " " + rs.getString("officer_ln"));
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
