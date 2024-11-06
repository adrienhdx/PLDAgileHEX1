package source.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class Model {
    private Graph map;
    private ArrayList<Delivery> assignedDeliveryArrayList;
    private ArrayList<Delivery> postponedDeliveryArrayList;
    private ArrayList<Delivery> pendingDeliveryArrayList;
    private ArrayList<Courier> courierArrayList;
    private ArrayList<Segment> segmentArrayList;
    private ArrayList<Vertex> vertexArrayList;
    private double[][] matrice_adjacence;
    private CompleteGraph completeGraph;
    private ArrayList<Vertex> Vertex_to_visit;
    private PropertyChangeSupport propertyChangeSupport;
    private Entrepot entrepot;
    private Map<Long, List<Long>> contraintesPrecedence;
    private Map<Long, Integer> vertexToGlobalNum;

    public Model(){
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.completeGraph = new CompleteGraph();
        this.courierArrayList = new ArrayList<>();
        this.pendingDeliveryArrayList = new ArrayList<>();
        this.assignedDeliveryArrayList = new ArrayList<>();
        this.postponedDeliveryArrayList = new ArrayList<>();
        this.vertexToGlobalNum = new HashMap<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener ArrayListener){
        propertyChangeSupport.addPropertyChangeListener(ArrayListener);
    }

    //Getters and setters
   public double[][] getMatrice_adjacence() {
        return matrice_adjacence;
    }
    public void setMatrice_adjacence(double[][] matrice_adjacence) {}

    // Getters et Setters
    public Graph getMap() { return map; }
    public void setMap(Graph map) { this.map = map; }

    public ArrayList<Delivery> getAssignedDeliveryArrayList() { return assignedDeliveryArrayList; }


    public ArrayList<Delivery> getPostponedDeliveryArrayList() { return postponedDeliveryArrayList; }


    public ArrayList<Delivery> getPendingDeliveryArrayList() { return pendingDeliveryArrayList; }

    public ArrayList<Courier> getCourierArrayList() { return courierArrayList; }

    public ArrayList<Segment> getSegmentArrayList() { return segmentArrayList; }
    public CompleteGraph getCompleteGraph() { return completeGraph; }
    public void setCompleteGraph(CompleteGraph completeGraph) { this.completeGraph = completeGraph; }

    public ArrayList<Vertex> getVertex_to_visit() { return Vertex_to_visit; }

    public ArrayList<Vertex> getVertexArrayList() { return vertexArrayList; }

    public void setVertex_to_visit(ArrayList<Vertex> vertex_to_visit) {
        Vertex_to_visit = vertex_to_visit;
    }

    public Entrepot getEntrepot(){
        return entrepot;
    }

    public void setEntrepot(Entrepot entrepot){
        this.entrepot = entrepot;
    }

    //Method

    public Courier createCourier (String firstName, String lastName, String phoneNumber){
        if(!firstName.isEmpty() & !lastName.isEmpty() & !phoneNumber.isEmpty()) {
            for (Courier courier : courierArrayList) {
                if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                    propertyChangeSupport.firePropertyChange("errorMessage", null, "This courier already exists");
                    return null;
                }
            }
            return new Courier(firstName, lastName, phoneNumber);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "Please fill in all fields");
            return null;
        }
    }

    public void addCourier (Courier courier){
        courierArrayList.add(courier);
        propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
    }

    public Courier getCourier(String firstName, String lastName) {
        for (Courier courier : courierArrayList){
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                return courier;
            }
        }
        return null;
    }

    public Delivery getPendingDelivery(Long pickUpPtStr, Long deliveryPtStr) {
        for (Delivery delivery : pendingDeliveryArrayList){
            if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                return delivery;
            }
        }
        return null;
    }

    public void deleteCourier(Courier courier){
        if (courier != null) {
            courierArrayList.remove(courier);
            propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public void assignDelivery(Courier courier, Delivery delivery){
        if (courier != null) {
            if (delivery != null) {
                courier.getRoute().getDeliveries().add(delivery);
                ArrayList<Segment> routeComputed = this.ObtenirArrayListeSegmentsTSP(courier.getRoute().getDeliveries());
                if (routeComputed != null) {
                    courier.getRoute().setSegments(routeComputed);
                    pendingDeliveryArrayList.remove(delivery);
                    assignedDeliveryArrayList.add(delivery);
                    delivery.setState(DeliveryState.ASSIGNED);
                    propertyChangeSupport.firePropertyChange("courierRouteDeliveries", null, courier.getRoute().getDeliveries());
                    propertyChangeSupport.firePropertyChange("pendingDeliveryRemoved", null, delivery);
                } else {
                    courier.getRoute().getDeliveries().remove(delivery);
                    propertyChangeSupport.firePropertyChange("errorMessage", null, "No route found : the delivery can't be assigned to this courier");
                }
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No delivery selected");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public void updateMap(ArrayList<Vertex> vertexList, ArrayList<Segment> segmentList){
        if (segmentList != null && vertexList != null) {
            if (!vertexList.isEmpty()) {
                segmentArrayList = segmentList;
                vertexArrayList = vertexList;
                propertyChangeSupport.firePropertyChange("segmentArrayList", null, segmentArrayList);
                propertyChangeSupport.firePropertyChange("map", null, vertexArrayList.getFirst());
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The file doesn't contain any vertex, please select an other file");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    public void updateDeliveryList(ArrayList<Delivery> deliveryList, Entrepot entrepot){
        if (deliveryList != null) {
            if (entrepot != null){
                pendingDeliveryArrayList = deliveryList;
                propertyChangeSupport.firePropertyChange("pendingDeliveryArrayList", null, pendingDeliveryArrayList);
                this.entrepot = entrepot;
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "Entrepot location missing");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    public void getCourierSegmentList(Courier courier){
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                segmentArrayList = courier.getRoute().getSegments();
                ArrayList<Vertex> vertexToDisplay = new ArrayList<>();
                vertexToDisplay.add(entrepot.getAddress());
                for (Delivery delivery : courier.getRoute().getDeliveries()) {
                    vertexToDisplay.add(delivery.getPickUpPt());
                    vertexToDisplay.add(delivery.getDeliveryPt());
                }
                propertyChangeSupport.firePropertyChange("displaySegments", null, segmentArrayList);
                propertyChangeSupport.firePropertyChange("displayVertices", null, vertexToDisplay);

            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No route is associated with this courier : you must assign him at least one delivery");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public void getCourierInfo(Courier courier){
        propertyChangeSupport.firePropertyChange("courierInfo", null, courier);
    }

    //Recherche de chemin
    public void creerMatriceAdjacence(){
        // Création de la matrice d'adjacence entre tous les sommets de la carte chargée

        int taille = vertexArrayList.size();
        double [][] matrice = new double[taille][taille];
        int i = 1;
        // On numérote chaque sommet afin de pouvoir les identifier dans la matrice (leur ID n'est pas pratique)
        for (Vertex vertex : vertexArrayList){
            vertexToGlobalNum.put(vertex.getId(), i);
            for (int j=0; j<taille; j++){
                matrice[i-1][j] = -1;
            }
            i++;
        }
        // On remplit les longueurs entre deux sommets en récupérant leur numéro
        for (Segment segment : segmentArrayList){
            Integer num_ligne = vertexToGlobalNum.get(segment.getOrigine().getId());
            Integer num_colonne = vertexToGlobalNum.get(segment.getDestination().getId());
            if (num_colonne != 0 && num_ligne != 0){
                matrice[num_ligne-1][num_colonne-1] = segment.getLongueur();
                // NOTE pour test erreur décommenter la ligne suivante et commenter celle au-dessus
                //matrice[num_ligne-1][num_colonne-1] = Integer.MAX_VALUE;
            }

        }

        this.matrice_adjacence = matrice;

//        System.out.println("Matrice d'adjacence :");
//        for (int row = 0; row < taille; row++) {
//            for (int col = 0; col < taille; col++) {
//                // Affiche MAX_VALUE sous forme de "INF" pour mieux visualiser
//                if (matrice[row][col] == Integer.MAX_VALUE) {
//                    System.out.print("INF ");
//                } else {
//                    if (matrice[row][col] == -1) continue;
//                    System.out.print(matrice[row][col] + " ");
//                }
//            }
//            System.out.println(); // Retour à la ligne après chaque ligne de la matrice
//        }

    }

    public void addDelivery(Delivery delivery){
        if (Vertex_to_visit == null){
            creerMatriceAdjacence();
            Vertex_to_visit = new ArrayList<>();
            Vertex_to_visit.add(this.entrepot.getAddress());
            this.entrepot.getAddress().setTSP_num(1);
            completeGraph.cost = new double[1][1];
            completeGraph.cost[0][0] = 0;
            completeGraph.nbVertices ++;
        }
        Vertex pickup_pt = delivery.getPickUpPt();
        Vertex delivery_pt = delivery.getDeliveryPt();
        int taille = completeGraph.cost.length;

        // Test si les points appartiennent déjà à des commandes précédentes
        boolean newptA = false;
        boolean newptB = false;
        if (!Vertex_to_visit.contains(pickup_pt)){
            Vertex_to_visit.add(pickup_pt);
            newptA = true;
            completeGraph.nbVertices ++;
        }
        if (!Vertex_to_visit.contains(delivery_pt)) {
            Vertex_to_visit.add(delivery_pt);
            newptB = true;
            completeGraph.nbVertices ++;
        }

        //On ajoute les deux nouveaux noeuds a la matrice et on calcule donc toutes les nouvelles "cases" avec la
        // distance la plus courte entre les deux points


        double [][] matrix = new double[taille+2][taille+2];
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                matrix[i][j] = completeGraph.cost[i][j];
            }
        }

        for (Vertex vertex : Vertex_to_visit) {
            if (newptA && !newptB) {
                pickup_pt.setTSP_num(taille+1);
                matrix[taille][vertex.getTSP_num() - 1] = aStar(vertex, pickup_pt).distance;
                matrix[vertex.getTSP_num() - 1][taille] = aStar(pickup_pt, vertex).distance;
                matrix[taille][taille] = 0;

            }
            else if (newptB && newptA) {
                pickup_pt.setTSP_num(taille+1);
                delivery_pt.setTSP_num(taille + 2);
                matrix[taille][vertex.getTSP_num() - 1] = aStar(vertex, pickup_pt).distance;
                matrix[vertex.getTSP_num() - 1][taille] = aStar(pickup_pt, vertex).distance;
                matrix[taille+1][vertex.getTSP_num() - 1] = aStar(vertex, delivery_pt).distance;
                matrix[vertex.getTSP_num() - 1][taille+1] = aStar(delivery_pt, vertex).distance;
            }

            else if (newptB ) {
                delivery_pt.setTSP_num(taille + 1);
                matrix[taille][vertex.getTSP_num() - 1] = aStar(vertex, delivery_pt).distance;
                matrix[vertex.getTSP_num() - 1][taille] = aStar(delivery_pt, vertex).distance;
                matrix[taille][taille] = 0;
            }
        }

        if (newptA && newptB) {
            matrix[taille][taille + 1] = aStar(pickup_pt, delivery_pt).distance;
            matrix[taille + 1][taille] = aStar(delivery_pt, pickup_pt).distance;
            matrix[taille + 1][taille + 1] = 0;
            matrix[taille][taille] = 0;

        }
        completeGraph.cost = matrix;
        //print cost matrix
//        System.out.println("Matrice de coût après ajout de la livraison :");
//        for (int row = 0; row < completeGraph.nbVertices; row++) {
//            for (int col = 0; col < completeGraph.nbVertices; col++) {
//                // Affiche MAX_VALUE sous forme de "INF" pour mieux visualiser
//                if (completeGraph.cost[row][col] == Integer.MAX_VALUE) {
//                    System.out.print("INF ");
//                } else {
//                    if (completeGraph.cost[row][col] == -1) continue;
//                    System.out.print(completeGraph.cost[row][col] + " ");
//                }
//            }
//            System.out.println(); // Retour à la ligne après chaque ligne de la matrice
//        }

        // On ajoute les contraintes de précédence
        // Si pickup existe déjà dans la map
        // On ajoute deliveryPt.getTSPNum dans la liste des suivants
        // Sinon on ajoute simplement pickupPt.getTSPNum dans la map avec deliveryPt.getTSPNum comme suivant

        if (contraintesPrecedence == null) {
            contraintesPrecedence = new HashMap<>();
        }
        // ajouter -1 pour le point de delivery si nouveau
        if (newptB) contraintesPrecedence.put(delivery_pt.getId(), new ArrayList<>(Arrays.asList((long)-1)));

        if (contraintesPrecedence.containsKey(pickup_pt.getId())) {
            // vérifier si l'entrée est -1 : si oui supprimer la liste et la recréer avec delivery_pt.getId()
            if (contraintesPrecedence.get(pickup_pt.getId()).contains((long)-1)) {
                contraintesPrecedence.put(pickup_pt.getId(), new ArrayList<>(Arrays.asList(delivery_pt.getId())));
            } else {
                contraintesPrecedence.get(pickup_pt.getId()).add(delivery_pt.getId());
            }
        } else {
            contraintesPrecedence.put(pickup_pt.getId(), new ArrayList<>(Arrays.asList(delivery_pt.getId())));
        }
    }

    private double heuristique(Vertex v1, Vertex v2) {
        // Calcul d'une heuristique pour améliorer les performances de a*
        return Math.sqrt(Math.pow(v1.getLatitude() - v2.getLatitude(), 2) + Math.pow(v1.getLongitude() - v2.getLongitude(), 2));
    }

    private ArrayList<Vertex> reconstructPath(Map<Vertex, Vertex> cameFrom, Vertex current) {
        ArrayList<Vertex> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current); // Insère au début du chemin
            current = cameFrom.get(current);
        }
        return path;
    }

    public class astarResult {
        public final double distance;
        public final ArrayList<Vertex> path;

        public astarResult(double distance, ArrayList<Vertex> path) {
            this.distance = distance;
            this.path = path;
        }
    }

    public astarResult aStar(Vertex start, Vertex goal) {
        // renvoie la distance entre les deux sommets entrés en paramètres
        int n = vertexArrayList.size(); // Nombre de sommets

        // Ensembles pour les scores des chemins trouvés
        Map<Vertex, Double> gScore = new HashMap<>();  // Coût de départ au sommet
        Map<Vertex, Double> fScore = new HashMap<>();  // Coût total estimé (g + h)
        Map<Vertex, Vertex> cameFrom = new HashMap<>();

        // Ensemble des sommets à visiter (min-heap sur les coûts)
        PriorityQueue<Vertex> openSet = new PriorityQueue<>(Comparator.comparingDouble(v -> fScore.get(v)));


        // On remplit les valeurs de base à l'infini
        for (Vertex v : vertexArrayList) {
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
                return new astarResult(gScore.get(current),reconstructPath(cameFrom, current));  // La distance minimale
            }

            // Parcourir les voisins (nœuds adjacents)
            for (int i = 0; i < n; i++) {
                if (this.matrice_adjacence[vertexToGlobalNum.get(current.getId()) - 1][i] > 0) {  // Si l'arête existe
                    Vertex neighbor = vertexArrayList.get(i);
                    double tentativeGScore = gScore.get(current) + this.matrice_adjacence[vertexToGlobalNum.get(current.getId()) - 1][i];

                    if (tentativeGScore < gScore.get(neighbor)) {
                        // Meilleur chemin trouvé vers ce voisin
                        cameFrom.put(neighbor, current);
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
        return new astarResult(Double.POSITIVE_INFINITY, new ArrayList<>());
    }

    public ArrayList<Segment> reconstruireRouteReel(ArrayList<Segment> routeBirdFly) {
        ArrayList<Vertex> sommetsAVisiter = new ArrayList<>();
        for (Segment segment : routeBirdFly) {
            Vertex ptA = segment.getOrigine();
            Vertex ptB = segment.getDestination();
            ArrayList<Vertex> chemin = aStar(ptA, ptB).path;
            for (Vertex v : chemin) {
                sommetsAVisiter.add(v);
            }

        }
        ArrayList<Segment> reelRoute = new ArrayList<>();
        for (int i = 0; i < sommetsAVisiter.size()-1; i++) {
            Segment seg = new Segment(sommetsAVisiter.get(i), sommetsAVisiter.get(i+1));
            reelRoute.add(seg);
        }
        return reelRoute;
    }

    public long[] ObtenirOrdreSommets(long[] sommets, Map<Long, List<Long>> precedence) {

        TSP tsp = new TSP1();

        ArrayList<Long> sommetsList = new ArrayList<>();
        for (long sommet : sommets) {
            sommetsList.add(sommet);
        }

        // Règles de précédence :
        // C(j, i) = C(0, j) = C(i, 0) = +inf
        // Pour chaque entrée dans la hashmap de précédence
        for (Map.Entry<Long, List<Long>> entry : precedence.entrySet()) {
            // Si la liste des suivants n'est pas vide
            int courantIndex = sommetsList.indexOf(entry.getKey());
            if (courantIndex == -1 || courantIndex == 0) {
                // Si le sommet courant n'existe pas ou est le dépot
                continue;
            }
            if (!entry.getValue().contains((long)-1)) {
                // Pour chaque suivant
                // On obtient l'indice du sommet courant dans la liste des sommets
                for (long suivant : entry.getValue()) {
                    // On obtient l'indice du sommet suivant
                    int suivantIndex = sommetsList.indexOf(suivant);
                    // On empêche de retourner au dépot
                    completeGraph.cost[courantIndex][0] = Integer.MAX_VALUE;
                    // On empêche d'aller du suivant vers le courant
                    completeGraph.cost[suivantIndex][courantIndex] = Integer.MAX_VALUE;
                }
            } else {
                // On empêche d'aller du dépot vers le sommet courant directement
                completeGraph.cost[0][courantIndex] = Integer.MAX_VALUE;
            }
        }

        /*int[] precedenceReindexed = new int[precedence.length];

        for (int i = 0; i < sommets.length; i++) {
            if (precedence[i] == -1) continue;

            // trouver indice de precedence[i] dans sommets
            int index = sommetsArrayList.indexOf(precedence[i]);
            if (index != -1) {
                precedenceReindexed[i] = index;
            }
        }

        // règles de précédence :
        // C(j, i) = C(0, j) = C(i, 0) = +inf
        for (int i=1; i<sommets.length; i++){
            if (precedence[i] != -1) {
                completeGraph.cost[i][0] = Integer.MAX_VALUE;
                completeGraph.cost[precedenceReindexed[i]][i] = Integer.MAX_VALUE;
            } else {
                completeGraph.cost[0][i] = Integer.MAX_VALUE;
            }
        }*/

        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, completeGraph);

        if (tsp.getSolutionCost() == Integer.MAX_VALUE) {
            System.out.println("TSP : No solution found");
            return null;
        }
        System.out.print("TSP : Solution of cost "+tsp.getSolutionCost()+" found in "
                +(System.currentTimeMillis() - startTime)+"ms");

        long[] ordre = new long[sommets.length+1];
        for (int i=1; i<sommets.length; i++)
        {
            ordre[i] = sommets[tsp.getSolution(i)];
        }

        ordre[sommets.length] = 0;

        return ordre;
    }

    public ArrayList<Segment> ObtenirArrayListeSegmentsTSP(ArrayList<Delivery> deliveries) {
        // décomposer deliveries en un int[] de sommets et un int[] de précédence

        for (Delivery delivery : deliveries) {
            addDelivery(delivery); // ici chaque vertex est ajoutée dans vertex_to_visit
        }

        System.out.println("Deliveries chargées : " + deliveries.size());

        // sommets
        long[] sommets = new long[Vertex_to_visit.size()];
        HashMap<Long, Vertex> dictionnaire = new HashMap<>();
        for (int i = 0; i < Vertex_to_visit.size(); i++) {
            Vertex current = Vertex_to_visit.get(i);
            sommets[i] = current.getId();
            dictionnaire.put(current.getId(), current);
        }

        System.out.println("Sommets : " + Arrays.toString(sommets));

        // obtenir l'ordre d'après ObtenirOrdreSommets()

        long[] ordre = ObtenirOrdreSommets(sommets, contraintesPrecedence);
        if (ordre == null) {
            System.out.println("TSP : Pas de trajet possible avec cette nouvelle commande");
            return null;
        }

        System.out.println("Ordre calculé "+Arrays.toString(ordre));

        // recomposer l'ordre en une ArrayListe de segments et retour (ne pas oublier le dépôt)
        ArrayList<Segment> segments = new ArrayList<>();
        for (int i = 1; i < ordre.length; i++) {
            Vertex origine = dictionnaire.get(ordre[i-1]);
            if (origine == null) {
                origine = entrepot.getAddress();
            }
            Vertex destination = dictionnaire.get(ordre[i]);
            if (destination == null) {
                destination = entrepot.getAddress();
            }
            Segment seg = new Segment(origine, destination);
            segments.add(seg);
        }

        System.out.println("Segments : " + segments.size());
        for (Segment seg : segments) {
            System.out.println(seg);
        }
        ArrayList<Segment> vraisSegments = reconstruireRouteReel(segments);
        return vraisSegments;
    }


    public ArrayList<Segment> computeTour(ArrayList<Delivery> dArrayList) {
        // create warehouse
        // create segment from warehouse to p1 then p1 to d1 then d1 to p2 etc
        System.out.println("Calcul du tour par TSP :");
        ArrayList<Segment> segments = new ArrayList<>();
        Vertex origin;
        Vertex warehouse = entrepot.getAddress();
        Vertex destination = warehouse;
        for (Delivery delivery : dArrayList) {
            origin = delivery.getPickUpPt();
            Segment seg = new Segment(destination, origin);
            segments.add(seg);
            destination = delivery.getDeliveryPt();
            Segment seg2 = new Segment(origin, destination);
            segments.add(seg2);
        }

        Segment fin = new Segment(destination, warehouse);
        segments.add(fin);
        return segments;

    }
}
