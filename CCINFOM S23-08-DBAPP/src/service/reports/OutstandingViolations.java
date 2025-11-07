package service.reports;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class OutstandingViolations {
    private static final String sqlStatement = """
            SELECT 
                CONCAT(o.first_name, ' ', o.last_name) AS owner,
                DATE_FORMAT(v.violation_date, '%Y-%m') AS period,
                COUNT(*) AS total_violations,
                SUM(v.fine_amount) AS total_fines
            FROM Violation v
            JOIN Owner o ON o.owner_id = v.owner_id
            LEFT JOIN Payment p ON p.payment_id = v.payment_id AND p.payment_type = 'Violation'
            WHERE (v.payment_status = 'Unpaid' OR v.payment_id IS NULL)
            GROUP BY owner, period
            ORDER BY owner ASC, period ASC
            """;
    public void viewOutstandingViolations(Scanner s) {
        System.out.println("==================================================");
        System.out.println("           OUTSTANDING VIOLATIONS REPORT          ");
        System.out.println("==================================================");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
             ResultSet rs = ps.executeQuery()) {
                
            Map<String, Map<String, Integer>> data = new LinkedHashMap<>();

            while(rs.next()) {
                String owner = rs.getString("owner");
                String period = rs.getString("period");
                int total = rs.getInt("total_violations");

                data.computeIfAbsent(owner, k -> new LinkedHashMap<>()).put(period, total);
            }

            if (data.isEmpty()) {
                System.out.println("No outstanding violations found.");
            }
            else {
                System.out.printf("%-32s %-12s %s%n", "Owner", "Month-Year", "Total Violations");
                System.out.println("--------------------------------------------------------------");
                for (var e : data.entrySet()) {
                    String owner = e.getKey();
                    for (var m : e.getValue().entrySet()) {
                        System.out.printf("%-32s %-12s %d%n", owner, m.getKey(), m.getValue());
                    }
                }
            }
            } catch (SQLException ex) {
                System.out.println("Error gathering report. " + ex.getMessage());
            }
            System.out.println("==================================================");
    }
}
