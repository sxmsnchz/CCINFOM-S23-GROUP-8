package model;
import java.sql.Date;

public class Payment {
    private int paymentId;
    private int owner Id;
    private int officerId;
    private int branchId;
    private double amountPaid;
    private Date datePaid;
    private String receiptNumber;
    private String paymentType;

    // constructors
    public Payment() {}

    public Payment(int paymentId, int ownerId, int officerId, int branchId,
                   double amountPaid, Date datePaid, String receiptNumber, String paymentType) {
        this.paymentId = paymentId;
        this.ownerId = ownerId;
        this.officerId = officerId;
        this.branchId = branchId;
        this.amountPaid = amountPaid;
        this.datePaid = datePaid;
        this.receiptNumber = receiptNumber;
        this.paymentType = paymentType;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
    
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOfficerId() {
        return officerId;
    }

    public void setOfficerId(int officerId) {
        this.officerId = officerId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

}
