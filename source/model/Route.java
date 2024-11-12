package source.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
    private ArrayList<Segment> segments;
    private ArrayList<Delivery> deliveries;


    public Route() {
        segments = new ArrayList<>();
        deliveries = new ArrayList<>();
    }

    //Getters et Setters
    public ArrayList<Segment> getSegments() { return segments; }
    public void setSegments(ArrayList<Segment> segments) { this.segments = segments; }

    public ArrayList<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(ArrayList<Delivery> deliveries) { this.deliveries = deliveries; }
}
