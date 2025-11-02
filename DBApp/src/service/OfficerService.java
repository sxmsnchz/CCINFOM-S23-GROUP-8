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
