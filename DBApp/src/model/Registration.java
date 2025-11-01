package model;
import java.sql.Date;

public class Registration {
    private int registrationId; //primary key
    private int vehicleId; //foreign key to vehicle
    private int ownerId; //foreign key to owner
    private int officerId; //foreign key to officer
    private Date dateRegistered;
    private Date validUntil;
    private String status;

    public Registration() {}

    public Registration(int registrationId, int vehicleid,
                        int ownerId, int officerId, Date dateRegistered,
                        Date validUntil, String status) {
        this.registrationId = registrationId;
        this.vehicleId = vehicleid;
        this.ownerId = ownerId;
        this.officerId = officerId;
        this.dateRegistered = dateRegistered;
        this.validUntil = validUntil;
        this.status = status;
    }

    // setters
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public void setDateRegistered(Date dateRegistered) { this.dateRegistered = dateRegistered; }

    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }

    public void setStatus(String status) { this.status = status; }

    // getters
    public int getRegistrationId() { return registrationId; }

    public int getVehicleId() { return vehicleId; }

    public int getOwnerId() { return ownerId; }

    public int getOfficerId() { return officerId; }

    public Date getDateRegistered() { return dateRegistered; }

    public Date getValidUntil() { return validUntil; }

    public String getStatus() { return status; }
}
