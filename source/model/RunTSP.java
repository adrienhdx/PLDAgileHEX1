package source.model;

public class RunTSP {
	public static void main(String[] args) {
		int[] sommets = {0, 17210, 17385, 19273};
		int[] precedence = {-1, -1, 1, -1};

		TSP tsp = new TSP1();
		double[][] matrix = {  	{ 0.0, 10.0, 8.0, 1.4 },
				{ 11.0, 0.0, 5.1, 8.1 },
				{ 7.0, 3.4, 0.0, 3.2 },
				{ 1.8, 8.1, 3.2, 0.0} };

		System.out.println(matrix[0].toString());
		System.out.println(matrix[1].toString());
		System.out.println(matrix[2].toString());
		System.out.println(matrix[3].toString());

		// règles de précédence :
		// C(j, i) = C(0, j) = C(i, 0) = +inf
		for (int i=1; i<4; i++){
			if (precedence[i] == -1) continue;
			matrix[i][precedence[i]] = Integer.MAX_VALUE;
			matrix[0][i] = Integer.MAX_VALUE;
			matrix[precedence[i]][0] = Integer.MAX_VALUE;
		}

		System.out.println(matrix[0].toString());
		System.out.println(matrix[1].toString());
		System.out.println(matrix[2].toString());
		System.out.println(matrix[3].toString());
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
