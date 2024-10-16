package source.modeles;

public class Segment {

    private Noeud origine;
    private Noeud destination;
    private double longueur;
    private String nomRue;

    public Segment(String nomRue, Noeud origine, Noeud destination, double longueur) {
        this.nomRue = nomRue;
        this.origine = origine;
        this.destination = destination;
        this.longueur = longueur;
    }

    public Noeud getOrigine() {
        return origine;
    }

    public void setOrigine(Noeud origine) {
        this.origine = origine;
    }

    public Noeud getDestination() {
        return destination;
    }

    public void setDestination(Noeud destination) {
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
