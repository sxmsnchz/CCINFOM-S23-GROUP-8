 
package service;
import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Violation;


public class ViolationService {

    private Connection con;

    public ViolationService() {
        con = DatabaseConnection.getConnection();
    }

    /*
     * Records new violations issued by a specific officer
     * 
     */
    public Violation addViolationByOfficer(Violation v) {
        if (con == null)
            return null;
            
        String sql_query = "INSERT INTO Violation " + "(vehicle_id, owner_id, branch_id, officer_id, violation_type, fine_amount, violation_date) " 
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql_query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getVehicleId());
            ps.setInt(2, v.getOwnerId());
            ps.setInt(3, v.getBranchId());
            ps.setInt(4, v.getOfficerId());
            ps.setString(5, v.getViolationType());
            ps.setDouble(6, v.getFineAmount());
            ps.setDate(7, v.getViolationDate());

            int i = ps.executeUpdate();
            if (i == 0)
                throw new SQLException("Insert failed.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    v.setViolationId(rs.getInt(1));
            }
            return v;
        } catch (SQLException e) {
            System.err.println("Failed to insert violation for officer " + v.getOfficerId() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Violation> getAllViolations() {
        List<Violation> list = new ArrayList<>();
        if (con == null)
            return list;

        String sql_query2 = "SELECT violation_id, vehicle_id, owner_id, branch_id, officer_id, " + 
                            "violation_type, fine_amount, violation_date, payment_status " +
                            "FROM Violation " + "ORDER BY violation_date DESC, violation_id DESC";
        try(PreparedStatement ps = con.prepareStatement(sql_query2);
            ResultSet rs = ps.executeQuery()) {
        
            while(rs.next()) {
                int id = rs.getInt("violation_id");
                int vehicleId = rs.getInt("vehicle_id");
                int ownerId = rs.getInt("owner_id");
                int branchId = rs.getInt("branch_id");
                int officerId = rs.getInt("officer_id");
                String violationType = rs.getString("violation_type");
                double fineAmount = rs.getDouble("fine_amount");
                java.sql.Date violationDate = rs.getDate("violation_date");
                String paymentStatus = rs.getString("payment_status");

                Violation v = new Violation(id, vehicleId, ownerId, branchId, officerId, violationType, fineAmount, violationDate, paymentStatus);
                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load violations: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Violation> getViolationsOfOwner(int ownerId) {
        List<Violation> violations = new ArrayList<>();
        if (con == null)
            return violations;

        String sql_query3 = "SELECT violation_id, vehicle_id, owner_id, branch_id, officer_id, " +
                            "violation_type, fine_amount, violation_date, payment_status " +
                            "FROM Violation WHERE owner_id = ? ORDER BY violation_date DESC";
        try (PreparedStatement ps = con.prepareStatement(sql_query3)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("violation_id");
                    int vehicleId = rs.getInt("vehicle_id");
                    int branchId = rs.getInt("branch_id");
                    int officerId = rs.getInt("officer_id");
                    String violationType = rs.getString("violation_type");
                    double fineAmount = rs.getDouble("fine_amount");
                    java.sql.Date violationDate = rs.getDate("violation_date");
                    String paymentStatus = rs.getString("payment_status");

                    Violation v = new Violation(id, vehicleId, ownerId, branchId, officerId, violationType, fineAmount, violationDate, paymentStatus);
                    violations.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch violations for owner " + ownerId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return violations;
    } 
}
