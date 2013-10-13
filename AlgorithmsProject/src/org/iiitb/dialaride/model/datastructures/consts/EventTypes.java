package org.iiitb.dialaride.model.datastructures.consts;

public enum EventTypes {	
	REACHED_NODE(1),
	CUSTOMER_REQUEST(2),
	REACHED_PICKUP_NODE(3),
	REACHED_PARTIAL_TARGET_NODE(4),
	REACHED_TARGET_NODE(5);
	
	int priority;
	
	
	public int getPriority() {
		return priority;
	}


	/**
	 * Greater the value, Greater the priority
	 * @param priority
	 */
	EventTypes(int priority) {
		this.priority = priority;
	}
}
