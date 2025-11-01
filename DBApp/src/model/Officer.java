package model;

public class Officer {
    private int officerId;
    private String firstName;
    private String lastName;
    private int branchId; //referencing branch FK
    private String password;
    
    public Officer() {}

    public Officer(int officerId, String firstName,String lastName, int branchId, String password) {
        this.officerId = officerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branchId;
        this.password = password;
    }

    //setters
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setBranchId(int branchId) { this.branchId = branchId; }

    public void setPassword(String password) { this.password = password; }

    //getters
    public int getOfficerId() { return officerId; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public int getBranchId() { return branchId; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPassword() { return password; }
    
}
