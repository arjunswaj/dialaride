package org.iiitb.dialaride.model.bean;

public class RideRequest {
	private int timeStart;
	private int timeEnd;
	private int source;
	private int destination;

	public RideRequest(int timeStart, int timeEnd, int source, int destination) {
		super();
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.source = source;
		this.destination = destination;
	}

	public int getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(int timeStart) {
		this.timeStart = timeStart;
	}

	public int getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(int timeEnd) {
		this.timeEnd = timeEnd;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Source: ").append(source).append(", Destination: ")
				.append(destination).append(", Time Start: ").append(timeStart)
				.append(", Time End: ").append(timeEnd).append("\n");
		return sb.toString();
	}

}
