package model;

public class Owner {
    private int ownerId; // primary key
    private String firstName;
    private String lastName;
    private int postal_code;
    private String street;
    private String barangay;
    private String city;
    private String province;
    private String region;
    private int licenseNumber;
    private String password; 

    public Owner() {} 

    public Owner(int ownerId, String firstName, String lastName, int postal_code, String street,
                 String barangay, String city, String province, String region, int licenseNumber, String password) {
        this.ownerId = ownerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postal_code = postal_code;
        this.street = street;
        this.barangay = barangay;
        this.city = city;
        this.province = province;
        this.region = region;
        this.licenseNumber = licenseNumber;
        this.password = password;
    }

    //setters
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setStreet(String street) { this.street = street; }

    public void setCity(String city) { this.city = city; }

    public void setProvince(String province) { this.province = province; }

    public void setLicenseNumber(int licenseNumber) { this.licenseNumber = licenseNumber; }

    public void setPostalCode(int postal_code) { this.postal_code = postal_code; }

    public void setBarangay(String barangay) { this.barangay = barangay; }

    public void setRegion(String region) { this.region = region; }

    public void setPassword(String password) { this.password = password; }
    //getters
    public int getOwnerId() { return ownerId; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getStreet() { return street; }

    public String getCity() { return city; }

    public String getProvince() { return province; }

    public int getLicenseNumber() { return licenseNumber; }

    public int getPostalCode() { return postal_code; }

    public String getBarangay() { return barangay; }

    public String getRegion() { return region; }

    public String getPassword() { return password; }

}

