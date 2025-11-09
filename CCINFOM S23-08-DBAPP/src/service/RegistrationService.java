package service;

import database.DatabaseConnection;
import model.Session;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * RegistrationService
 *
 * Provides vehicle registration flow modeled similar to RenewalService:
 *  - addVehicle(...) inserts a vehicle and returns vehicle_id
 *  - addRegistration(...) inserts a registration and returns registration_id
 *  - registerVehicle(Scanner) is an interactive form with confirm loop
 *  - optional immediate payment + receipt generation
 */
public class RegistrationService {
    private Connection conn;

    public RegistrationService() {
        conn = DatabaseConnection.getConnection();
    }

    // Interactive registration form (confirm loop similar to RenewalService)
    public void registerVehicle(Scanner scanner) {
        BranchDetailsService branchDetails = new BranchDetailsService();

        boolean confirmDetails = false;
        String choice;

        try {
            System.out.println("==================================================");
            System.out.println("            VEHICLE REGISTRATION FORM             ");
            System.out.println("==================================================");

            int ownerId = Session.loggedInOwnerId;
            Integer officerId = null; // will be auto-assigned from branch if possible
            int branchId = 0;
            String plate = null, make = null, series = null, chassis = null, engine = null, color = null;
            int year = LocalDate.now().getYear();
            long mvFileNo = 0L;

            while (!confirmDetails) {
                System.out.print("Plate Number: ");
                plate = scanner.nextLine().trim().toUpperCase();

                System.out.print("Make: ");
                make = scanner.nextLine().trim();

                System.out.print("Series: ");
                series = scanner.nextLine().trim();

                System.out.print("Manufacture Year (YYYY): ");
                year = scanner.nextInt();
                scanner.nextLine();

                System.out.print("MV File No: ");
                mvFileNo = scanner.nextLong();
                scanner.nextLine();

                System.out.print("Chassis No: ");
                chassis = scanner.nextLine().trim();

                System.out.print("Engine No: ");
                engine = scanner.nextLine().trim();

                System.out.print("Color: ");
                color = scanner.nextLine().trim();
                
                System.out.println("List of branches");
                branchDetails.viewAllBranches();

                System.out.print("Branch ID where processed: ");
                branchId = scanner.nextInt();
                scanner.nextLine();

                System.out.println("--------------------------------------------------");
                System.out.println("Please confirm the details below:");
                System.out.println("Owner ID : " + ownerId);
                System.out.println("Plate    : " + plate);
                System.out.println("Make     : " + make + " " + series);
                System.out.println("Year     : " + year);
                System.out.println("Chassis  : " + chassis);
                System.out.println("Engine   : " + engine);
                System.out.println("Color    : " + color);

                do {
                    System.out.print("Enter (Y/N): ");
                    choice = scanner.nextLine();

                    switch (choice) {
                        case "Y" -> {
                            // Attempt to add vehicle and registration. Only set confirmDetails=true on success.

                            // add vehicle
                            int vehicleId = addVehicle(plate, Date.valueOf(LocalDate.of(year,1,1)), mvFileNo, chassis, engine, make, series, color);
                            if (vehicleId == 0) {
                                System.out.println("Vehicle insertion failed (possible duplicate). Please re-enter the information.");
                                break; // re-enter outer form
                            }

                            // automatically pick an officer assigned to the chosen branch (may be null)
                            officerId = getOfficerIdForBranch(branchId);
                            if (officerId == null) {
                                System.out.println("Warning: no officer found for branch " + branchId + ". Registration will have NULL officer_id.");
                            } else {
                                System.out.println("Assigned officer_id: " + officerId);
                            }

                            // add registration
                            int registrationId = addRegistration(vehicleId, ownerId, branchId, officerId);
                            if (registrationId == 0) {
                                System.out.println("Registration insertion failed. Please re-enter the information.");
                                break; // re-enter outer form
                            }

                            // success
                            confirmDetails = true;
                            System.out.println("Registration created (ID: " + registrationId + ").");

                        }
                        case "N" -> System.out.println("Details not confirmed. Please re-enter the information.");
                        default -> System.out.println("Invalid input. Please enter Y or N.");
                    }

                } while (!choice.equalsIgnoreCase("Y") && !choice.equalsIgnoreCase("N"));
            }

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inserts a vehicle row and returns generated vehicle_id
    public int addVehicle(String plate, Date manufactureDate, long mvFileNo, String chassis, String engine, String make, String series, String color) {
        try {
            String sql = "INSERT INTO vehicle (plate_number, manufacture_date, mv_file_no, chassis_no, engine_no, make, series, color) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, plate);
            ps.setDate(2, manufactureDate);
            ps.setLong(3, mvFileNo);
            ps.setString(4, chassis);
            ps.setString(5, engine);
            ps.setString(6, make);
            ps.setString(7, series);
            ps.setString(8, color);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

            // fallback
            PreparedStatement lookup = conn.prepareStatement("SELECT vehicle_id FROM vehicle WHERE plate_number = ?");
            lookup.setString(1, plate);
            ResultSet lr = lookup.executeQuery();
            if (lr.next()) return lr.getInt("vehicle_id");

        } catch (Exception e) {
            System.out.println("Error inserting vehicle: " + e.getMessage());
        }
        return 0;
    }

    // helper: return any officer_id for the branch (first found) or null if none
    private Integer getOfficerIdForBranch(int branchId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT officer_id FROM officer WHERE branch_id = ? LIMIT 1");
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("officer_id");
                if (!rs.wasNull()) return id;
            }
        } catch (SQLException e) {
            System.out.println("Error finding officer for branch: " + e.getMessage());
        }
        return null;
    }

    // Inserts a registration row. officerId can be null to insert a NULL officer_id.
    public int addRegistration(int vehicleId, int ownerId, int branchId, Integer officerId) {
        try {
            String sql = "INSERT INTO registration (vehicle_id, owner_id, branch_id, officer_id, status) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, vehicleId);
            ps.setInt(2, ownerId);
            ps.setInt(3, branchId);
            if (officerId != null) {
                ps.setInt(4, officerId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, "INACTIVE");
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

            // fallback lookup
            PreparedStatement lookup = conn.prepareStatement("SELECT registration_id FROM registration WHERE vehicle_id = ? AND owner_id = ? ORDER BY registration_id DESC LIMIT 1");
            lookup.setInt(1, vehicleId);
            lookup.setInt(2, ownerId);
            ResultSet lr = lookup.executeQuery();
            if (lr.next()) return lr.getInt("registration_id");

        } catch (Exception e) {
            System.out.println("Error inserting registration: " + e.getMessage());
        }
        return 0;
    }

}