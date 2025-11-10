package service;

import database.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import model.Session;
import view.UserMenu;

/**
 * PaymentService.java
 *
 * Handles all payment-related functions for the Vehicle Registration System.
 * Allows users to:
 *   1. Settle unpaid transactions (violations, registration, or renewal)
 *   2. View complete payment history (optional future feature)
 *
 * Now supports renewal detection up to 60 days before expiry.
 */
public class PaymentService {

    private Connection conn;

    public PaymentService() {
        conn = DatabaseConnection.getConnection();
    }

    // =======================================================================================
    // 1. SETTLE PAYMENT (used by user to pay for unpaid violations or registrations/renewals)
    // =======================================================================================
    public void settlePayment(Scanner scanner) {
        try {
            System.out.println("--------------------------------------------------");
            System.out.println("                 SETTLE PAYMENT                   ");
            System.out.println("--------------------------------------------------");

            int ownerId = Session.loggedInOwnerId;

            if (ownerId == 0) {
                System.out.println("Error: No user logged in. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            // Updated SQL query: includes renewals 60 days before expiry
            String query = """
                SELECT
                    'Violation' AS type,
                    v.violation_id AS id,
                    ve.plate_number AS plate_number,
                    v.violation_type AS description,
                    v.fine_amount AS amount
                FROM violation v
                JOIN vehicle ve ON v.vehicle_id = ve.vehicle_id
                WHERE v.owner_id = ?
                  AND (v.payment_status = 'Unpaid' OR v.payment_id IS NULL)

                UNION

                SELECT
                    CASE
                        WHEN (r.first_date_registered IS NULL
                           OR r.payment_id IS NULL
                           OR r.expiry_date IS NULL)
                        THEN 'Registration'
                        ELSE 'Renewal'
                    END AS type,
                    r.registration_id AS id,
                    v.plate_number AS plate_number,
                    CASE
                        WHEN (r.first_date_registered IS NULL
                           OR r.payment_id IS NULL
                           OR r.expiry_date IS NULL)
                        THEN 'New Registration'
                        ELSE 
                            CASE 
                                WHEN r.expiry_date < CURDATE() THEN 'Renewal (Expired)'
                                ELSE 'Renewal (Expiring Soon)'
                            END
                    END AS description,
                    CASE
                        WHEN (r.first_date_registered IS NULL
                           OR r.payment_id IS NULL
                           OR r.expiry_date IS NULL)
                        THEN 7410
                        ELSE 1500
                    END AS amount
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                WHERE r.owner_id = ?
                AND (
                    (r.first_date_registered IS NULL
                     OR r.payment_id IS NULL
                     OR r.expiry_date IS NULL)
                    OR (r.first_date_registered IS NOT NULL
                        AND r.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 60 DAY)
                        AND NOT EXISTS (
                            SELECT 1 FROM renewal re
                            WHERE re.registration_id = r.registration_id
                            AND YEAR(re.last_renewal_date) = YEAR(CURDATE())
                        )
                    )
                );
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nUnpaid Transactions:");
            System.out.println("--------------------------------------------------");
            boolean hasUnpaid = false;

            while (rs.next()) {
                hasUnpaid = true;
                String type = rs.getString("type");
                int id = rs.getInt("id");
                System.out.println("[" + type + "] " +
                        (type.equalsIgnoreCase("Renewal") ? "Renewal ID: " : "Registration ID: ") + id +
                        " | Plate No: " + rs.getString("plate_number") +
                        " | " + rs.getString("description") +
                        " | Amount: Php " + rs.getDouble("amount"));
            }

            if (!hasUnpaid) {
                System.out.println("You have no unpaid violations or registrations.");
                redirectToMenu(scanner);
                return;
            }

            System.out.println("--------------------------------------------------");
            System.out.print("Enter transaction type (Violation / Registration / Renewal): ");
            String chosenType = scanner.nextLine().trim();

            if (!chosenType.equalsIgnoreCase("Violation") &&
                !chosenType.equalsIgnoreCase("Registration") &&
                !chosenType.equalsIgnoreCase("Renewal")) {
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

            double amount = 0;
            int branchId = 0;
            int officerId = 0;
            String paymentType = chosenType;

            if (chosenType.equalsIgnoreCase("Violation")) {
                PreparedStatement ps2 = conn.prepareStatement("""
                    SELECT fine_amount, branch_id, officer_id
                    FROM violation
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

            } else {
                PreparedStatement ps3 = conn.prepareStatement("""
                    SELECT branch_id, officer_id, payment_id, expiry_date, first_date_registered
                    FROM registration
                    WHERE registration_id = ?;
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
                int prevPay = rs3.getInt("payment_id");
                Date expiry = rs3.getDate("expiry_date");
                Date firstReg = rs3.getDate("first_date_registered");

                // Detect if new registration or renewal (including within 60 days)
                if ((firstReg == null || prevPay == 0 || expiry == null)) {
                    paymentType = "Registration";
                    amount = 7410.00;
                } else if (expiry != null &&
                          (expiry.before(new java.util.Date()) ||
                           expiry.before(java.sql.Date.valueOf(java.time.LocalDate.now().plusDays(60))))) {
                    paymentType = "Renewal";
                    amount = 1500.00;
                } else {
                    System.out.println("This registration is not due for payment.");
                    redirectToMenu(scanner);
                    return;
                }
            }

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

            PreparedStatement ps4 = conn.prepareStatement("""
                INSERT INTO payment (officer_id, branch_id, owner_id, payment_type, amount_paid, date_paid)
                VALUES (?, ?, ?, ?, ?, ?);
            """, Statement.RETURN_GENERATED_KEYS);

            ps4.setInt(1, officerId);
            ps4.setInt(2, branchId);
            ps4.setInt(3, ownerId);
            ps4.setString(4, paymentType);
            ps4.setDouble(5, amount);
            ps4.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps4.executeUpdate();

            ResultSet genKeys = ps4.getGeneratedKeys();
            int paymentId = 0;
            if (genKeys.next()) {
                paymentId = genKeys.getInt(1);
            }

            ReceiptService receiptService = new ReceiptService();
            receiptService.generateReceipt(paymentId, change);

            if (chosenType.equalsIgnoreCase("Violation")) {
                PreparedStatement updateV = conn.prepareStatement(
                    "UPDATE violation SET payment_status = 'Cleared', payment_id = ? WHERE violation_id = ?;"
                );
                updateV.setInt(1, paymentId);
                updateV.setInt(2, chosenId);
                updateV.executeUpdate();

            } else if (paymentType.equalsIgnoreCase("Registration")) {
                PreparedStatement updateR = conn.prepareStatement("""
                    UPDATE registration
                    SET payment_id = ?,
                        first_date_registered = CURDATE(),
                        current_date_registered = CURDATE(),
                        expiry_date = DATE_ADD(CURDATE(), INTERVAL 3 YEAR),
                        status = 'ACTIVE'
                    WHERE registration_id = ?;
                """);
                updateR.setInt(1, paymentId);
                updateR.setInt(2, chosenId);
                updateR.executeUpdate();

            } else if (paymentType.equalsIgnoreCase("Renewal")) {
                PreparedStatement updateRenewal = conn.prepareStatement("""
                    UPDATE renewal
                    SET last_renewal_date = CURDATE(),
                        payment_id = ?
                    WHERE registration_id = ?;
                """);
                updateRenewal.setInt(1, paymentId);
                updateRenewal.setInt(2, chosenId);
                updateRenewal.executeUpdate();

                PreparedStatement updateStatus = conn.prepareStatement("""
                    UPDATE registration
                    SET current_date_registered = CURDATE(),
                        expiry_date = DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
                        status = 'ACTIVE'
                    WHERE registration_id = ?;
                """);
                updateStatus.setInt(1, chosenId);
                updateStatus.executeUpdate();
            }

            System.out.println("Payment successfully recorded. Receipt generated.");

        } catch (Exception e) {
            System.out.println("Error settling payment: " + e.getMessage());
        }
    }

    private void redirectToMenu(Scanner scanner) {
        System.out.println("\nPress Enter to return to the user menu...");
        scanner.nextLine();
        new UserMenu().viewUserMenu();
    }
}
