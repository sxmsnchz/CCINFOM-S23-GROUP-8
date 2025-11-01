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
            System.out.println("         VEHICLE REGISTRATION SYSTEM       ");
            System.out.println("==========================================");
            System.out.println("[1] User Login");
            System.out.println("[2] Officer Login");
            System.out.println("[3] Exit");
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
                    System.out.println("\nThank you! Exiting system... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid option. Please enter 1, 2, or 3.\n");
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

            // check if numeric
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
                // successful login
                Session.loggedInOwnerId = ownerId;
                Session.loggedInRole = "owner";
                System.out.println("\nLogin successful! Welcome, " +
                        rs.getString("first_name") + " " + rs.getString("last_name") + ".");
                new UserMenu().viewUserMenu();
            } else {
                // check if account exists
                PreparedStatement check = conn.prepareStatement(
                        "SELECT * FROM owner WHERE owner_id = ?");
                check.setInt(1, ownerId);
                ResultSet exists = check.executeQuery();

                if (exists.next()) {
                    System.out.println("Incorrect password. Returning to main menu...");
                } else {
                    System.out.print("Account does not exist. Proceed to registration? (Y/N): ");
                    String response = input.nextLine().trim();

                    if (response.equalsIgnoreCase("Y")) {
                        System.out.println("\nRedirecting to registration form...");
                       // RegistrationService regService = new RegistrationService();
                      //  regService.registerNewOwner(input);
                    } else {
                        System.out.println("Returning to main menu...");
                    }
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

            // check if numeric
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
                // successful login
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
}
