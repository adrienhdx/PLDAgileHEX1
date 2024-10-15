package source;

public class Segment {
    private String id;
    private Vertice origin;
    private Vertice destination;

    public Segment(String id, Vertice origin, Vertice destination) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Vertice getOrigin() { return origin; }
    public void setOrigin(Vertice origin) { this.origin = origin; }

    public Vertice getDestination() { return destination; }
    public void setDestination(Vertice destination) { this.destination = destination; }
}
