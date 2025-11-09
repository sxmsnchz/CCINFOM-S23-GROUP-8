package view;

import java.sql.*;
import java.util.Scanner;
import database.DatabaseConnection;
import model.Session;

public class VehicleRegistrationHome {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("==========================================");
            System.out.println("         VEHICLE REGISTRATION SYSTEM      ");
            System.out.println("==========================================");
            System.out.println("[1] User Login");
            System.out.println("[2] Officer Login");
            System.out.println("[3] User Sign Up");
            System.out.println("[4] Exit");
            System.out.println("==========================================");
            System.out.print("Select an option: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    userLogin(input);
                    break;
                case "2":
                    officerLogin(input);
                    break;
                case "3":
                    userSignUp(input);
                    break;
                case "4":
                    System.out.println("\nThank you! Exiting system... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid option. Please enter 1, 2, 3, or 4.\n");
            }
        }
        input.close();
        System.exit(0);
    }

    // -------------------------------------------
    // USER LOGIN
    // -------------------------------------------
    private static void userLogin(Scanner input) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            System.out.println("\n========== USER LOGIN ==========");
            System.out.print("Enter Owner ID: ");
            String ownerIdInput = input.nextLine().trim();

            if (!ownerIdInput.matches("\\d+")) {
                System.out.println("Invalid ID format. Returning to main menu...");
                return;
            }

            int ownerId = Integer.parseInt(ownerIdInput);
            System.out.print("Enter Password: ");
            String password = input.nextLine().trim();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM owner WHERE owner_id = ? AND password = ?");
            ps.setInt(1, ownerId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.loggedInOwnerId = ownerId;
                Session.loggedInRole = "owner";
                System.out.println("\nLogin successful! Welcome, " +
                        rs.getString("first_name") + " " + rs.getString("last_name") + ".");
                new UserMenu().viewUserMenu();
            } else {
                PreparedStatement check = conn.prepareStatement(
                        "SELECT * FROM owner WHERE owner_id = ?");
                check.setInt(1, ownerId);
                ResultSet exists = check.executeQuery();

                if (exists.next()) {
                    System.out.println("Incorrect password. Returning to main menu...");
                } else {
                    System.out.println("Account does not exist. Please proceed to account sign up.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error during user login: " + e.getMessage());
        }
    }

    // -------------------------------------------
    // OFFICER LOGIN
    // -------------------------------------------
    private static void officerLogin(Scanner input) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            System.out.println("\n========== OFFICER LOGIN ==========");
            System.out.print("Enter Officer ID: ");
            String officerIdInput = input.nextLine().trim();

            if (!officerIdInput.matches("\\d+")) {
                System.out.println("Invalid ID format. Returning to main menu...");
                return;
            }

            int officerId = Integer.parseInt(officerIdInput);
            System.out.print("Enter Password: ");
            String password = input.nextLine().trim();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM officer WHERE officer_id = ? AND password = ?");
            ps.setInt(1, officerId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.loggedInOfficerId = officerId;
                Session.loggedInRole = "officer";
                System.out.println("\nLogin successful! Welcome Officer " +
                        rs.getString("first_name") + " " + rs.getString("last_name") + ".");
                new OfficerMenu().viewOfficerMenu();
            } else {
                System.out.println("Invalid credentials. Returning to main menu...");
            }
        } catch (Exception e) {
            System.out.println("Error during officer login: " + e.getMessage());
        }
    }

    // -------------------------------------------
    // USER SIGN-UP (with validation)
    // -------------------------------------------
    private static void userSignUp(Scanner input) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            System.out.println("\n========== USER SIGN UP ==========");
            System.out.print("First Name: ");
            String firstName = input.nextLine().trim();
            if (!firstName.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid. Letters only.");
                return;
            }

            System.out.print("Last Name: ");
            String lastName = input.nextLine().trim();
            if (!lastName.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid. Letters only.");
                return;
            }

            System.out.print("Street: ");
            String street = input.nextLine().trim();
            if (street.isEmpty()) {
                System.out.println("Invalid. Cannot be empty.");
                return;
            }

            System.out.print("Barangay: ");
            String barangay = input.nextLine().trim();
            if (!barangay.matches("^[A-Za-z0-9 ]+$")) {
                System.out.println("Invalid. Letters and numbers only.");
                return;
            }

            System.out.print("City: ");
            String city = input.nextLine().trim();
            if (!city.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid. Letters only.");
                return;
            }

            System.out.print("Province: ");
            String province = input.nextLine().trim();
            if (!province.matches("^[A-Za-z ]+$")) {
                System.out.println("Invalid. Letters only.");
                return;
            }

            System.out.print("Region: ");
            String region = input.nextLine().trim();
            if (!region.matches("^[A-Za-z0-9 ]+$")) {
                System.out.println("Invalid. Letters and numbers only.");
                return;
            }

            System.out.print("Postal Code (4 digits): ");
            String postalCode = input.nextLine().trim();
            if (!postalCode.matches("^\\d{4}$")) {
                System.out.println("Invalid. 4 digits only.");
                return;
            }

            System.out.print("Password (min 8 chars): ");
            String password = input.nextLine().trim();
            if (password.length() < 8) {
                System.out.println("Invalid. Must be at least 8 characters.");
                return;
            }

            System.out.print("License Number (format A00-00-000000): ");
            String license = input.nextLine().trim();
            if (!license.matches("^[A-Z][0-9]{2}-[0-9]{2}-[0-9]{6}$")) {
                System.out.println("Invalid. Must follow format A00-00-000000.");
                return;
            }

            // Auto-format capitalization
            firstName = capitalizeWords(firstName);
            lastName = capitalizeWords(lastName);
            street = capitalizeWords(street);
            barangay = capitalizeWords(barangay);
            city = capitalizeWords(city);
            province = capitalizeWords(province);
            region = capitalizeWords(region);
            if (!city.toLowerCase().contains("city")) city += " City";
            if (province.equalsIgnoreCase("NCR")) {
                province = "Metro Manila";
            }


            // Insert into DB
            String query = """
                INSERT INTO owner
                (first_name, last_name, street, barangay, city, province, region, postal_code, password, license_number)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, street);
            ps.setString(4, barangay);
            ps.setString(5, city);
            ps.setString(6, province);
            ps.setString(7, region);
            ps.setString(8, postalCode);
            ps.setString(9, password);
            ps.setString(10, license);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newOwnerId = rs.getInt(1);
                    System.out.println("\nAccount successfully created!");
                    System.out.println("Your assigned Owner ID is: " + newOwnerId);
                }
            }

            System.out.println("\nReturning to main menu...\n");

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("license_number"))
                System.out.println("Error: License number already exists in the system.");
            else
                System.out.println("Duplicate entry found. Please check your inputs.");
        } catch (Exception e) {
            System.out.println("Error during user sign-up: " + e.getMessage());
        }
    }

    // Capitalization helper
    private static String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
