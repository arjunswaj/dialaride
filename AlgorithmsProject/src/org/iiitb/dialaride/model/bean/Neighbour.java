package org.iiitb.dialaride.model.bean;

public class Neighbour {

	private int nodeNumber;

	private int distance;

	public Neighbour(int nodeNumber, int distance) {
		super();
		this.nodeNumber = nodeNumber;
		this.distance = distance;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "NodeNumber: " + nodeNumber + " distance: " + distance; 
	}
}
