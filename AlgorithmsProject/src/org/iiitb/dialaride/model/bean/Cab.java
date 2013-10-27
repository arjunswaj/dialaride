package org.iiitb.dialaride.model.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cab implements Comparable<Cab> {

	private int cabNo;

	private int capacity;

	private int passengers;

	private int currentNode;

	private boolean isPickingAnyone;

	private boolean isDroppingAnyone;

	private int version;

	private List<IntermediateNode> path = new ArrayList<IntermediateNode>();

	public Cab(int cabNo, int currentNode, int capacity,
			boolean isPickingAnyone, int version) {
		super();
		this.cabNo = cabNo;
		this.currentNode = currentNode;
		this.capacity = capacity;
		this.isPickingAnyone = isPickingAnyone;
		this.version = version;
		this.passengers = 0;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCabNo() {
		return cabNo;
	}

	public void setCabNo(int cabNo) {
		this.cabNo = cabNo;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public int getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(int currentNode) {
		this.currentNode = currentNode;
	}

	public List<IntermediateNode> getPath() {
		return path;
	}

	public void setPath(List<IntermediateNode> path) {
		this.path = path;
	}

	public boolean isPickingAnyone() {
		return isPickingAnyone;
	}

	public void setPickingAnyone(boolean isPickingAnyone) {
		this.isPickingAnyone = isPickingAnyone;
	}

	public boolean isDroppingAnyone() {
		return isDroppingAnyone;
	}

	public void setDroppingAnyone(boolean isDroppingAnyone) {
		this.isDroppingAnyone = isDroppingAnyone;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cab No: ").append(cabNo).append(", Capacity: ")
				.append(capacity).append(", Passengers: ").append(passengers)
				.append("");
		return sb.toString();
	}

	@Override
	public int compareTo(Cab arg0) {
		return this.cabNo - arg0.getCabNo();
	}
}
