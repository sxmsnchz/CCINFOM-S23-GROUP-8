package model;

public class Vehicle {
	private int vehicleId; // primary key
	private String plateNumber;
	private int mvFileNo;
	private int chassisNo;
	private int engineNo;
	// make
	// series
	private String color;
	private String model;
	private int manufactureDate; // year
	private int ownerId; // foreign key to Owner

	public Vehicle() {}

	public Vehicle(int vehicleId, String plateNumber, String model, int manufactureDate, int ownerId,
				   int mvFileNo, int chassisNo, int engineNo, String color) {
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

	public void setManufactureDate(int manufactureDate) { this.manufactureDate = manufactureDate; }

	public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

	public void setMvFileNo(int mvFileNo) { this.mvFileNo = mvFileNo; }

	public void setChassisNo(int chassisNo) { this.chassisNo = chassisNo; }

	public void setEngineNo(int engineNo) { this.engineNo = engineNo; }

	public void setColor(String color) { this.color = color; }

	// getters
	public int getVehicleId() { return vehicleId; }

	public String getPlateNumber() { return plateNumber; }

	public String getModel() { return model; }

	public int getManufactureDate() { return manufactureDate; }

	public int getOwnerId() { return ownerId; }

	public int getMvFileNo() { return mvFileNo; }

	public int getChassisNo() { return chassisNo; }

	public int getEngineNo() { return engineNo; }

	public String getColor() { return color; }

	/**
	 * Compact display-friendly details for the vehicle.
	 */
	public String getDetails() {
		return String.format("Plate:%s | Model:%s | Year:%d | Color:%s | OwnerID:%d",
				plateNumber == null ? "N/A" : plateNumber,
				model == null ? "N/A" : model,
				manufactureDate,
				color == null ? "N/A" : color,
				ownerId);
	}
}
