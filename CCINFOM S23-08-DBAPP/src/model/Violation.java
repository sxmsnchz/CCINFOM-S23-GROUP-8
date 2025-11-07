package model;

import java.sql.Date;

public class Violation {
	private int violationId;
	private int vehicleId;
	private int ownerId;
	private int branchId;
	private int officerId;
	private int paymentId;
	private String violationType;
	private Date violationDate;
	private double fineAmount;
	private String paymentStatus; // e.g., "unpaid" or "cleared"

	public Violation() {}

	public Violation(int violationId, int vehicleId, int ownerId, int branchId,
					 int officerId, String violationType, Date violationDate,
					 double fineAmount, String paymentStatus) {
		this.violationId = violationId;
		this.vehicleId = vehicleId;
		this.ownerId = ownerId;
		this.branchId = branchId;
		this.officerId = officerId;
		this.violationType = violationType;
		this.violationDate = violationDate;
		this.fineAmount = fineAmount;
		this.paymentStatus = paymentStatus;
	}

	// setters
	public void setViolationId(int violationId) { 
		this.violationId = violationId; }

	public void setVehicleId(int vehicleId) { 
		this.vehicleId = vehicleId; }

	public void setOwnerId(int ownerId) { 
		this.ownerId = ownerId; }

	public void setBranchId(int branchId) { 
		this.branchId = branchId; }

	public void setOfficerId(int officerId) { 
		this.officerId = officerId; }

	public void setPaymentId(int paymentId) { 
		this.paymentId = paymentId; }

	public void setViolationType(String violationType) { 
		this.violationType = violationType; }

	public void setViolationDate(Date violationDate) { 
		this.violationDate = violationDate; }

	public void setFineAmount(double fineAmount) { 
		this.fineAmount = fineAmount; }

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus; }

	// getters
	public int getViolationId() { 
		return violationId; }

	public int getVehicleId() { 
		return vehicleId; }

	public int getOwnerId() { 
		return ownerId; }

	public int getBranchId() { 
		return branchId; }

	public int getOfficerId() { 
		return officerId; }

	public int getPaymentId() { 
		return paymentId; }

	public String getViolationType() { 
		return violationType; }

	public Date getViolationDate() { 
		return violationDate; }

	public double getFineAmount() { 
		return fineAmount; }

	public String getPaymentStatus() { 
		return paymentStatus; }
}
