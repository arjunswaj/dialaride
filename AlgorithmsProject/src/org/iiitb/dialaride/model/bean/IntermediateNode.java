package org.iiitb.dialaride.model.bean;

public class IntermediateNode {

	private int nodeNumber;

	private int dropCount;

	private int dist;

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public IntermediateNode(int nodeNumber, int dropCount, int dist) {
		super();
		this.nodeNumber = nodeNumber;
		this.dropCount = dropCount;
		this.dist = dist;
	}

}
