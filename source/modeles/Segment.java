package source.modeles;

public class Segment {

    private Vertice origine;
    private Vertice destination;
    private double longueur;
    private String nomRue;

    public Segment(String nomRue, Vertice origine, Vertice destination, double longueur) {
        this.nomRue = nomRue;
        this.origine = origine;
        this.destination = destination;
        this.longueur = longueur;
    }

    public Vertice getOrigine() {
        return origine;
    }

    public void setOrigine(Vertice origine) {
        this.origine = origine;
    }

    public Vertice getDestination() {
        return destination;
    }

    public void setDestination(Vertice destination) {
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
