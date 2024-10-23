package source.model;

public class Segment {

    private Vertex origine;
    private Vertex destination;
    private double longueur;
    private String nomRue;

    public Segment(String nomRue, Vertex origine, Vertex destination, double longueur) {
        this.nomRue = nomRue;
        this.origine = origine;
        this.destination = destination;
        this.longueur = longueur;
    }

    public Segment(Vertex origine, Vertex destination) {
        this.origine = origine;
        this.destination = destination;
    }

    public Vertex getOrigine() {
        return origine;
    }

    public void setOrigine(Vertex origine) {
        this.origine = origine;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public String getNomRue() {
        return nomRue;
    }

    public void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }

    @Override
    public String toString() {
        return "Segment{origine=" + origine + ", destination=" + destination + ", longueur=" + longueur + ", nomRue='" + nomRue + "'}";
    }
}
