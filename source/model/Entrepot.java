package source.model;

import java.sql.Time;
import java.time.LocalTime;

public class Entrepot {
    Vertex address;
    LocalTime departureHour;

    public Entrepot(Vertex address, LocalTime departureHour) {
        this.address = address;
        this.departureHour = departureHour;
    }

    //Getters and setters
    public Vertex getAddress() {
        return address;
    }
    public void setAddress(Vertex address) {
        this.address = address;
    }

    public LocalTime getDepartureHour() {
        return departureHour;
    }
    public void setDepartureHour(LocalTime departureHour) {
        this.departureHour = departureHour;
    }

    @Override
    public String toString() {
        return "Entrepot{Address='" + address + "', DepartureHour=" + departureHour +"}";
    }
}
