package source.model;
import java.util.*;

public class SolveurTSP {

    private ArrayList<Segment> segmentArrayList;
    private ArrayList<Vertex> vertexArrayList;

    private double[][] matrice_adjacence;
    private CompleteGraph completeGraph;

    private ArrayList<Vertex> Vertex_to_visit;
    private Entrepot entrepot;

    private double longueurSolutionCourante;

    private Map<Long, List<Long>> contraintesPrecedence;
    private Map<Long, Integer> vertexToGlobalNum;

    public SolveurTSP() {
        this.vertexArrayList = new ArrayList<>();
        this.segmentArrayList = new ArrayList<>();
        this.vertexToGlobalNum = new HashMap<>();
        this.completeGraph = new CompleteGraph();
    }

    public ArrayList<Vertex> getVertexList() {
        return vertexArrayList;
    }
    public ArrayList<Segment> getSegmentList() {
        return segmentArrayList;
    }

    public double getLongueurSolutionCourante() {
        return longueurSolutionCourante;
    }

    public void setSegmentList(ArrayList<Segment> segmentArrayList) {
        this.segmentArrayList = segmentArrayList;
    }

    public void setVertexList(ArrayList<Vertex> vertexArrayList) {
        this.vertexArrayList = vertexArrayList;
    }

    public void setEntrepot(Entrepot entrepot) {
        this.entrepot = entrepot;
    }


    public Entrepot getEntrepot() {
        return entrepot;
    }

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
            Integer num_ligne = vertexToGlobalNum.get(segment.getOrigin().getId());
            Integer num_colonne = vertexToGlobalNum.get(segment.getDestination().getId());
            if (num_colonne != 0 && num_ligne != 0){
                matrice[num_ligne-1][num_colonne-1] = segment.getLength();
                // NOTE pour test erreur décommenter la ligne suivante et commenter celle au-dessus
                //matrice[num_ligne-1][num_colonne-1] = Integer.MAX_VALUE;
            }

        }

        this.matrice_adjacence = matrice;

    }

    public void addDelivery(Delivery delivery){
        // Ajout des points d'une livraison à la liste des points a livrer
        // Ajout de l'entrepot si ce n'est pas déjà fait dans la liste des sommets à visiter
        if (Vertex_to_visit == null){
            // On crée la première fois la matrice d'adjacence représentant tous les segments du fichier xml pour faire le astar après
            creerMatriceAdjacence();
            Vertex_to_visit = new ArrayList<>();
            Vertex_to_visit.add(this.entrepot.getAddress());
            // On cherche à numéroter les sommets avec TSPnum pour avoir leur numéro dans la matrice
            this.entrepot.getAddress().setTSP_num(1);
            completeGraph.cost = new double[1][1];
            completeGraph.cost[0][0] = 0;
            completeGraph.nbVertices ++;
        }
        Vertex pickup_pt = delivery.getPickUpPt();
        Vertex delivery_pt = delivery.getDeliveryPt();
        int taille = completeGraph.cost.length;

        // Test si les points appartiennent déjà à des commandes précédentes (et sont donc déjà présent dans la matrice)
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

        //On ajoute les  nouveaux noeuds a la matrice et on calcule donc toutes les nouvelles "cases" avec la
        // distance la plus courte entre les points


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
        // Reconstruit le chemin à partir de la map cameFrom en récupérant à chaque fois le point précédent
        ArrayList<Vertex> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current); // Insère au début du chemin
            current = cameFrom.get(current);
        }
        return path;
    }

    private class astarResult {
        public final double distance;
        public final ArrayList<Vertex> path;

        public astarResult(double distance, ArrayList<Vertex> path) {
            this.distance = distance;
            this.path = path;
        }
    }

    private astarResult aStar(Vertex start, Vertex goal) {
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

    private ArrayList<Segment> reconstruireRouteReel(ArrayList<Segment> routeBirdFly) {
        // Reconstruit la route réelle en ajoutant tous les sommets intermédiaires au chemin
        ArrayList<Vertex> sommetsAVisiter = new ArrayList<>();
        for (Segment segment : routeBirdFly) {
            Vertex ptA = segment.getOrigin();
            Vertex ptB = segment.getDestination();
            ArrayList<Vertex> chemin = aStar(ptA, ptB).path;
            for (Vertex v : chemin) {
                sommetsAVisiter.add(v);
            }

        }
        ArrayList<Segment> reelRoute = new ArrayList<>();
        for (int i = 0; i < sommetsAVisiter.size()-1; i++) {
            if (sommetsAVisiter.get(i).equals(sommetsAVisiter.get(i+1))) {
                continue;
            }
            Segment seg = new Segment(sommetsAVisiter.get(i), sommetsAVisiter.get(i+1));
            reelRoute.add(seg);
        }
        return reelRoute;
    }

    private long[] ObtenirOrdreSommets(long[] sommets, Map<Long, List<Long>> precedence) {

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

        // vérifier si la solution est valide (s'il y a des null alors elle est invalide)
        longueurSolutionCourante = tsp.getSolutionCost();
        if (longueurSolutionCourante==0) return null;

        ordre[sommets.length] = 0;

        return ordre;
    }

    public ArrayList<Segment> ObtenirArrayListeSegmentsTSP(ArrayList<Delivery> deliveries) {
        // décomposer deliveries en un int[] de sommets et un int[] de précédence

        // reset matrice
        completeGraph = new CompleteGraph();
        Vertex_to_visit = null;
        contraintesPrecedence = null;

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

}
