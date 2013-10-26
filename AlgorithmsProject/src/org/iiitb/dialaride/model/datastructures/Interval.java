package org.iiitb.dialaride.model.datastructures;

import org.iiitb.dialaride.model.bean.Cab;

public class Interval {

	private int start;
	private int end;

	private int max;
	private boolean deleted = false;
	private Cab cab;

	public Interval(int start, int end, Cab cab) {
		super();
		this.start = start;
		this.end = end;
		this.cab = cab;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Cab getCab() {
		return cab;
	}

	public void setCab(Cab cab) {
		this.cab = cab;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String toString() {
		return "{Interval (" + start + ", " + end + "), max: " + max + " : "
				+ cab + "isDeleted:" + deleted + "} ";
	}
}
