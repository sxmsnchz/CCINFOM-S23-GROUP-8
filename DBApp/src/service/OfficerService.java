package service;
import database.DatabaseConnection;
import model.Officer;
import model.Session;

import java.lang.Thread.State;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import view.UserMenu;

public class OfficerService {

    private Connection con;

    public OfficerService() {
        con = DatabaseConnection.getConnection();
    }
    
    /**
     * Return registrations processed by a specific officer.
     * @param officerId the officer_id to filter by
     * @return list of Registration objects (may be empty)
     */
    public List<model.Registration> getRegistrationsByOfficer(int officerId) {
        List<model.Registration> regs = new ArrayList<>();
        if (con == null) return regs;

        String query = "SELECT registration_id, vehicle_id, owner_id, officer_id, first_date_registered, current_date_registered, expiry_date, status "
                   + "FROM registration WHERE officer_id = ? ORDER BY registration_id ASC";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, officerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int regId = rs.getInt("registration_id");
                    int vehicleId = rs.getInt("vehicle_id");
                    int ownerId = rs.getInt("owner_id");
                    java.sql.Date firstDate = rs.getDate("first_date_registered");
                    java.sql.Date currentDate = rs.getDate("current_date_registered");
                    java.sql.Date expiry = rs.getDate("expiry_date");
                    String status = rs.getString("status");

                    model.Registration r = new model.Registration(regId, vehicleId, ownerId, firstDate, currentDate, expiry, status);
                    regs.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to query registrations for officer " + officerId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return regs;
    }


    /**
     * Retrieve all officers from the database.
     * @return list of Officer model objects (may be empty)
     */
    public List<model.Officer> getAllOfficers() {
        List<model.Officer> officers = new ArrayList<>();
        if (con == null) return officers;

        String query = """
            SELECT o.officer_id, o.last_name, o.first_name
            FROM officer o
            ORDER BY last_name
            """;
        try (Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("officer_id");
                String lastName= rs.getString("last_name");
                String firstName = rs.getString("first_name");

                model.Officer off = new model.Officer(id, firstName, lastName);
                officers.add(off);
            }
        } catch(SQLException e) {
            System.err.println("Failed to load officers" + e.getMessage());
            e.printStackTrace();
        }
        return officers;
    }
}
