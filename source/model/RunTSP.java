package source.model;

import java.util.*;

public class RunTSP {
	public static void main(String[] args) {

		// CLASSE TEST POUR TSP

		int[] sommets = {0, 17210, 17385, 19273};
		// 0 1 2 3
		// Supposons le cas 2->3 2->1

		// Contraintes de précédence : clé = sommet, valeur = liste des suivants
		// Passé en paramètre
		/*Map<Integer, List<Integer>> precedence = new HashMap<>();
		precedence.put(17385, Arrays.asList(-1)); // vide
		precedence.put(17210, Arrays.asList(17385)); // 1->2
		precedence.put(19273, Arrays.asList(17385)); // 3->2*/

		Map<Integer, List<Integer>> precedence = new HashMap<>();
		precedence.put(17385, Arrays.asList(17210, 19273)); // 2->1 2->3
		precedence.put(17210, Arrays.asList(-1)); // vide
		precedence.put(19273, Arrays.asList(-1)); // vide

		// Matrice de distance
		double[][] matrix = {
				{ 0.0,  10.0,  8.0,  1.4 },
				{ 10.0,  0.0,  5.1,  8.1 },
				{ 8.0,  5.1,  0.0,  3.2 },
				{ 1.4,  8.1,  3.2,  0.0 }
		};

		// Conversion des sommets dans une liste pour chiper les indices tel un renard rusé
		List<Integer> sommetsList = new ArrayList<>();
		for (int sommet : sommets) {
			sommetsList.add(sommet);
		}

		// Règles de précédence :
		// C(j, i) = C(0, j) = C(i, 0) = +inf
		// Pour chaque entrée dans la hashmap de précédence
		for (Map.Entry<Integer, List<Integer>> entry : precedence.entrySet()) {
			// Si la liste des suivants n'est pas vide
			int courantIndex = sommetsList.indexOf(entry.getKey());
			if (courantIndex == -1 || courantIndex == 0) {
				// Si le sommet courant n'existe pas ou est le dépot
				continue;
			}
			if (!entry.getValue().contains(-1)) {
				// Pour chaque suivant
				// On obtient l'indice du sommet courant dans la liste des sommets
				for (int suivant : entry.getValue()) {
					// On obtient l'indice du sommet suivant
					int suivantIndex = sommetsList.indexOf(suivant);
					// On empêche de retourner au dépot
					matrix[courantIndex][0] = Integer.MAX_VALUE;
					// On empêche d'aller du suivant vers le courant
					matrix[suivantIndex][courantIndex] = Integer.MAX_VALUE;
				}
			} else {
				// On empêche d'aller du dépot vers le sommet courant directement
				matrix[0][courantIndex] = Integer.MAX_VALUE;
			}
		}

		// Affichage de la matrice modifiée
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] == Integer.MAX_VALUE) {
					System.out.print("inf ");
					continue;
				}
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}

		// Solution avec TSP
		Graph g = new CompleteGraph(4, matrix);

		TSP tsp = new TSP1();
		long startTime = System.currentTimeMillis();
		tsp.searchSolution(20000, g);
		System.out.print("Solution of cost " + tsp.getSolutionCost() + " found in "
				+ (System.currentTimeMillis() - startTime) + "ms : ");
		for (int i = 0; i < 4; i++)
			System.out.print(tsp.getSolution(i) + " ");
		System.out.println("0");

	}

}
