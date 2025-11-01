package model;

public class Owner {
    private int ownerId;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String province;
    private int licenseNumber;

    public Owner() {} 

    public Owner(int ownerId, String firstName, String lastName,
                String street, String city, String province, int licenseNumber) {
        this.ownerId = ownerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.province = province;
        this.licenseNumber = licenseNumber;
    }

    //setters
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setStreet(String street) { this.street = street; }

    public void setCity(String city) { this.city = city; }

    public void setProvince(String province) { this.province = province; }

    public void setLicenseNumber(int licenseNumber) { this.licenseNumber = licenseNumber; }


    //getters
    public int getOwnerId() { return ownerId; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getStreet() { return street; }

    public String getCity() { return city; }

    public String getProvince() { return province; }

    public int getLicenseNumber() { return licenseNumber; }

}

