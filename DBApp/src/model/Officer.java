package model;

public class Officer {
    private int officerId;
    private String firstName;
    private String lastName;
    private int branchId; //referencing branch FK
    
    public Officer() {}

    public Officer(int officerId, String firstName,String lastName, int branchId) {
        this.officerId = officerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branchId;
    }

    //setters
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setBranchId(int branchId) { this.branchId = branchId; }

    //getters
    public int getOfficerId() { return officerId; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public int getBranchId() { return branchId; }

    public String getFullName() { return firstName + " " + lastName; }
}
