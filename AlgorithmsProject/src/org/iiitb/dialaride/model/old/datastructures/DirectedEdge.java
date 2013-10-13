package org.iiitb.dialaride.model.old.datastructures;

public class DirectedEdge {
	private final int v;
	private final int w;
	private final double weight;

	/**
	 * Create a directed edge from v to w with given weight.
	 */
	public DirectedEdge(int v, int w, double weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	/**
	 * Return the vertex where this edge begins.
	 */
	public int from() {
		return v;
	}

	/**
	 * Return the vertex where this edge ends.
	 */
	public int to() {
		return w;
	}

	/**
	 * Return the weight of this edge.
	 */
	public double weight() {
		return weight;
	}

	/**
	 * Return a string representation of this edge.
	 */
	public String toString() {
		return v + "->" + w + " " + String.format("%5.2f", weight);
	}
}