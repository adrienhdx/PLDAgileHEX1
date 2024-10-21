package source.model;
import java.util.List;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 10000;
	private static final int MIN_COST = 1;
	int nbVertices;
	double[][] cost;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param nbVertices
	 */
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

	public void addDelivery(Delivery delivery, List<Vertice> list_sommets){
		//Il faudrait une liste ordonnée des sommets par rapport a leur numglobal
		Vertex pickup_pt = delivery.getPickUpPt();
        Vertex delivery_pt = delivery.getDeliveryPt();
		Astar astar = new Astar();
		pickup_pt.setTSP_num(1);
		delivery_pt.setTSP_num(2);

		if (this.cost == null){
			this.cost = new double[2][2];
			//this.cost[0][1] = astar.aStar(pickup_pt, delivery_pt, mat_adj, list_sommets);
		}
		else{
			int taille = cost.length;
			double [][] matrix = new double[taille + 2][taille + 2];
			this.cost = matrix;
			for (int i = 0; i < taille; i++) {
				//this.cost[taille][i] = astar.aStar(sommeti, pickup_pt, mat_adj, list_sommets);
				//this.cost[taille+1][i] = astar.aStar(sommeti, delivery_pt, mat_adj, list_sommets);
			}
			for (int i = 0; i < taille; i++) {
				//this.cost[i][taille] = astar.aStar(sommeti, pickup_pt, mat_adj, list_sommets);
				//this.cost[i][taille+1] = astar.aStar(sommeti, delivery_pt, mat_adj, list_sommets);
			}
			//this.cost[taille][taille+1] = astar.aStar(pickup_pt, delivery_pt, mat_adj, list_sommets);
		}
	}

}
