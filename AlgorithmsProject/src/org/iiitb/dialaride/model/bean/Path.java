package org.iiitb.dialaride.model.bean;

import org.iiitb.dialaride.model.datastructures.consts.EventTypes;

public class Path {

	private int nodeNumber;
	private int timeInstant;
	private EventTypes eventTypes;

	public Path(int nodeNumber, int timeInstant, EventTypes eventTypes) {
		super();
		this.nodeNumber = nodeNumber;
		this.timeInstant = timeInstant;
		this.eventTypes = eventTypes;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public int getTimeInstant() {
		return timeInstant;
	}

	public void setTimeInstant(int timeInstant) {
		this.timeInstant = timeInstant;
	}

	public EventTypes getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(EventTypes eventTypes) {
		this.eventTypes = eventTypes;
	};

}
