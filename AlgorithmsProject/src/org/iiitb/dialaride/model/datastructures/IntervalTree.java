package org.iiitb.dialaride.model.datastructures;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.iiitb.dialaride.model.bean.Cab;

public class IntervalTree<Value extends Interval> {

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private IntervalNode root; // root of the BST

	// BST helper node data type
	private class IntervalNode {
		private Value val; // associated data
		private IntervalNode left, right; // links to left and right subtrees
		private boolean color; // color of parent link
		private int N; // subtree count

		public IntervalNode(Value val, boolean color, int N) {
			this.val = val;
			this.color = color;
			this.N = N;
		}

		public String toString() {
			return val.toString();
		}
	}

	/*************************************************************************
	 * IntervalNode helper methods
	 *************************************************************************/
	// is node x red; false if x is null ?
	private boolean isRed(IntervalNode x) {
		if (x == null)
			return false;
		return (x.color == RED);
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(IntervalNode x) {
		if (x == null)
			return 0;
		return x.N;
	}

	/*************************************************************************
	 * Size methods
	 *************************************************************************/

	// return number of key-value pairs in this symbol table
	public int size() {
		return size(root);
	}

	// is this symbol table empty?
	public boolean isEmpty() {
		return root == null;
	}

	/*************************************************************************
	 * Red-black insertion
	 *************************************************************************/

	// insert the key-value pair; overwrite the old value with the new value
	// if the key is already present
	public void add(Value val) {
		root = add(root, val);
		root.color = BLACK;
	}

	// insert the key-value pair in the subtree rooted at h
	private IntervalNode add(IntervalNode h, Value val) {
		if (h == null) {
			val.setMax(val.getEnd());
			IntervalNode node = new IntervalNode(val, RED, 1);
			return node;
		}

		int cmp = val.getStart() - h.val.getStart();
		if (cmp <= 0) {
			h.left = add(h.left, val);
		} else if (cmp > 0) {
			h.right = add(h.right, val);
		}
		// else h.val = val;
		int maxVal = findMaxVal(h.left, h.right, val.getEnd());
		h.val.setMax(maxVal);

		// fix-up any right-leaning links
		if (isRed(h.right) && !isRed(h.left))
			h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))
			flipColors(h);
		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

	private int findMaxVal(IntervalNode left, IntervalNode right, int end) {
		int maxVal = 0;
		int leftVal = 0;
		int rightVal = 0;
		if (null == left) {
			leftVal = Integer.MIN_VALUE;
		} else {
			leftVal = left.val.getMax();
		}
		if (null == right) {
			rightVal = Integer.MIN_VALUE;
		} else {
			rightVal = right.val.getMax();
		}

		if (leftVal >= rightVal && leftVal >= end) {
			maxVal = leftVal;
		} else if (rightVal >= leftVal && rightVal >= end) {
			maxVal = rightVal;
		} else if (end >= leftVal && end >= rightVal) {
			maxVal = end;
		}
		return maxVal;

	}

	public Iterable<Value> keys() {
		Queue<Value> queue = new LinkedList<Value>();
		keys(root, queue);
		return queue;
	}

	public void softDelete(Interval interval) {
		int ctr = 0;

		Cab intervalCab = interval.getCab();
		Iterable<Interval> intervals = keys(interval);
		for (Interval intval : intervals) {
			Cab matchedIntervalCab = intval.getCab();			
			if (matchedIntervalCab.getCabNo() == intervalCab.getCabNo()) {
				intval.setDeleted(true);
				//System.out.println("Deleted Baby. YAY! " + interval.getCab());
				ctr += 1;
			}
		}
		if (ctr == 0) {
			//System.out.println("None Deleted!: " + interval.getCab());
			intervals = keys(interval);
		}
	}

	public Iterable<Interval> keys(Interval interval) {
		Queue<Interval> queue = new LinkedList<Interval>();
		Queue<IntervalNode> nodes = new LinkedList<IntervalTree<Value>.IntervalNode>();

		if (null != root) {
			nodes.add(root);
		}
		while (!nodes.isEmpty()) {
			IntervalNode node = nodes.remove();
			if (doesItOverlaps(node, interval)) {
				if (!node.val.isDeleted()) {
					queue.add(node.val);
				}
				
			} 
			addLeftNode(nodes, node, interval);
			addRightNode(nodes, node, interval);
		}

		return queue;
	}

	private boolean addLeftNode(Queue<IntervalNode> nodes, IntervalNode node,
			Interval interval) {
		boolean added = false;
		if (null != node.left && node.left.val.getMax() >= interval.getStart()) {
			nodes.add(node.left);
			added = true;
		}
		return added;
	}

	private boolean addRightNode(Queue<IntervalNode> nodes, IntervalNode node,
			Interval interval) {
		boolean added = false;
		if (null != node.right) {
			nodes.add(node.right);
			added = true;
		}
		return added;
	}

	private boolean doesItOverlaps(IntervalNode node, Interval interval) {
		boolean stat = true;
		// System.out.println("Exploring IntervalNode: " + node);
		if (interval.getEnd() < node.val.getStart()) {
			// System.out.println("Interval (" + interval +
			// ") Ends before IntervalNode ("
			// + node.val + ") Starts");
			stat = false;
		} else if (interval.getStart() > node.val.getEnd()) {
			// System.out.println("Interval (" + interval +
			// ") Starts after IntervalNode ("
			// + node.val + ") Ends");
			stat = false;
		}
		return stat;
	}

	private void keys(IntervalNode h, Queue<Value> queue) {
		if (null == h) {
			return;
		}
		keys(h.left, queue);
		queue.add(h.val);
		keys(h.right, queue);
	}

	/*************************************************************************
	 * red-black tree helper functions
	 *************************************************************************/

	// make a left-leaning link lean to the right
	private IntervalNode rotateRight(IntervalNode h) {
		assert (h != null) && isRed(h.left);
		IntervalNode x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;

		h.val.setMax(findMaxVal(h.left, h.right, h.val.getEnd()));
		x.val.setMax(findMaxVal(x.left, x.right, x.val.getEnd()));

		return x;
	}

	// make a right-leaning link lean to the left
	private IntervalNode rotateLeft(IntervalNode h) {
		assert (h != null) && isRed(h.right);
		IntervalNode x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;

		h.val.setMax(findMaxVal(h.left, h.right, h.val.getEnd()));
		x.val.setMax(findMaxVal(x.left, x.right, x.val.getEnd()));

		return x;
	}

	// flip the colors of a node and its two children
	private void flipColors(IntervalNode h) {
		// h must have opposite color of its two children
		assert (h != null) && (h.left != null) && (h.right != null);
		assert (!isRed(h) && isRed(h.left) && isRed(h.right))
				|| (isRed(h) && !isRed(h.left) && !isRed(h.right));
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private IntervalNode moveRedLeft(IntervalNode h) {
		assert (h != null);
		assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private IntervalNode moveRedRight(IntervalNode h) {
		assert (h != null);
		assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
		flipColors(h);
		if (isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	// restore red-black tree invariant
	private IntervalNode balance(IntervalNode h) {
		assert (h != null);

		if (isRed(h.right))
			h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))
			flipColors(h);

		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

}
