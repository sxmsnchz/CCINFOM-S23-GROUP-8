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
	private int manufactureDate; 

	public Vehicle() {}

	public Vehicle(int vehicleId, String plateNumber, String model, int manufactureDate,
				   int mvFileNo, int chassisNo, int engineNo, String color) {
		this.vehicleId = vehicleId;
		this.plateNumber = plateNumber;
		this.model = model;
		this.manufactureDate = manufactureDate;
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

	public void setMvFileNo(int mvFileNo) { this.mvFileNo = mvFileNo; }

	public void setChassisNo(int chassisNo) { this.chassisNo = chassisNo; }

	public void setEngineNo(int engineNo) { this.engineNo = engineNo; }

	public void setColor(String color) { this.color = color; }

	// getters
	public int getVehicleId() { return vehicleId; }

	public String getPlateNumber() { return plateNumber; }

	public String getModel() { return model; }

	public int getManufactureDate() { return manufactureDate; }

	public int getMvFileNo() { return mvFileNo; }

	public int getChassisNo() { return chassisNo; }

	public int getEngineNo() { return engineNo; }

	public String getColor() { return color; }

}
