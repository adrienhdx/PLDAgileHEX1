package source.modeles;

import java.util.Date;
import java.util.List;

public class Route {
    private String id;
    private Date date;
    private List<source.Segment> segments;
    private List<Delivery> deliveries;
    private Courier courier;

    public Route(String id, Date date, List<source.Segment> segments) {
        this.id = id;
        this.date = date;
        this.segments = segments;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public List<source.Segment> getSegments() { return segments; }
    public void setSegments(List<Segment> segments) { this.segments = segments; }

    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }

    public List<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<Delivery> deliveries) { this.deliveries = deliveries; }
}
