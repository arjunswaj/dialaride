package org.iiitb.dialaride.model.datastructures;

public class RbNode {
	public final int key;
	public boolean color;
	public RbNode parent;
	public RbNode left;
	public RbNode right;
	boolean deleted = false;

	public static boolean BLACK = false;
	public static boolean RED = true;

	private RbNode() {
		key = -1;
		// Default constructor is only meant to be used for the
		// construction of the NIL node.
	}

	public RbNode(int key) {
		this.parent = NIL;
		this.left = NIL;
		this.right = NIL;
		this.key = key;
		this.color = RED;
	}

	static RbNode NIL;
	static {
		NIL = new RbNode();
		NIL.color = BLACK;
		NIL.parent = NIL;
		NIL.left = NIL;
		NIL.right = NIL;
	}

	public boolean isNull() {
		return this == NIL;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String toString() {
		if (this == NIL) {
			return "nil";
		}
		return "(" + this.key + " " + (this.color == RED ? "RED" : "BLACK")
				+ " (" + this.left.toString() + ", " + this.right.toString()
				+ ")";
	}
}
