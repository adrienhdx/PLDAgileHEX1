package source.model;

public class Courier {
    private final String lastName;
    private final String firstName;
    private final String phoneNum;
    private Route route;

    public Courier(String firstName, String lastName, String phoneNum) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phoneNum = phoneNum;
        this.route = new Route();
    }

    // Getters et Setters
    public String getLastName() { return lastName; }

    public String getFirstName() { return firstName; }

    public String getPhoneNum() { return phoneNum; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
}
