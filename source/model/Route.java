package source.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
    private Date date;
    private List<Segment> segments;
    private List<Delivery> deliveries;


    public Route() {
        segments = new ArrayList<>();
        deliveries = new ArrayList<>();
        date = new Date();
    }

    // Getters et Setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public List<Segment> getSegments() { return segments; }
    public void setSegments(List<Segment> segments) { this.segments = segments; }

    public List<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<Delivery> deliveries) { this.deliveries = deliveries; }
}
