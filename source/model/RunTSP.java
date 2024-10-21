package source.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunTSP {
	public static void main(String[] args) {
		int[] sommets = {0, 17210, 17385, 19273};
		// 1->2 and 3->2
		int[] precedence = {-1, 17385, -1, 17385};
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

		for (int i = 0; i < sommets.length; i++) {
			System.out.println(precedence[i]); // ici precedence n'a pas changé
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

		Graph g = new CompleteGraph(4, matrix);

		long startTime = System.currentTimeMillis();
		tsp.searchSolution(20000, g);
		System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
				+(System.currentTimeMillis() - startTime)+"ms : ");
		for (int i=0; i<4; i++)
			System.out.print(tsp.getSolution(i)+" ");
		System.out.println("0");

	}

}
