package source.model;

import java.util.Collection;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {
	@Override
	protected double bound(Integer currentVertex, Collection<Integer> unvisited) {
		double lowerBound=0;
		// The lower bound is at least the cost of the minimum incoming branch
		double minimumIncomingBranch = Double.MAX_VALUE;
		for (Integer v : unvisited) {
			if (g.getCost(currentVertex, v) < minimumIncomingBranch) {
				minimumIncomingBranch = g.getCost(currentVertex, v);
			}
		}
		lowerBound += minimumIncomingBranch;
		// The lower bound is at least the cost of the minimum outgoing branch
		for (Integer v : unvisited) {
			double min = g.getCost(v, 0);
			for (Integer u : unvisited) {
				if (!u.equals(v) && g.getCost(v, u) < min) {
					min = g.getCost(v, u);
				}
			}
			lowerBound += min;
		}

		return lowerBound;
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g) {
		return new SeqIter(unvisited, currentVertex, g);
	}

}
