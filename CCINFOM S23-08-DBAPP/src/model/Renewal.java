package model;
import java.sql.Date;

public class Renewal {
    private int renewalId; //PK
    private int registrationId; //FK
    private int officerId; //FK
    private int branchId; //FK
    private Integer paymentId; //FK
    private Date lastRenewalDate;
    //private int duration    note: might not need this one na

    public Renewal() {}

    public Renewal(int renewalId, 
                    int registrationId, int officerId, int branchId, int paymentId,
                    Date lastRenewalDate) {
        
        this.renewalId = renewalId;
        this.registrationId = registrationId;
        this.officerId = officerId;
        this.branchId = branchId;
        this.paymentId = paymentId;
        this.lastRenewalDate = lastRenewalDate;
    }

    public Renewal(int renewalId, 
                    int registrationId, int officerId, int branchId) {
        
        this.renewalId = renewalId;
        this.registrationId = registrationId;
        this.officerId = officerId;
        this.branchId = branchId;
        this.paymentId = null;
        this.lastRenewalDate = null;
    }

    // getters and setters
    public int getRenewalId() { return renewalId; }
    public void setRenewalId(int renewalId) { this.renewalId = renewalId; }

    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getOwnerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public int getBranchId() { return branchId;}
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public int getPaymentId() { return paymentId;}
    public void setPaymentId(int paymentId) { this.paymentId = paymentId;}

    public Date getLastRenewalDate() { return lastRenewalDate; }
    public void setLastRenewalDate(Date lastRenewalDate) { this.lastRenewalDate = lastRenewalDate; }
}
