package source.model;

public class RunTSP {
	public static void main(String[] args) {
		TSP tsp = new TSP1();
		int nbVertices = 4;

		// all PickupX->Warehouse are +inf
		// all DeliveryX->PickupX are +inf
		// Let's say 1->2
		// And 3->2
		// We get 0 1 2 3 0
		double[][] matrix = {  	{ 0.0, 10.0, Integer.MAX_VALUE, 1.4 },
								{ Integer.MAX_VALUE, 0.0, 5.1, 8.1 },
								{ 7.0, Integer.MAX_VALUE, 0.0, 3.2 },
								{ Integer.MAX_VALUE, 8.1, 3.2, 0.0} };
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
