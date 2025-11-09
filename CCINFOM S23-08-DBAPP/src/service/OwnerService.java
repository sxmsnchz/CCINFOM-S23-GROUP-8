package service;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Owner;
import model.Vehicle;
import model.Registration;
import model.Violation;


public class OwnerService {

    private Connection con;

    public OwnerService() {
        con = DatabaseConnection.getConnection();
    }

    public void viewOwnerDetails(int ownerId) {
        if (con == null) {
            System.out.println("Database connection failed.");
            return;
        }

        Owner owner = getOwnerById(ownerId);
        if (owner == null) {
            System.out.println("No owner found with ID " + ownerId);
            return;
        }

        printOwnerHeader(owner);

        List<Vehicle> vehicles = getVehiclesOfOwner(ownerId);
        printVehicles(vehicles);

        List<Registration> registrations = getRegistrationsOfOwner(ownerId);
        printRegistrations(registrations);

        List<Violation> violations = getViolationsByOwner(ownerId);
        printViolations(violations);
    }

    public void viewOwnerByLicenseDetails(String licenseNumber) {
        if (con == null) {
            System.out.println("Database connection failed.");
            return;
        }

        Owner owner = getOwnerByLicense(licenseNumber);
        if (owner == null) {
            System.out.println("No owner found with License " + licenseNumber);
            return;
        }

        viewOwnerDetails(owner.getOwnerId());
    }

