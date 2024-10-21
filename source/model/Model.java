package source.model;

import java.util.List;
import java.util.Observable;

public class Model {
    private Graph map;
    private List<Delivery> assignedDeliveryList;
    private List<Delivery> postponedDeliveryList;
    private List<Delivery> pendingDeliveryList;
    private List<Courier> courierList;
    private List<Segment> segmentList;
    private List<Vertex> verticeList;

    public Model(){}

    // Getters et Setters


    public double[][] getMatrice_adjacence() {
        return matrice_adjacence;
    }
    public void setMatrice_adjacence(double[][] matrice_adjacence) {}

    public Graph getMap() { return map; }
    public void setMap(Graph map) { this.map = map; }

    public List<Delivery> getAssignedDeliveryList() { return assignedDeliveryList; }
    public void setAssignedDeliveryList(List<Delivery> assignedDeliveryList) { this.assignedDeliveryList = assignedDeliveryList; }

    public List<Delivery> getPostponedDeliveryList() { return postponedDeliveryList; }
    public void setPostponedDeliveryList(List<Delivery> postponedDeliveryList) { this.postponedDeliveryList = postponedDeliveryList; }

    public List<Delivery> getPendingDeliveryList() { return pendingDeliveryList; }
    public void setPendingDeliveryList(List<Delivery> pendingDeliveryList) { this.pendingDeliveryList = pendingDeliveryList; }

    public List<Courier> getCourierList() { return courierList; }
    public void setCourierList(List<Courier> courierList) { this.courierList = courierList; }

    public List<Segment> getSegmentList() { return segmentList; }
    public void setSegmentList(List<Segment> segmentList) { this.segmentList = segmentList; }

    public List<Vertex> getVerticeList() { return verticeList; }
    public void setVerticeList(List<Vertex> verticeList) { this.verticeList = verticeList; }

    public void creerMatriceAdjacence(){
        // Création de la matrice d'adjacence entre tous les sommets de la carte chargée

        int taille = verticeList.size();
        double [][] matrice = new double[taille][taille];
        int i = 1;
        // On numérote chaque sommet afin de pouvoir les identifier dans la matrice (leur ID n'est pas pratique)
        for (Vertex vertice : verticeList){
            vertice.setGlobal_num(i);
            for (int j=0; j<taille; j++){
                matrice[i][j] = Integer.MAX_VALUE;
            }
            i++;
        }
        // On remplit les longueurs entre deux sommets en récupérant leur numéro
        for (Segment segment : segmentList){
            int num_ligne = segment.getOrigine().getGlobal_num();
            int num_colonne = segment.getDestination().getGlobal_num();
            matrice[num_ligne-1][num_colonne-1] = segment.getLongueur();
        }
        this.matrice_adjacence = matrice;
    }

    public void ajouter_commande(){
        //a compléter
    }
}
