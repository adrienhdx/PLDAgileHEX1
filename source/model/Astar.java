package source.model;

import java.util.*;

public class Astar {

    public Astar() {}

    private double heuristique(Vertice v1, Vertice v2) {
        // Calcul d'une heuristique pour améliorer les performances de a*
        return Math.sqrt(Math.pow(v1.getLatitude() - v2.getLatitude(), 2) + Math.pow(v1.getLongitude() - v2.getLongitude(), 2));
    }

    public double aStar(Vertice start, Vertice goal, double[][] matrice_adj, List<Vertice> verticeList) {
        int n = verticeList.size(); // Nombre de sommets
        
        // Ensembles pour les scores des chemins trouvés
        Map<Vertice, Double> gScore = new HashMap<>();  // Coût de départ au sommet
        Map<Vertice, Double> fScore = new HashMap<>();  // Coût total estimé (g + h)

        // Ensemble des sommets à visiter (min-heap sur les coûts)
        PriorityQueue<Vertice> openSet = new PriorityQueue<>(Comparator.comparingDouble(v -> fScore.get(v)));


        // On remplit les valeurs de base à l'infini
        for (Vertice v : verticeList) {
            gScore.put(v, Double.POSITIVE_INFINITY);
            fScore.put(v, Double.POSITIVE_INFINITY);
        }

        // Initialisation du nœud de départ
        gScore.put(start, 0.0);  // Distance de start à start est 0
        fScore.put(start, heuristique(start, goal));  // Heuristique de départ
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // Extraire le sommet avec le score f le plus bas
            Vertice current = openSet.poll();

            // Si nous avons atteint l'objectif, retourner la distance
            if (current.equals(goal)) {
                return gScore.get(current);  // La distance minimale
            }

            // Parcourir les voisins (nœuds adjacents)
            for (int i = 0; i < n; i++) {
                if (matrice_adj[current.getGlobal_num() - 1][i] > 0) {  // Si l'arête existe
                    Vertice neighbor = verticeList.get(i);
                    double tentativeGScore = gScore.get(current) + matrice_adj[current.getGlobal_num() - 1][i];

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
}
