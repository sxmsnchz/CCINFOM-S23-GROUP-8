package model;

import java.sql.Date;

public class Vehicle {
	private String plateNumber; // primary key
	private String model;
	private int manufactureYear;
	private int ownerId; // foreign key to Owner

	public Vehicle() {}

	public Vehicle(String plateNumber, String model, int manufactureYear, int ownerId) {
		this.plateNumber = plateNumber;
		this.model = model;
		this.manufactureYear = manufactureYear;
		this.ownerId = ownerId;
	}

	// setters
	public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

	public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

	public void setModel(String model) { this.model = model; }

	public void setManufactureYear(int manufactureYear) { this.manufactureYear = manufactureYear; }

	public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

	// getters
	public int getVehicleId() { return vehicleId; }

	public String getPlateNumber() { return plateNumber; }

	public String getModel() { return model; }

	public int getManufactureYear() { return manufactureYear; }

	public int getOwnerId() { return ownerId; }
}
