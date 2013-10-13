package org.iiitb.dialaride.model.bean;

public class IntermediateNode {

	private int nodeNumber;

	private int dropCount;

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

	public IntermediateNode(int nodeNumber, int dropCount) {
		super();
		this.nodeNumber = nodeNumber;
		this.dropCount = dropCount;
	}

}
