package source.model;

import java.util.Date;
import java.util.List;

public class Route {
    private String id;
    private Date date;
    private List<Segment> segments;
    private List<Delivery> deliveries;


    public Route(String id, Date date, List<Segment> segments) {
        this.id = id;
        this.date = date;
        this.segments = segments;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public List<Segment> getSegments() { return segments; }
    public void setSegments(List<Segment> segments) { this.segments = segments; }

    public List<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<Delivery> deliveries) { this.deliveries = deliveries; }
}
