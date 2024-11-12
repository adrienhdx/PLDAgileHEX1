package source.model;

public class Segment {

    private final Vertex origin;
    private Vertex destination;
    private double length;
    private String streetName;

    public Segment(String streetName, Vertex origin, Vertex destination, double length) {
        this.streetName = streetName;
        this.origin = origin;
        this.destination = destination;
        this.length = length;
    }

    public Segment(Vertex origin, Vertex destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public Vertex getDestination() {
        return destination;
    }
    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public double getLength() {
        return length;
    }

    public String getStreetName() {
        return streetName;
    }

    @Override
    public String toString() {
        return "Segment{origine=" + origin + ", destination=" + destination + ", longueur=" + length + ", nomRue='" + streetName + "'}";
    }
}
