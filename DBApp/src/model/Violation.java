package model;

import java.sql.Date;

public class Violation {
	private int violationId;
	private int vehicleId;
	private int ownerId;
	private int branchId;
	private int officerId;
	private String violationType;
	private Date violationDate;
	private double fineAmount;
	private String status; // e.g., "unpaid" or "cleared"

	public Violation() {}

	public Violation(int violationId, int vehicleId, int ownerId, int branchId,
					 int officerId, String violationType, Date violationDate,
					 double fineAmount, String status) {
		this.violationId = violationId;
		this.vehicleId = vehicleId;
		this.ownerId = ownerId;
		this.branchId = branchId;
		this.officerId = officerId;
		this.violationType = violationType;
		this.violationDate = violationDate;
		this.fineAmount = fineAmount;
		this.status = status;
	}

	// setters
	public void setViolationId(int violationId) { this.violationId = violationId; }

	public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

	public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

	public void setBranchId(int branchId) { this.branchId = branchId; }

	public void setOfficerId(int officerId) { this.officerId = officerId; }

	public void setViolationType(String violationType) { this.violationType = violationType; }

	public void setViolationDate(Date violationDate) { this.violationDate = violationDate; }

	public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

	public void setStatus(String status) { this.status = status; }

	// getters
	public int getViolationId() { return violationId; }

	public int getVehicleId() { return vehicleId; }

	public int getOwnerId() { return ownerId; }

	public int getBranchId() { return branchId; }

	public int getOfficerId() { return officerId; }

	public String getViolationType() { return violationType; }

	public Date getViolationDate() { return violationDate; }

	public double getFineAmount() { return fineAmount; }

	public String getStatus() { return status; }
}
