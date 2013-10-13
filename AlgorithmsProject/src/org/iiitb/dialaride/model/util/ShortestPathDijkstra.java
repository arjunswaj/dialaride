package org.iiitb.dialaride.model.util;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.iiitb.dialaride.model.DialARideModel;
import org.iiitb.dialaride.model.bean.Neighbour;
import org.iiitb.dialaride.model.bean.Node;

class CostComparator implements Comparator<Cost> {
	@Override
	public int compare(Cost x, Cost y) {
		return x.getCost() - y.getCost();
	}
}

class Cost {
	private int nodeNumber;
	private int cost;

	public Cost(int nodeNumber, int cost) {
		super();
		this.nodeNumber = nodeNumber;
		this.cost = cost;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Number: ").append(nodeNumber).append(" Cost: ")
				.append(cost);
		return sb.toString();
	}
}

public class ShortestPathDijkstra {

	public void computeShortestPaths(DialARideModel model) {
		List<Node> nodes = model.getNodes();
		int numOfNodes = nodes.size();
		int[][] cost = model.getCost();
		int[][] prev = model.getPrev();
		for (int sourceNode = 0; sourceNode < numOfNodes; sourceNode += 1) {
			boolean[] visited = new boolean[numOfNodes];
			for (int index = 0; index < numOfNodes; index += 1) {
				cost[sourceNode][index] = Integer.MAX_VALUE;
				if (sourceNode == index) {
					cost[sourceNode][index] = 0;
					prev[sourceNode][index] = 0;
				}
				visited[index] = false;
			}
			CostComparator costComparator = new CostComparator();
			Queue<Cost> costQueue = new PriorityQueue<Cost>(numOfNodes,
					costComparator);
			costQueue.add(new Cost(sourceNode, 0));
			while (!costQueue.isEmpty()) {
				Cost costVal = costQueue.remove();				
				int src = costVal.getNodeNumber();
				if (!visited[src]) {
					visited[src] = true;
					Node node = nodes.get(src);
					for (Neighbour neighbour : node.getNeighbours().values()) {
						int dest = neighbour.getNodeNumber();						
						if (cost[sourceNode][dest] > cost[sourceNode][src]
								+ neighbour.getDistance()) {
							cost[sourceNode][dest] = cost[sourceNode][src]
									+ neighbour.getDistance();
							prev[sourceNode][dest] = src;
							costQueue
									.add(new Cost(dest, cost[sourceNode][dest]));
						}
					}
				}
			}			
		}
	}
}
