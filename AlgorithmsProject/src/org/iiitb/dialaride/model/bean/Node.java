package org.iiitb.dialaride.model.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.iiitb.dialaride.model.datastructures.Interval;
import org.iiitb.dialaride.model.datastructures.IntervalTree;

public class Node {

	/**
	 * Node Number
	 */
	private int nodeNumber;

	/**
	 * Sorted Map of Distance and List of Nodes
	 */
	private SortedMap<Integer, List<Neighbour>> adjacentNodes = new TreeMap<Integer, List<Neighbour>>();

	/**
	 * Neighbors with neighbor no.
	 */
	private Map<Integer, Neighbour> neighbours = new HashMap<Integer, Neighbour>();

	/**
	 * Map of Target Destination and Routes computed from Dijkstra's Algorithm
	 */
	private Map<Integer, List<Neighbour>> bestRoutes = new HashMap<Integer, List<Neighbour>>();

	private IntervalTree<Interval> cabsSet = new IntervalTree<Interval>();

	public Node(int nodeNumber) {
		super();
		this.nodeNumber = nodeNumber;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public Map<Integer, List<Neighbour>> getBestRoutes() {
		return bestRoutes;
	}

	public void setBestRoutes(Map<Integer, List<Neighbour>> bestRoutes) {
		this.bestRoutes = bestRoutes;
	}

	public SortedMap<Integer, List<Neighbour>> getAdjacentNodes() {
		return adjacentNodes;
	}

	public void setAdjacentNodes(
			SortedMap<Integer, List<Neighbour>> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}

	public Map<Integer, Neighbour> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Map<Integer, Neighbour> neighbours) {
		this.neighbours = neighbours;
	}

	public IntervalTree<Interval> getCabsSet() {
		return cabsSet;
	}

	public void setCabsSet(IntervalTree<Interval> cabsSet) {
		this.cabsSet = cabsSet;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node No: ").append(nodeNumber);

		sb.append("\n\tAdj (Node No, Distance): ");
		for (Integer dist : adjacentNodes.keySet()) {
			List<Neighbour> neighbours = adjacentNodes.get(dist);
			for (Neighbour neighbour : neighbours) {
				sb.append("(").append(neighbour.getNodeNumber()).append(", ")
						.append(neighbour.getDistance()).append(") ");
			}
		}

		sb.append("\n\tCabs: ");
		/*
		 * for (Cab cab : cabs) { sb.append(cab.getCabNo()).append(" "); }
		 */
		Interval searchInterval = new Interval(0, 2880, null);
		Iterable<Interval> intervals = cabsSet.keys(searchInterval);
		for (Interval intv : intervals) {
			Cab cab = intv.getCab();
			sb.append(cab.getCabNo()).append(" (").append(intv.getStart())
					.append(",").append(intv.getEnd()).append(" - P: ")
					.append(cab.getPassengers()).append(") ");
		}
		sb.append("\n");
		return sb.toString();
	}

}
