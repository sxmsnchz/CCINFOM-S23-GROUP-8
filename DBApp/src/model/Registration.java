package model;
import java.sql.Date;

public class Registration {
    private int registrationId; //primary key
    private int vehicleId; //foreign key to vehicle
    private int ownerId; //foreign key to owner
    private int officerId; //foreign key to officer
    private Date firstDateRegistered;
    private Date currentDateRegistered;
    private Date validUntil;
    private String status;

    public Registration() {}

    public Registration(int registrationId, int vehicleid,
                        int ownerId, int officerId, Date firstDateRegistered, Date currentDateRegistered,
                        Date validUntil, String status) {
        this.registrationId = registrationId;
        this.vehicleId = vehicleid;
        this.ownerId = ownerId;
        this.officerId = officerId;
        this.firstDateRegistered = firstDateRegistered;
        this.currentDateRegistered = currentDateRegistered;
        this.validUntil = validUntil;
        this.status = status;
    }

    // setters
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public void setFirstDateRegistered(Date firstDateRegistered) { this.firstDateRegistered = firstDateRegistered; }

    public void setCurrentDateRegistered(Date currentDateRegistered) { this.currentDateRegistered = currentDateRegistered; }

    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }

    public void setStatus(String status) { this.status = status; }

    // getters
    public int getRegistrationId() { return registrationId; }

    public int getVehicleId() { return vehicleId; }

    public int getOwnerId() { return ownerId; }

    public int getOfficerId() { return officerId; }

    public Date getFirstDateRegistered() { return firstDateRegistered; }

    public Date getCurrentDateRegistered() { return currentDateRegistered; }

    /**
     * Returns a short one-line summary of the registration record.
     */
    public String getSummary() {
        String first = (firstDateRegistered != null) ? firstDateRegistered.toString() : "N/A";
        String current = (currentDateRegistered != null) ? currentDateRegistered.toString() : "N/A";
        String valid = (validUntil != null) ? validUntil.toString() : "N/A";
        return String.format("RegID:%d | VehicleID:%d | OwnerID:%d | First:%s | Current:%s | ValidUntil:%s | Status:%s",
                registrationId, vehicleId, ownerId, first, current, valid, status == null ? "N/A" : status);
    }

    public Date getValidUntil() { return validUntil; }

    public String getStatus() { return status; }
}
