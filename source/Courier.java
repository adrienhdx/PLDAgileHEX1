package source;

public class Courier {
    private String id;
    private String lastName;
    private String firstName;
    private String phoneNum;

    public Courier(String id, String lastName, String firstName, String phoneNum) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phoneNum = phoneNum;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
}
