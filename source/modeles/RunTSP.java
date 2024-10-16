package source.modeles;

public class RunTSP {
	public static void main(String[] args) {
		TSP tsp = new TSP1();
		int nbVertices = 4;
		double[][] matrix = {  	{ 0.0, 10.0, 7.0, 1.4 },
								{ 10.0, 0.0, 5.1, 8.1 },
								{ 7.0, 5.1, 0.0, 3.2 },
								{ 1.4, 8.1, 3.2, 0.0} };
		Graph g = new CompleteGraph(nbVertices, matrix);

		long startTime = System.currentTimeMillis();
		tsp.searchSolution(20000, g);
		System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
				+(System.currentTimeMillis() - startTime)+"ms : ");
		for (int i=0; i<nbVertices; i++)
			System.out.print(tsp.getSolution(i)+" ");
		System.out.println("0");

	}

}
