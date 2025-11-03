package service;

import database.DatabaseConnection;
import model.Session;
import java.sql.*;
import java.time.LocalDate;

/**
 * ReceiptService.java
 *
 * This class handles the generation and display of official receipts.
 * It automatically assigns a receipt number (V001, R002, etc.),
 * stores it in the Receipt table, and prints a formatted summary.
 */
public class ReceiptService {

    private Connection conn;

    public ReceiptService() {
        conn = DatabaseConnection.getConnection();
    }


    // Generates a new receipt for a given payment ID.
    public void generateReceipt(int paymentId, double change) {
        try {
            // Retrieve payment details
            String paymentQuery = """
                SELECT p.payment_type, p.amount_paid, p.date_paid,
                       p.owner_id, p.officer_id, p.branch_id,
                       o.first_name AS officer_fn, o.last_name AS officer_ln,
                       b.branch_name
                FROM payment p
                JOIN officer o ON p.officer_id = o.officer_id
                JOIN branch b ON p.branch_id = b.branch_id
                WHERE p.payment_id = ?;
            """;

            PreparedStatement ps = conn.prepareStatement(paymentQuery);
            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Error: Payment not found.");
                return;
            }

            // Extract payment info
            String paymentType = rs.getString("payment_type");
            double amount = rs.getDouble("amount_paid");
            Date datePaid = rs.getDate("date_paid");
            String officerLast = rs.getString("officer_ln");
            String officerFirst = rs.getString("officer_fn");
            String officerName = officerLast + ", " + officerFirst; // formatted Last, First
            String branchName = rs.getString("branch_name");

            // Determine prefix (V for Violation, R for Registration)
            String prefix = paymentType.equalsIgnoreCase("Violation") ? "V" : "R";

            // Get the last used receipt number with the same prefix
            String lastReceipt = getLastReceiptNumber(prefix);
            String nextReceipt = generateNextReceipt(lastReceipt, prefix);

            // Insert into Receipt table
            String insertReceipt = """
                INSERT INTO receipt (payment_id, receipt_number, issue_date, printed_by)
                VALUES (?, ?, ?, ?);
            """;

            PreparedStatement ps2 = conn.prepareStatement(insertReceipt);
            ps2.setInt(1, paymentId);
            ps2.setString(2, nextReceipt);
            ps2.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps2.setString(4, officerName);
            ps2.executeUpdate();

            // Display receipt to user
            System.out.println("\n==================================================");
            System.out.println("              LTO OFFICIAL RECEIPT                ");
            System.out.println("==================================================");
            System.out.println("Receipt Number  : " + nextReceipt);
            System.out.println("Transaction     : " + paymentType);
            System.out.println("Amount Paid     : PHP " + String.format("%.2f", amount));
            System.out.println("Date Issued     : " + LocalDate.now());
            System.out.println("Processed By    : " + officerName);
            System.out.println("Branch          : " + branchName);
            if (change > 0)
                System.out.println("Change Given    : PHP " + String.format("%.2f", change));
            System.out.println("==================================================");
            System.out.println("          Thank you for your payment!             ");
            System.out.println("==================================================\n");

        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }

    // Helper: fetch the last used receipt number with same prefix
    private String getLastReceiptNumber(String prefix) throws SQLException {
        String query = "SELECT receipt_number FROM receipt WHERE receipt_number LIKE ? ORDER BY receipt_id DESC LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, prefix + "%");
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("receipt_number");
        }
        return null;
    }

    // Helper: generate the next receipt number in sequence (e.g., R001 → R002)
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


    //Displays all receipts belonging to the current logged in user
    // Sorted by most recent issue date
    public void viewReceipts() {
        try {
            int ownerId = Session.loggedInOwnerId;

            String query = """
                SELECT r.receipt_number, r.issue_date, r.printed_by,
                       p.payment_type, p.amount_paid,
                       b.branch_name
                FROM receipt r
                JOIN payment p ON r.payment_id = p.payment_id
                JOIN branch b ON p.branch_id = b.branch_id
                WHERE p.owner_id = ?
                ORDER BY r.issue_date DESC;
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n==================================================");
            System.out.println("                  YOUR RECEIPTS                   ");
            System.out.println("==================================================");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println("[" + rs.getString("payment_type") + "]");
                System.out.println("Receipt Number : " + rs.getString("receipt_number"));
                System.out.println("Amount Paid    : PHP " + String.format("%.2f", rs.getDouble("amount_paid")));
                System.out.println("Branch         : " + rs.getString("branch_name"));
                System.out.println("Processed By   : " + rs.getString("printed_by"));
                System.out.println("Date Issued    : " + rs.getDate("issue_date"));
                System.out.println("--------------------------------------------------");
            }

            if (!hasResults) {
                System.out.println("You haven't made any payments yet.");
            }

        } catch (Exception e) {
            System.out.println("Error displaying receipts: " + e.getMessage());
        }
    }
}
