package source.model;

public class CompleteGraph implements Graph {
	public int nbVertices;
	public double[][] cost;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 *
	 */
	public CompleteGraph() {
	}

	public CompleteGraph(int nbVertices, double[][] cost) {
		this.nbVertices = nbVertices;
		this.cost = cost;
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public double getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}
}
