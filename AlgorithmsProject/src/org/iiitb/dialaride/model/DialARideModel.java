package org.iiitb.dialaride.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;

import org.iiitb.dialaride.model.bean.Cab;
import org.iiitb.dialaride.model.bean.Neighbour;
import org.iiitb.dialaride.model.bean.Node;
import org.iiitb.dialaride.model.bean.Path;
import org.iiitb.dialaride.model.bean.RideRequest;

public class DialARideModel {

	/**
	 * Map of Location, Set of Cabs
	 */
	Map<Integer, Set<Cab>> cabs;

	/**
	 * List of Nodes
	 */
	private List<Node> nodes;

	/**
	 * Sorted Map of time and list of requests
	 */
	private SortedMap<Integer, List<RideRequest>> rideRequests;

	private Map<Integer, List<Path>> cabPath;

	private int[][] cost;

	private int[][] prev;

	private int maxRevenue;

	private int totalDistance;

	private int successfullyScheduledRequests;

	private int rejectedRequests;

	public DialARideModel(Map<Integer, Set<Cab>> cabs, List<Node> nodes,
			SortedMap<Integer, List<RideRequest>> rideRequests,
			Map<Integer, List<Path>> cabPath) {
		super();
		this.cabs = cabs;
		this.nodes = nodes;
		this.rideRequests = rideRequests;
		this.cabPath = cabPath;
		cost = new int[nodes.size()][nodes.size()];
		prev = new int[nodes.size()][nodes.size()];
	}

	public Map<Integer, Set<Cab>> getCabs() {
		return cabs;
	}

	public void setCabs(Map<Integer, Set<Cab>> cabs) {
		this.cabs = cabs;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public SortedMap<Integer, List<RideRequest>> getRideRequests() {
		return rideRequests;
	}

	public void setRideRequests(
			SortedMap<Integer, List<RideRequest>> rideRequests) {
		this.rideRequests = rideRequests;
	}

	public int[][] getCost() {
		return cost;
	}

	public void setCost(int[][] cost) {
		this.cost = cost;
	}

	public int[][] getPrev() {
		return prev;
	}

	public void setPrev(int[][] prev) {
		this.prev = prev;
	}

	public Map<Integer, List<Path>> getCabPath() {
		return cabPath;
	}

	public void setCabPath(Map<Integer, List<Path>> cabPath) {
		this.cabPath = cabPath;
	}

	public int getMaxRevenue() {
		return maxRevenue;
	}

	public void setMaxRevenue(int maxRevenue) {
		this.maxRevenue = maxRevenue;
	}

	public int getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}

	public int getSuccessfullyScheduledRequests() {
		return successfullyScheduledRequests;
	}

	public void setSuccessfullyScheduledRequests(
			int successfullyScheduledRequests) {
		this.successfullyScheduledRequests = successfullyScheduledRequests;
	}

	public int getRejectedRequests() {
		return rejectedRequests;
	}

	public void setRejectedRequests(int rejectedRequests) {
		this.rejectedRequests = rejectedRequests;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cabs:\n");
		for (Integer nodeNo : cabs.keySet()) {
			Set<Cab> cabList = cabs.get(nodeNo);
			for (Cab cab : cabList) {
				sb.append(cab);
			}
		}
		sb.append("\nNodes:\n");
		for (Node node : nodes) {
			sb.append(node);
		}
		sb.append("\nRide Requests:\n");
		for (Integer timeStart : rideRequests.keySet()) {
			List<RideRequest> rideReqs = rideRequests.get(timeStart);
			for (RideRequest rideRequest : rideReqs) {
				sb.append(rideRequest);
			}
		}

		sb.append("\nShortest Path Cost Matrix:\n");
		for (int idx1 = 0; idx1 < nodes.size(); idx1 += 1) {
			for (int idx2 = 0; idx2 < nodes.size(); idx2 += 1) {
				sb.append(cost[idx1][idx2]).append(" ");
			}
			sb.append("\n");
		}

		sb.append("\nPaths:\n");
		for (int idx1 = 0; idx1 < nodes.size(); idx1 += 1) {
			for (int idx2 = 0; idx2 < nodes.size(); idx2 += 1) {
				Stack<Integer> revPath = new Stack<Integer>();
				int d = idx2;
				while (d != idx1) {
					revPath.add(d);
					d = prev[idx1][d];
				}
				revPath.add(d);
				String arrow = "";
				int src = idx1;
				int dest = -1;

				while (!revPath.isEmpty()) {
					dest = revPath.pop();
					int val = 0;
					Neighbour neighbour = nodes.get(src).getNeighbours()
							.get(dest);
					if (null != neighbour) {
						val = neighbour.getDistance();
					}
					arrow = " ---" + val + "---> ";
					src = dest;
					if (0 != val) {
						sb.append(arrow);
					}
					sb.append(dest);
				}
				sb.append("\n");
			}
			sb.append("\n");

		}

		return sb.toString();
	}

}
