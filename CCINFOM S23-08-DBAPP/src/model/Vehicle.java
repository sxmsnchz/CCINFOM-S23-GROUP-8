package model;
import java.sql.Date;

public class Vehicle {
	private int vehicleId; // primary key
	private String plateNumber;
	private int mvFileNo;
	private String chassisNo;
	private String engineNo;
	private String make;
	private String series;
	private String color;
	private Date manufactureDate; 

	public Vehicle() {}

	public Vehicle(int vehicleId, String plateNumber, Date manufactureDate,
				   int mvFileNo, String chassisNo, String engineNo, String color) {
		this.vehicleId = vehicleId;
		this.plateNumber = plateNumber;
		this.manufactureDate = manufactureDate;
		this.mvFileNo = mvFileNo;
		this.chassisNo = chassisNo;
		this.engineNo = engineNo;
		this.color = color;
	}

	public Vehicle(String plateNumber, Date manufactureDate,
				   int mvFileNo, String chassisNo, String engineNo, String make, String series, String color) {
		this.plateNumber = plateNumber;
		this.manufactureDate = manufactureDate;
		this.mvFileNo = mvFileNo;
		this.chassisNo = chassisNo;
		this.engineNo = engineNo;
		this.make = make;
		this.series = series;
		this.color = color;
	}

	// setters
	public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

	public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

	public void setManufactureDate(Date manufactureDate) { this.manufactureDate = manufactureDate; }

	public void setMvFileNo(int mvFileNo) { this.mvFileNo = mvFileNo; }

	public void setChassisNo(String chassisNo) { this.chassisNo = chassisNo; }

	public void setEngineNo(String engineNo) { this.engineNo = engineNo; }

	public void setMake(String make) { this.make = make; }

	public void setSeries(String series) { this.series = series; }

	public void setColor(String color) { this.color = color; }

	// getters
	public int getVehicleId() { return vehicleId; }

	public String getPlateNumber() { return plateNumber; }

	public Date getManufactureDate() { return manufactureDate; }

	public int getMvFileNo() { return mvFileNo; }

	public String getChassisNo() { return chassisNo; }

	public String getEngineNo() { return engineNo; }

	public String getMake() { return make; }

	public String getSeries() { return series; }

	public String getColor() { return color; }

}
