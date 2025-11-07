package service;

import database.DatabaseConnection;
import model.Session;
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
 * and updates the records accordingly.
 */
public class PaymentService {

    private Connection conn; // connection for MySQL database

    // connects to our database
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

            int ownerId = Session.loggedInOwnerId; // current logged-in user

            // check if no user logged in
            if (ownerId == 0) {
                System.out.println("Error: No user logged in. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            // SQL query to properly check renewals using the renewal table
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
                  AND (v.payment_status = 'Unpaid' OR v.payment_id IS NULL)  -- backup condition for missing payment_id

                UNION

                SELECT
                    'Registration' AS type,
                    r.registration_id AS id,
                    v.plate_number AS plate_number,
                    CASE
                        -- Defensive triple check for new registration
                        WHEN (r.first_date_registered IS NULL
                           OR r.payment_id IS NULL
                           OR r.expiry_date IS NULL)
                        THEN 'New Registration'

                        -- Renewal logic: expired but not renewed this year
                        WHEN (r.first_date_registered IS NOT NULL
                           AND r.expiry_date < CURDATE()
                           AND NOT EXISTS (
                               SELECT 1 FROM renewal re
                               WHERE re.registration_id = r.registration_id
                               AND YEAR(re.last_renewal_date) = YEAR(CURDATE())
                           ))
                        THEN 'Renewal'
                    END AS description,

                    CASE
                        WHEN (r.first_date_registered IS NULL
                           OR r.payment_id IS NULL
                           OR r.expiry_date IS NULL)
                        THEN 7410
                        WHEN (r.first_date_registered IS NOT NULL
                           AND r.expiry_date < CURDATE()
                           AND NOT EXISTS (
                               SELECT 1 FROM renewal re
                               WHERE re.registration_id = r.registration_id
                               AND YEAR(re.last_renewal_date) = YEAR(CURDATE())
                           ))
                        THEN 1500
                        ELSE 0
                    END AS amount
                FROM registration r
                JOIN vehicle v ON r.vehicle_id = v.vehicle_id
                WHERE r.owner_id = ?
                AND (
                    (r.first_date_registered IS NULL
                     OR r.payment_id IS NULL
                     OR r.expiry_date IS NULL)  -- new registration
                    OR (r.first_date_registered IS NOT NULL
                        AND r.expiry_date < CURDATE()
                        AND NOT EXISTS (
                            SELECT 1 FROM renewal re
                            WHERE re.registration_id = r.registration_id
                            AND YEAR(re.last_renewal_date) = YEAR(CURDATE())
                        )
                    )
                );
            """;

            // create prepared statement
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nUnpaid Transactions:");
            System.out.println("--------------------------------------------------");
            boolean hasUnpaid = false;

            while (rs.next()) {
                hasUnpaid = true;
                System.out.println("[" + rs.getString("type") + "] ID: " + rs.getInt("id") +
                        " | Plate No: " + rs.getString("plate_number") +
                        " | " + rs.getString("description") +
                        " | Amount: Php " + rs.getDouble("amount"));
            }

            // If no unpaid transactions found, go back to menu
            if (!hasUnpaid) {
                System.out.println("You have no unpaid violations or registrations.");
                redirectToMenu(scanner);
                return;
            }

            // ask user which type and ID to pay
            System.out.println("--------------------------------------------------");
            System.out.print("Enter transaction type (Violation / Registration): ");
            String chosenType = scanner.nextLine().trim();

            // Validate type
            if (!chosenType.equalsIgnoreCase("Violation") && !chosenType.equalsIgnoreCase("Registration")) {
                System.out.println("Invalid type. Redirecting...");
                redirectToMenu(scanner);
                return;
            }

            System.out.print("Enter ID to pay: "); // Ask for ID to pay
            String inputTid = scanner.nextLine().trim();
            if (!inputTid.matches("\\d+")) { // must be numeric
                System.out.println("Invalid ID format.");
                redirectToMenu(scanner);
                return;
            }

            int chosenId = Integer.parseInt(inputTid);

            // initialize variables to store details of the payment
            double amount = 0;
            int branchId = 0;
            int officerId = 0;
            String plateNo = null;
            String transactionDesc = "";
            String paymentType = "";

            // retrieve specific details based on transaction type
            if (chosenType.equalsIgnoreCase("Violation")) {
                // Query violation details for the given violation_id
                PreparedStatement ps2 = conn.prepareStatement("""
                    SELECT fine_amount, branch_id, officer_id, v.vehicle_id, ve.plate_number, v.violation_type
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

                // Extract data for payment
                amount = rs2.getDouble("fine_amount");
                branchId = rs2.getInt("branch_id");
                officerId = rs2.getInt("officer_id");
                plateNo = rs2.getString("plate_number");
                transactionDesc = rs2.getString("violation_type");
                paymentType = "Violation";

            // handle registration or renewal
            } else {
                PreparedStatement ps3 = conn.prepareStatement("""
                    SELECT r.branch_id, r.officer_id, r.vehicle_id, r.payment_id,
                           r.expiry_date, r.first_date_registered, v.plate_number
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
                plateNo = rs3.getString("plate_number");
                int prevPay = rs3.getInt("payment_id");
                Date expiry = rs3.getDate("expiry_date");
                Date firstReg = rs3.getDate("first_date_registered");

                // Determine whether it’s a new registration or renewal
                if ((firstReg == null || prevPay == 0 || expiry == null)) {
                    transactionDesc = "New Registration";
                    paymentType = "Registration";
                    amount = 7410.00;
                } else if (expiry != null && expiry.before(new java.util.Date())) {
                    transactionDesc = "Renewal";
                    paymentType = "Renewal";
                    amount = 1500.00;
                } else {
                    System.out.println("This registration is not due for payment.");
                    redirectToMenu(scanner);
                    return;
                }
            }

            // confirm payment
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

            // Calculate change (if paid more than amount)
            double change = userPayment > amount ? userPayment - amount : 0;
            System.out.println("Payment accepted. Processing...");

            // insert payment record
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

            // Retrieve auto-generated payment_id
            ResultSet genKeys = ps4.getGeneratedKeys();
            int paymentId = 0;
            if (genKeys.next()) {
                paymentId = genKeys.getInt(1);
            }

            // Automatically generate receipt after successful payment
            ReceiptService receiptService = new ReceiptService();
            receiptService.generateReceipt(paymentId, change);

            if (chosenType.equalsIgnoreCase("Violation")) {
                // violation payment — mark cleared
                PreparedStatement updateV = conn.prepareStatement(
                    "UPDATE violation SET payment_status = 'Cleared', payment_id = ? WHERE violation_id = ?;"
                );
                updateV.setInt(1, paymentId);
                updateV.setInt(2, chosenId);
                updateV.executeUpdate();

            } else {
                if (paymentType.equalsIgnoreCase("Registration")) {
                    // new registration — set both first and current date registered
                    PreparedStatement updateR = conn.prepareStatement("""
                        UPDATE registration
                        SET payment_id = ?,
                            first_date_registered = CURDATE(),
                            current_date_registered = CURDATE(),
                            expiry_date = DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
                            status = 'ACTIVE'
                        WHERE registration_id = ?;
                    """);
                    updateR.setInt(1, paymentId);
                    updateR.setInt(2, chosenId);
                    updateR.executeUpdate();

                } else if (paymentType.equalsIgnoreCase("Renewal")) {
                    // renewal — insert record in renewal table instead of updating registration payment_id
                    PreparedStatement insertRenewal = conn.prepareStatement("""
                        INSERT INTO renewal (registration_id, payment_id, branch_id, officer_id, last_renewal_date)
                        VALUES (?, ?, ?, ?, CURDATE());
                    """);
                    insertRenewal.setInt(1, chosenId);
                    insertRenewal.setInt(2, paymentId);
                    insertRenewal.setInt(3, branchId);
                    insertRenewal.setInt(4, officerId);
                    insertRenewal.executeUpdate();

                    // also update registration’s status and new expiry
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
            }

            System.out.println("Payment successfully recorded. Receipt will be generated separately.");

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
}
