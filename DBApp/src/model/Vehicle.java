package model;

import java.sql.Date;

public class Vehicle {
	private int vehicleId;
	private String plateNumber; // primary key
	private String model;
	private Date manufactureDate;
	private int ownerId; // foreign key to Owner
	private int mvFileNo;
	private String chassisNo;
	private String engineNo;
	private String color;

	public Vehicle() {}

	public Vehicle(int vehicleId, String plateNumber, String model, Date manufactureDate, int ownerId,
				   int mvFileNo, String chassisNo, String engineNo, String color) {
		this.vehicleId = vehicleId;
		this.plateNumber = plateNumber;
		this.model = model;
		this.manufactureDate = manufactureDate;
		this.ownerId = ownerId;
		this.mvFileNo = mvFileNo;
		this.chassisNo = chassisNo;
		this.engineNo = engineNo;
		this.color = color;
	}

	// setters
	public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

	public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

	public void setModel(String model) { this.model = model; }

	public void setManufactureDate(Date manufactureDate) { this.manufactureDate = manufactureDate; }

	public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

	public void setMvFileNo(int mvFileNo) { this.mvFileNo = mvFileNo; }

	public void setChassisNo(String chassisNo) { this.chassisNo = chassisNo; }

	public void setEngineNo(String engineNo) { this.engineNo = engineNo; }

	public void setColor(String color) { this.color = color; }

	// getters
	public int getVehicleId() { return vehicleId; }

	public String getPlateNumber() { return plateNumber; }

	public String getModel() { return model; }

	public Date getManufactureDate() { return manufactureDate; }

	public int getOwnerId() { return ownerId; }

	public int getMvFileNo() { return mvFileNo; }

	public String getChassisNo() { return chassisNo; }

	public String getEngineNo() { return engineNo; }

	public String getColor() { return color; }

}
