package org.iiitb.dialaride.model.datastructures;

/** An implementation of an interval tree, following the explanation.
 * from CLR.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.iiitb.dialaride.model.bean.Cab;

class IntervalNode {
	Interval interval;
	RbNode rbNode;

	public IntervalNode(Interval interval, RbNode rbNode) {
		super();
		this.interval = interval;
		this.rbNode = rbNode;
	}

	public Interval getInterval() {
		return interval;
	}

	public RbNode getRbNode() {
		return rbNode;
	}

}

public class IntervalTree {
	private final StatisticUpdate updater;
	private final RbTree tree;

	private final Map<RbNode, Interval> intervals;
	private final Map<RbNode, Integer> max;
	private final Map<RbNode, Integer> min;

	public IntervalTree() {
		this.updater = new IntervalTreeStatisticUpdate();
		this.tree = new RbTree(this.updater);

		this.intervals = new WeakHashMap<RbNode, Interval>();
		this.intervals.put(RbNode.NIL, null);

		this.max = new WeakHashMap<RbNode, Integer>();
		this.max.put(RbNode.NIL, new Integer(Integer.MIN_VALUE));
		this.min = new WeakHashMap<RbNode, Integer>();
		this.min.put(RbNode.NIL, new Integer(Integer.MAX_VALUE));
	}

	public void insert(Interval interval) {
		RbNode node = new RbNode(interval.getLow());
		this.intervals.put(node, interval);
		this.tree.insert(node);
	}

	public int size() {
		return this.tree.size();
	}

	// Returns the first matching interval that we can find.
	public Interval search(Interval interval) {

		RbNode node = tree.root();
		if (node.isNull())
			return null;

		while ((!node.isNull()) && (!getInterval(node).overlaps(interval))) {
			if (canOverlapOnLeftSide(interval, node)) {
				node = node.left;
			} else if (canOverlapOnRightSide(interval, node)) {
				node = node.right;
			} else {
				return null;
			}
		}

		// Defensive coding. node can be the NIL node, but it must
		// not be itself the null object.
		assert node != null;
		return getInterval(node);
	}

	private boolean canOverlapOnLeftSide(Interval interval, RbNode node) {
		return (!node.left.isNull()) && getMax(node.left) >= interval.getLow();
	}

	private boolean canOverlapOnRightSide(Interval interval, RbNode node) {
		return (!node.right.isNull())
				&& getMin(node.right) <= interval.getHigh();
	}

	// Returns all matches as a list of Intervals
	public List<Interval> searchAll(Interval interval) {
		// System.out.println("\n\nStarting search for " + interval);

		if (tree.root().isNull()) {
			return new ArrayList<Interval>();
		}
		return this._searchAll(interval, tree.root());
	}

	private List<Interval> _searchAll(Interval interval, RbNode node) {
		assert (!node.isNull());

		// System.out.println("Looking at " + getInterval(node));

		List<Interval> results = new ArrayList<Interval>();
		if (getInterval(node).overlaps(interval) && !node.isDeleted()) {
			results.add(getInterval(node));
			// System.out.println("match");
		} else {
			// System.out.println("mismatch, isDeleted:" + node.isDeleted());
		}

		if (canOverlapOnLeftSide(interval, node)) {
			results.addAll(_searchAll(interval, node.left));
		}

		if (canOverlapOnRightSide(interval, node)) {
			results.addAll(_searchAll(interval, node.right));
		}

		return results;
	}

	public void softDelete(Interval interval, int intervalCount) {
		List<IntervalNode> intervals = searchAllIntervalNodesForSoftDelete(
				interval, tree.root());
		IntervalNode intNode = intervals.get(intervalCount);
		intNode.getRbNode().setDeleted(true);
	}

	public void softDelete(Interval interval) {
		Cab cab = interval.getCab();
		List<IntervalNode> intervals = searchAllIntervalNodesForSoftDelete(
				interval, tree.root());
		for (IntervalNode intval : intervals) {
			Cab retCab = intval.getInterval().getCab();
			if ((retCab.getCabNo() == cab.getCabNo())) {
				intval.getRbNode().setDeleted(true);
				System.out.println("Deleted Baby. YAY!");
				break;
			}
		}
	}

	private List<IntervalNode> searchAllIntervalNodesForSoftDelete(
			Interval interval, RbNode node) {
		assert (!node.isNull());

		List<IntervalNode> results = new ArrayList<IntervalNode>();
		// if (null != node && null != getInterval(node)) {
		// System.out.println("Looking at " + getInterval(node));
		if (getInterval(node).overlaps(interval) && !node.isDeleted()) {
			results.add(new IntervalNode(getInterval(node), node));
			// System.out.println("match in deleted");
		} else {
			// System.out.println("mismatch in deleted, isDeleted:" +
			// node.isDeleted());
		}

		if (canOverlapOnLeftSide(interval, node)) {
			results.addAll(searchAllIntervalNodesForSoftDelete(interval,
					node.left));
		}

		if (canOverlapOnRightSide(interval, node)) {
			results.addAll(searchAllIntervalNodesForSoftDelete(interval,
					node.right));
		}
		// }
		return results;
	}

	public Interval getInterval(RbNode node) {
		assert (node != null);
		assert (!node.isNull());

		assert (this.intervals.containsKey(node));

		return this.intervals.get(node);
	}

	public int getMax(RbNode node) {
		assert (node != null);
		assert (this.intervals.containsKey(node));

		return (this.max.get(node)).intValue();
	}

	private void setMax(RbNode node, Integer value) {
		this.max.put(node, new Integer(value));
	}

	public int getMin(RbNode node) {
		assert (node != null);
		assert (this.intervals.containsKey(node));

		return (this.min.get(node)).intValue();
	}

	private void setMin(RbNode node, Integer value) {
		this.min.put(node, new Integer(value));
	}

	private class IntervalTreeStatisticUpdate implements StatisticUpdate {
		public void update(RbNode node) {
			setMax(node,
					max(max(getMax(node.left), getMax(node.right)),
							getInterval(node).getHigh()));

			setMin(node,
					min(min(getMin(node.left), getMin(node.right)),
							getInterval(node).getLow()));
		}

		private Integer max(Integer x, Integer y) {
			if (x > y) {
				return x;
			}
			return y;
		}

		private Integer min(Integer x, Integer y) {
			if (x < y) {
				return x;
			}
			return y;
		}

	}

	/**
	 * 
	 * Test case code: check to see that the data structure follows the right
	 * constraints of interval trees:
	 * 
	 * o. They're valid red-black trees o. getMax(node) is the maximum of any
	 * interval rooted at that node..
	 * 
	 * This code is expensive, and only meant to be used for assertions and
	 * testing.
	 */
	public boolean isValid() {
		return (this.tree.isValid() && hasCorrectMaxFields(this.tree.root) && hasCorrectMinFields(this.tree.root));
	}

	private boolean hasCorrectMaxFields(RbNode node) {
		if (node.isNull())
			return true;
		return (getRealMax(node) == getMax(node)
				&& hasCorrectMaxFields(node.left) && hasCorrectMaxFields(node.right));
	}

	private boolean hasCorrectMinFields(RbNode node) {
		if (node.isNull())
			return true;
		return (getRealMin(node) == getMin(node)
				&& hasCorrectMinFields(node.left) && hasCorrectMinFields(node.right));
	}

	private Integer getRealMax(RbNode node) {
		if (node.isNull())
			return Integer.MIN_VALUE;
		Integer leftMax = getRealMax(node.left);
		Integer rightMax = getRealMax(node.right);
		Integer nodeHigh = getInterval(node).getHigh();

		Integer max1 = (leftMax > rightMax ? leftMax : rightMax);
		return (max1 > nodeHigh ? max1 : nodeHigh);
	}

	private Integer getRealMin(RbNode node) {
		if (node.isNull())
			return Integer.MAX_VALUE;

		Integer leftMin = getRealMin(node.left);
		Integer rightMin = getRealMin(node.right);
		Integer nodeLow = getInterval(node).getLow();

		Integer min1 = (leftMin < rightMin ? leftMin : rightMin);
		return (min1 < nodeLow ? min1 : nodeLow);
	}

}