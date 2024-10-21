package source.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private double[][] matrice_adjacence;

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

    public int[] ObtenirOrdreSommets(int nombreSommets, int[] sommets, int[] precedence) {

        //int[] sommets = {0, 17210, 17385, 19273};
        // 1->2 and 3->2
        //int[] precedence = {-1, 17385, -1, 17385};
        // converted to -1 2 0 2

        TSP tsp = new TSP1();
        double[][] matrix = {  	{ 0.0, 	10.0, 	8.0, 1.4 },
                { 10.0, 0.0, 	5.1, 8.1 },
                { 8.0, 	5.1, 	0.0, 3.2 },
                { 1.4, 	8.1, 	3.2, 0.0 } };

        List<Integer> sommetsList = new ArrayList<>();
        for (int sommet : sommets) {
            sommetsList.add(sommet);
        }

        for (int i = 0; i < sommets.length; i++) {
            if (precedence[i] == -1) continue;

            // trouver indice de precedence[i] dans sommets
            int index = sommetsList.indexOf(precedence[i]);
            if (index != -1) {
                precedence[i] = index;
            }
        }

        // règles de précédence :
        // C(j, i) = C(0, j) = C(i, 0) = +inf
        for (int i=1; i<4; i++){
            if (precedence[i] != -1) {
                matrix[i][0] = Integer.MAX_VALUE;
                matrix[precedence[i]][i] = Integer.MAX_VALUE;
            } else {
                matrix[0][i] = Integer.MAX_VALUE;
            }
        }

        Graph g = new CompleteGraph(nombreSommets, matrix);

        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);

        System.out.print("TSP : Solution of cost "+tsp.getSolutionCost()+" found in "
                +(System.currentTimeMillis() - startTime)+"ms");

        int[] ordre = new int[nombreSommets+1];
        for (int i=1; i<nombreSommets; i++)
        {
            ordre[i] = sommets[tsp.getSolution(i)];
        }

        ordre[nombreSommets] = 0;

        return ordre;
    }
}
