package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/CCINFOM_S23_08_sql";
    private static final String USER = "root";  // MySQL username
    private static final String PASSWORD = "password"; // change to ur MySQL password

    // Print welcome only once per JVM
    private static boolean welcomePrinted = false;

    // to connect to DB
    // Note: this returns a new Connection each call. Do NOT close a shared/global Connection
    // via try-with-resources when other parts of the app also rely on a shared instance.
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (!welcomePrinted) {
                System.out.println("Welcome!");
                welcomePrinted = true;
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // test main method
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
    }
}


