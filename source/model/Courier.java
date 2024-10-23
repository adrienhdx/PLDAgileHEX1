package source.model;

public class Courier {
    private String lastName;
    private String firstName;
    private String phoneNum;
    private Route route;

    public Courier(String lastName, String firstName, String phoneNum) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phoneNum = phoneNum;
        this.route = new Route();
    }

    // Getters et Setters
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

}