    public Owner getOwnerById(int ownerId) {
        if (con == null)
            return null;
        
        String sql = "SELECT owner_id, first_name, last_name, street, barangay, city, " +
                     "province, region, postal_code, password, license_number " +
                     "FROM Owner WHERE owner_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;

                Owner o = new Owner();
                o.setOwnerId(rs.getInt("owner_id"));
                o.setFirstName(rs.getString("first_name"));
                o.setLastName(rs.getString("last_name"));
                o.setStreet(rs.getString("street"));
                o.setBarangay(rs.getString("barangay"));
                o.setCity(rs.getString("city"));
                o.setProvince(rs.getString("province"));
                o.setRegion(rs.getString("region"));
                o.setPostalCode(rs.getInt("postal_code"));
                o.setPassword(rs.getString("password"));
                o.setLicenseNumber(rs.getString("license_number"));
                return o;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching owner " + e.getMessage());
            return null;
        }
    }

    public Owner getOwnerByLicense(String licenseNumber) {
        if (con == null)
            return null;

            String sql = "SELECT owner_id, first_name, last_name, street, barangay, city, " +
                         "province, region, postal_code, password, license_number " +
                         "FROM Owner WHERE license_number = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, licenseNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        return null;

                        Owner o = new Owner();
                        o.setOwnerId(rs.getInt("owner_id"));
                        o.setFirstName(rs.getString("first_name"));
                        o.setLastName(rs.getString("last_name"));
                        o.setStreet(rs.getString("street"));
                        o.setBarangay(rs.getString("barangay"));
                        o.setCity(rs.getString("city"));
                        o.setProvince(rs.getString("province"));
                        o.setRegion(rs.getString("region"));
                        o.setPostalCode(rs.getInt("postal_code"));
                        o.setPassword(rs.getString("password"));
                        o.setLicenseNumber(rs.getString("license_number"));
                        return o;
                }
            } catch (SQLException e) {
                System.out.println("Error fetching owner by license " + e.getMessage());
                return null;
            }
    }

    public List<Vehicle> getVehiclesOfOwner(int ownerId) {
        List<Vehicle> list = new ArrayList<>();
        if (con == null)
            return list;

            String sql = "SELECT DISTINCT v.vehicle_id, v.plate_number, v.manufacture_date, v.mv_file_no, " +
                         "v.chassis_no, v.engine_no, v.make, v.series, v.color " +
                         "FROM Vehicle v " +
                         "JOIN Registration r ON r.vehicle_id = v.vehicle_id " +
                         "WHERE r.owner_id = ? " +
                         "ORDER BY v.vehicle_id ASC";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, ownerId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Vehicle v = new Vehicle();
                        v.setVehicleId(rs.getInt("vehicle_id"));
                        v.setPlateNumber(rs.getString("plate_number"));
                        v.setManufactureDate(rs.getDate("manufacture_date"));
                        v.setMvFileNo(rs.getLong("mv_file_no"));
                        v.setChassisNo(rs.getString("chassis_no"));
                        v.setEngineNo(rs.getString("engine_no"));
                        v.setMake(rs.getString("make"));
                        v.setSeries(rs.getString("series"));
                        v.setColor(rs.getString("color"));
                        list.add(v);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving vehicles " + e.getMessage());
            }
            return list;
    }

    public List<Registration> getRegistrationsOfOwner(int ownerId) {
        List<Registration> list = new ArrayList<>();
        if (con == null)
            return list;

        String sql = "SELECT registration_id, vehicle_id, payment_id, officer_id, " +
                     "first_date_registered, current_date_registered, expiry_date, status " +
                     "FROM Registration " +
                     "WHERE owner_id = ? " +
                     "ORDER BY current_date_registered DESC, registration_id DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Registration r = new Registration();
                    r.setRegistrationId(rs.getInt("registration_id"));
                    r.setVehicleId(rs.getInt("vehicle_id"));
                    r.setOfficerId(rs.getInt("officer_id"));
                    r.setFirstDateRegistered(rs.getDate("first_date_registered"));
                    r.setCurrentDateRegistered(rs.getDate("current_date_registered"));
                    r.setExpiryDate(rs.getDate("expiry_date"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                } 
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving registrations " + e.getMessage());
        }
        return list;
    } 

    public List<Violation> getViolationsByOwner(int ownerId) {
        List<Violation> list = new ArrayList<>();
        if (con == null)
            return list;

        String sql = "SELECT violation_id, owner_id, vehicle_id, officer_id, branch_id, " +
                     "violation_type, fine_amount, violation_date, payment_status, payment_id " +
                     "FROM Violation " +
                     "WHERE owner_id = ? " +
                     "ORDER BY violation_date DESC, violation_id DESC";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Violation v = new Violation();
                    v.setViolationId(rs.getInt("violation_id"));
                    v.setOwnerId(rs.getInt("owner_id"));
                    v.setVehicleId(rs.getInt("vehicle_id"));
                    v.setOfficerId(rs.getInt("officer_id"));
                    v.setBranchId(rs.getInt("branch_id"));
                    v.setViolationType(rs.getString("violation_type"));
                    v.setFineAmount(rs.getDouble("fine_amount"));
                    v.setViolationDate(rs.getDate("violation_date"));
                    v.setPaymentStatus(rs.getString("payment_status"));

                    list.add(v);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving violations " + e.getMessage());
        }
        return list;
    } 

    private void printOwnerHeader(Owner o) {
        System.out.println("\n================ OWNER DETAILS =================");
        System.out.printf("ID: %d%n", o.getOwnerId());
        System.out.printf("Name: %s %s%n", o.getFirstName(), o.getLastName());
        System.out.printf("License: %s%n", o.getLicenseNumber());
        System.out.printf("Address: %s %s %s (Postal: %s)%n", o.getCity(), o.getProvince(), o.getRegion(), o.getPostalCode());
        System.out.println();
    }

    private void printVehicles(List<Vehicle> vehicles) {
        System.out.println("--------------- VEHICLES OWNED ----------------");
        if (vehicles == null || vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }
        System.out.printf("%-10s %-10s %-12s %-12s %-10s%n", "VehicleID", "Plate", "Make", "Series", "Color");
        for (Vehicle v : vehicles) {
            System.out.printf("%-10s %-10s %-12s %-12s %-10s%n", v.getVehicleId(), v.getPlateNumber(), v.getMake(), v.getSeries(), v.getColor());
        }
        System.out.println();
    }

    private void printViolations(List<Violation> vio) {
        System.out.println("------------------ VIOLATIONS ------------------");
        if (vio == null || vio.isEmpty()) {
            System.out.println("No violations found.");
            return;
        }
        System.out.printf("%-12s %-10s %-10s %-10s %-22s %-12s %-10s%n", "ViolationID", "VehicleID", "OfficerID", "BranchID", "Type", "Date", "Status");
        for (Violation v : vio) {
            String type = (v.getViolationType() == null ? "-" : v.getViolationType());
            String date = (v.getViolationDate() == null ? "-" : v.getViolationDate().toString());
            String status = (v.getPaymentStatus() == null ? "-" : v.getPaymentStatus());

            System.out.printf("%-12d %-10d %-10d %-10d %-22s %-12s %-10s%n", v.getViolationId(), v.getVehicleId(), v.getOfficerId(), v.getBranchId(), type, date, status);
        }
        System.out.println();
    }

    private void printRegistrations(List<Registration> regs) {
        System.out.println("----------------- REGISTRATIONS ----------------");
        if (regs == null || regs.isEmpty()) {
            System.out.println("No registrations found.");
            System.out.println();
            return;
        }

        System.out.printf("%-14s %-10s %-10s %-12s %-12s %-8s%n", "RegistrationID", "VehicleID", "OfficerID", "Current Date", "Expiry Date", "Status");

        for (Registration r : regs) {
            String current = r.getCurrentDateRegistered() == null ? "-" : r.getCurrentDateRegistered().toString();
            String expiry = r.getExpiryDate() == null ? "-" : r.getExpiryDate().toString();

            System.out.printf("%-14d %-10d %-10d %-12s %-12s %-8s%n", r.getRegistrationId(), r.getVehicleId(), r.getOfficerId(), current, expiry, r.getStatus());
        }

        System.out.println();
    }
}
