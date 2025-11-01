package model;
public class Branch {

    private int branchId;
    private String branchName;
    private String street;
    private String barangay;
    private String city;
    private String province;
    private String postalCode;
    private String region;

    public Branch() {}

    public Branch(int branchId, String branchName, String street, String barangay,
                  String city, String province, String postalCode, String region) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.street = street;
        this.barangay = barangay;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.region = region;
    }

    // Getters and Setters
    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
