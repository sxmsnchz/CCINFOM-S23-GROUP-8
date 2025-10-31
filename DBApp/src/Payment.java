import java.sql.Date;

public class Payment {

    private int paymentId;
    private int officerId;
    private int branchId;
    private double amountPaid;
    private Date datePaid;
    private String receiptNumber;

    // constructors
    public Payment() {}

    public Payment(int paymentId, int officerId, int branchId,
                   double amountPaid, Date datePaid, String receiptNumber) {
        this.paymentId = paymentId;
        this.officerId = officerId;
        this.branchId = branchId;
        this.amountPaid = amountPaid;
        this.datePaid = datePaid;
        this.receiptNumber = receiptNumber;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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

    // to display later on
    @Override
    public String toString() {
        return "Payment ID: " + paymentId +
               " | Officer ID: " + officerId +
               " | Branch ID: " + branchId +
               " | Amount: ₱" + amountPaid +
               " | Date: " + datePaid +
               " | Receipt: " + receiptNumber;
    }
}
