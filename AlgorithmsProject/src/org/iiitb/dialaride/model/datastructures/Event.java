package org.iiitb.dialaride.model.datastructures;

import org.iiitb.dialaride.model.bean.Cab;
import org.iiitb.dialaride.model.bean.RideRequest;
import org.iiitb.dialaride.model.datastructures.consts.EventTypes;

public class Event implements Comparable<Event> {

	private EventTypes eventType;

	private Cab cab;

	private RideRequest rideRequest;

	private double timeInstant;

	private int nodeNumber;

	private int version;

	public Event(EventTypes eventType, Cab cab, RideRequest rideRequest,
			double timeInstant, int nodeNumber, int version) {
		super();
		this.eventType = eventType;
		this.cab = cab;
		this.rideRequest = rideRequest;
		this.timeInstant = timeInstant;
		this.nodeNumber = nodeNumber;
		this.version = version;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public void setTimeInstant(double timeInstant) {
		this.timeInstant = timeInstant;
	}

	public EventTypes getEventType() {
		return eventType;
	}

	public void setEventType(EventTypes eventType) {
		this.eventType = eventType;
	}

	public Cab getCab() {
		return cab;
	}

	public void setCab(Cab cab) {
		this.cab = cab;
	}

	public RideRequest getRideRequest() {
		return rideRequest;
	}

	public void setRideRequest(RideRequest rideRequest) {
		this.rideRequest = rideRequest;
	}

	public double getTimeInstant() {
		return timeInstant;
	}

	public void setTimeInstant(Double timeInstant) {
		this.timeInstant = timeInstant;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int compareTo(Event o) {
		double diff = this.timeInstant - o.getTimeInstant();
		if (0 == diff) {
			diff = eventType.getPriority() - o.getEventType().getPriority();
		}
		return (int) diff;
	}

}
