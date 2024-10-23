package source.model;

public class Vertex {
    private Long id;
    private double latitude;
    private double longitude;
    private int global_num;
    private int TSP_num;

    public Vertex(Long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTSP_num() { return TSP_num; }
    public void setTSP_num(int TSP_num) { this.TSP_num = TSP_num; }

    public int getGlobal_num() { return global_num; }
    public void setGlobal_num(int global_num) { this.global_num = global_num; }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Noeud{id='" + id + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}
