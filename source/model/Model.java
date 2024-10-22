package source.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Observable;

public class Model {
    private Graph map;
    private List<Delivery> assignedDeliveryList;
    private List<Delivery> postponedDeliveryList;
    private List<Delivery> pendingDeliveryList;
    private List<Courier> courierList;
    private List<Segment> segmentList;
    private List<Vertex> vertexList;
    private double[][] matrice_adjacence;
    private CompleteGraph completeGraph;
    private List<Vertex> Vertex_to_visit;
    private PropertyChangeSupport propertyChangeSupport;

    public Model(){
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.completeGraph = new CompleteGraph();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void addCourrier(String firstName, String lastName, String phoneNumber){
        for(Courier courier : courierList){
            if(courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                return;
            }
        }
        courierList.add(new Courier(lastName,firstName,phoneNumber));
    }

    public void deleteCourier(String firstName, String lastName){
        for(Courier courier : courierList){
            if(courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                courierList.remove(courier);
            }
        }
    }

    public double[][] getMatrice_adjacence() {
        return matrice_adjacence;
    }
    public void setMatrice_adjacence(double[][] matrice_adjacence) {}

    // Getters et Setters
    public Graph getMap() { return map; }
    public void setMap(Graph map) { this.map = map; }

    public List<Delivery> getAssignedDeliveryList() { return assignedDeliveryList; }
    public void setAssignedDeliveryList(List<Delivery> assignedDeliveryList) { this.assignedDeliveryList = assignedDeliveryList; }

    public List<Delivery> getPostponedDeliveryList() { return postponedDeliveryList; }
    public void setPostponedDeliveryList(List<Delivery> postponedDeliveryList) { this.postponedDeliveryList = postponedDeliveryList; }

    public List<Delivery> getPendingDeliveryList() { return pendingDeliveryList; }
    public void setPendingDeliveryList(List<Delivery> pendingDeliveryList) {
        ArrayList<Delivery> oldPendingDeliveryList = (ArrayList<Delivery>) this.pendingDeliveryList;
        this.pendingDeliveryList = pendingDeliveryList;
        propertyChangeSupport.firePropertyChange("pendingDeliveryList", oldPendingDeliveryList, pendingDeliveryList);
    }

    public List<Courier> getCourierList() { return courierList; }
    public void setCourierList(List<Courier> courierList) { this.courierList = courierList; }

    public List<Segment> getSegmentList() { return segmentList; }
    public void setSegmentList(List<Segment> segmentList) { this.segmentList = segmentList; }

    public ArrayList<Vertex> getVertexList() { return (ArrayList<Vertex>) vertexList; }
    public void setVertexList(List<Vertex> vertexList) {
        ArrayList<Vertex> oldVertexList = (ArrayList<Vertex>) this.vertexList;
        this.vertexList = vertexList;
        propertyChangeSupport.firePropertyChange("vertexList",oldVertexList,vertexList);
    }

    public CompleteGraph getCompleteGraph() { return completeGraph; }
    public void setCompleteGraph(CompleteGraph completeGraph) { this.completeGraph = completeGraph; }

    public List<Vertex> getVertex_to_visit() { return Vertex_to_visit; }

    public void setVertex_to_visit(List<Vertex> vertex_to_visit) {
        Vertex_to_visit = vertex_to_visit;
    }

    public void creerMatriceAdjacence(){
        // Création de la matrice d'adjacence entre tous les sommets de la carte chargée

        int taille = vertexList.size();
        double [][] matrice = new double[taille][taille];
        int i = 1;
        // On numérote chaque sommet afin de pouvoir les identifier dans la matrice (leur ID n'est pas pratique)
        for (Vertex Vertex : vertexList){
            Vertex.setGlobal_num(i);
            for (int j=0; j<taille; j++){
                matrice[i-1][j] = -1;
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

//        System.out.println("Matrice d'adjacence :");
//        for (int row = 0; row < taille; row++) {
//            for (int col = 0; col < taille; col++) {
//                // Affiche MAX_VALUE sous forme de "INF" pour mieux visualiser
//                if (matrice[row][col] == Integer.MAX_VALUE) {
//                    System.out.print("INF ");
//                } else {
//                    System.out.print(matrice[row][col] + " ");
//                }
//            }
//            System.out.println(); // Retour à la ligne après chaque ligne de la matrice
//        }

    }

    public void addDelivery(Delivery delivery){
        Vertex pickup_pt = delivery.getPickUpPt();
        Vertex delivery_pt = delivery.getDeliveryPt();
        // Dans le cas où on ajoute la première commande
        // TODO modifier en ajoutant l'entrepot de base en premier point (va donc decaler les indices)
        if (completeGraph.cost == null){
            pickup_pt.setTSP_num(1);
            delivery_pt.setTSP_num(2);
            completeGraph.cost = new double[2][2];
            completeGraph.cost[0][1] = aStar(pickup_pt, delivery_pt);
            completeGraph.cost[1][0] = aStar(delivery_pt, pickup_pt);
            completeGraph.cost[0][0] = 0;
            completeGraph.cost[1][1] = 0;
        }
        else{
            //On ajoute les deux nouveaux noeuds a la matrice et on calcule donc toutes les nouvelles "cases" avec la
            // distance la plus courte entre les deux points
            int taille = completeGraph.cost.length;
            pickup_pt.setTSP_num(taille+1);
            delivery_pt.setTSP_num(taille+2);
            double [][] matrix = new double[taille + 2][taille + 2];
            completeGraph.cost = matrix;
            for (Vertex vertex : Vertex_to_visit) {
                completeGraph.cost[taille][vertex.getGlobal_num()-1] = aStar(vertex, pickup_pt);
                completeGraph.cost[taille+1][vertex.getGlobal_num()-1] = aStar(vertex, delivery_pt);
                completeGraph.cost[vertex.getGlobal_num()-1][taille] = aStar(vertex, pickup_pt);
                completeGraph.cost[vertex.getGlobal_num()-1][taille+1] = aStar(vertex, delivery_pt);
            }
            completeGraph.cost[taille][taille+1] = aStar(pickup_pt, delivery_pt);
            completeGraph.cost[taille+1][taille] = aStar(delivery_pt, pickup_pt);
            completeGraph.cost[taille+1][taille+1] = 0;
            completeGraph.cost[taille][taille] = 0;
        }
    }

    private double heuristique(Vertex v1, Vertex v2) {
        // Calcul d'une heuristique pour améliorer les performances de a*
        return Math.sqrt(Math.pow(v1.getLatitude() - v2.getLatitude(), 2) + Math.pow(v1.getLongitude() - v2.getLongitude(), 2));
    }

    public double aStar(Vertex start, Vertex goal) {
        // renvoie la distance entre les deux sommets entrés en paramètres
        int n = vertexList.size(); // Nombre de sommets

        // Ensembles pour les scores des chemins trouvés
        Map<Vertex, Double> gScore = new HashMap<>();  // Coût de départ au sommet
        Map<Vertex, Double> fScore = new HashMap<>();  // Coût total estimé (g + h)

        // Ensemble des sommets à visiter (min-heap sur les coûts)
        PriorityQueue<Vertex> openSet = new PriorityQueue<>(Comparator.comparingDouble(v -> fScore.get(v)));


        // On remplit les valeurs de base à l'infini
        for (Vertex v : vertexList) {
            gScore.put(v, Double.POSITIVE_INFINITY);
            fScore.put(v, Double.POSITIVE_INFINITY);
        }

        // Initialisation du nœud de départ
        gScore.put(start, 0.0);  // Distance de start à start est 0
        fScore.put(start, heuristique(start, goal));  // Heuristique de départ
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // Extraire le sommet avec le score f le plus bas
            Vertex current = openSet.poll();

            // Si nous avons atteint l'objectif, retourner la distance
            if (current.equals(goal)) {
                return gScore.get(current);  // La distance minimale
            }

            // Parcourir les voisins (nœuds adjacents)
            for (int i = 0; i < n; i++) {
                if (this.matrice_adjacence[current.getGlobal_num() - 1][i] > 0) {  // Si l'arête existe
                    Vertex neighbor = vertexList.get(i);
                    double tentativeGScore = gScore.get(current) + this.matrice_adjacence[current.getGlobal_num() - 1][i];

                    if (tentativeGScore < gScore.get(neighbor)) {
                        // Meilleur chemin trouvé vers ce voisin
                        gScore.put(neighbor, tentativeGScore);
                        fScore.put(neighbor, gScore.get(neighbor) + heuristique(neighbor, goal));

                        // Si ce voisin n'est pas encore dans l'openSet, l'ajouter
                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }

        // Si pas de chemin, on retourne la valeur maximum
        return Double.POSITIVE_INFINITY;
    }

    public int[] ObtenirOrdreSommets(int nombreSommets, int[] sommets, int[] precedence) {

        //int[] sommets = {0, 17210, 17385, 19273};
        // 1->2 and 3->2
        //int[] precedence = {-1, 17385, -1, 17385};
        // converted to -1 2 0 2

        TSP tsp = new TSP1();

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
        for (int i=1; i<nombreSommets; i++){
            if (precedence[i] != -1) {
                completeGraph.cost[i][0] = Integer.MAX_VALUE;
                completeGraph.cost[precedence[i]][i] = Integer.MAX_VALUE;
            } else {
                completeGraph.cost[0][i] = Integer.MAX_VALUE;
            }
        }

        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, completeGraph);

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
