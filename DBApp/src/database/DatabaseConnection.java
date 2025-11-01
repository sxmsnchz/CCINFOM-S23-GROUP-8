package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_registration_system";
    private static final String USER = "root";  // MySQL username
    private static final String PASSWORD = " "; // change to ur own password 

    private static Connection connection = null;

    // to connect to DB
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    // test main method
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
    }
}


