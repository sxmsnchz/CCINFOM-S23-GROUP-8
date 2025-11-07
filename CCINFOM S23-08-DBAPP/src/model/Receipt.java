package model;

import java.sql.Date;

public class Receipt {

    private int receiptId; // PK (auto-increment in DB)
    private int paymentId;  // FK 
    private String receiptNumber; // e.g., "V005", "R010" (UNIQUE)
    private Date issueDate;        
    private String printedBy;    

    public Receipt() {}

    public Receipt(int receiptId, int paymentId, String receiptNumber, Date issueDate, String printedBy) {
        this.receiptId = receiptId;
        this.paymentId = paymentId;
        this.receiptNumber = receiptNumber;
        this.issueDate = issueDate;
        this.printedBy = printedBy;
    }

    public int getReceiptId() { 
        return receiptId; 
    }

    public void setReceiptId(int receiptId) { 
        this.receiptId = receiptId; 
    }

    public int getPaymentId() { 
        return paymentId; 
    }

    public void setPaymentId(int paymentId) { 
        this.paymentId = paymentId; 
    }

    public String getReceiptNumber() { 
        return receiptNumber; 
    
    }
    public void setReceiptNumber(String receiptNumber) { 
        this.receiptNumber = receiptNumber; 
    }

    public Date getIssueDate() { 
        return issueDate; 
    }

    public void setIssueDate(Date issueDate) { 
        this.issueDate = issueDate; 
    }

    public String getPrintedBy() { 
        return printedBy; 
    }

    public void setPrintedBy(String printedBy) { 
        this.printedBy = printedBy; 
    }
}
