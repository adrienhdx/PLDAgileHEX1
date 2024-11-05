package source.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
    private Date date;
    private ArrayList<Segment> segments;
    private ArrayList<Delivery> deliveries;


    public Route() {
        segments = new ArrayList<>();
        deliveries = new ArrayList<>();
        date = new Date();
    }

    // Getters et Setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public ArrayList<Segment> getSegments() { return segments; }
    public void setSegments(ArrayList<Segment> segments) { this.segments = segments; }

    public ArrayList<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(ArrayList<Delivery> deliveries) { this.deliveries = deliveries; }
}
