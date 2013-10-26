package org.iiitb.dialaride.model.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;

import org.iiitb.dialaride.model.DialARideModel;
import org.iiitb.dialaride.model.bean.Cab;
import org.iiitb.dialaride.model.bean.IntermediateNode;
import org.iiitb.dialaride.model.bean.Neighbour;
import org.iiitb.dialaride.model.bean.Node;
import org.iiitb.dialaride.model.bean.RideRequest;
import org.iiitb.dialaride.model.datastructures.Event;
import org.iiitb.dialaride.model.datastructures.Interval;
import org.iiitb.dialaride.model.datastructures.IntervalTree;
import org.iiitb.dialaride.model.datastructures.consts.EventTypes;
import org.iiitb.dialaride.model.datastructures.consts.SchedulerConstants;

public class CabScheduler {

	public void schedule(DialARideModel model) {
		int successfulScheduling = 0;
		int rejectedReqs = 0;
		int revenue = 0;
		Queue<Event> eventQueue = new PriorityQueue<Event>();

		SortedMap<Integer, List<RideRequest>> rideRequests = model
				.getRideRequests();
		for (Integer timeStart : rideRequests.keySet()) {
			// System.out.println("Putting Requests: " + timeStart);
			List<RideRequest> rideReqs = rideRequests.get(timeStart);
			for (RideRequest rideRequest : rideReqs) {
				eventQueue.add(new Event(EventTypes.CUSTOMER_REQUEST, null,
						rideRequest, rideRequest.getTimeStart(), -1, -1));
			}
		}
		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			System.out.println("Event at time: " + event.getTimeInstant());
			switch (event.getEventType()) {
			case CUSTOMER_REQUEST:
				boolean found = findACab(model, event.getRideRequest(),
						eventQueue, (int) event.getTimeInstant());
				if (found) {
					successfulScheduling += 1;
					revenue += model.getCost()[event.getRideRequest()
							.getSource()][event.getRideRequest()
							.getDestination()];
					System.out.println("**********Gotcha Ride!*************"
							+ " for Request: " + event.getRideRequest());
				} else {
					rejectedReqs += 1;
					System.out.println("-------------No Ride :-(------------"
							+ " for Request: " + event.getRideRequest());
				}
				break;
			case REACHED_NODE:
				if (event.getVersion() == event.getCab().getVersion()) {
					handleReachedIntermediateNode(model, event);
					System.out.println("Intermediate Node Reached "
							+ event.getNodeNumber() + "\t" + event.getCab());
				} else {
					handleStaleEvents(model, event);
				}
				break;
			case REACHED_PICKUP_NODE:
				handleReachedPickupNode(model, event);
				System.out
						.println("^^^^^^^^^^^^^^Pick Up! at time: "
								+ event.getTimeInstant() + "\n"
								+ event.getCab() + "\n");
				break;
			case REACHED_PARTIAL_TARGET_NODE:
				if (event.getVersion() == event.getCab().getVersion()) {
					handleReachedTargetNode(model, event, true);
					System.out
							.println("##################Yo! Reached Intermediate Destination "
									+ ", in Cab" + event.getCab());
				} else {
					// System.out.println("Dropping the invalid event 2");
					handleStaleEvents(model, event);
				}
				break;
			case REACHED_TARGET_NODE:
				if (event.getVersion() == event.getCab().getVersion()) {
					handleReachedTargetNode(model, event, false);
					System.out
							.println("##################Yo! Reached Destination "
									+ event.getRideRequest().getDestination()
									+ ", in Cab" + event.getCab());
				} else {
					// System.out.println("Dropping the invalid event 3");
					handleStaleEvents(model, event);
				}
				break;
			default:
				break;
			}
		}

		System.out.println("Successfully scheduled: " + successfulScheduling
				+ "\nRejected Requests: " + rejectedReqs + "\nRevenue: "
				+ revenue);
	}

	private void handleStaleEvents(DialARideModel model, Event event) {
		Cab cab = event.getCab();
		Node node = model.getNodes().get(event.getNodeNumber());
		IntervalTree<Interval> cabsSet = node.getCabsSet();
		cabsSet.softDelete(new Interval(
				(int) Math.ceil(event.getTimeInstant()), (int) Math.ceil(event
						.getTimeInstant()), cab));
	}

	private void handleReachedTargetNode(DialARideModel model, Event event,
			boolean isPartial) {
		Cab cab = event.getCab();
		cab.setDroppingAnyone(false);
		Node node = model.getNodes().get(event.getNodeNumber());
		IntervalTree<Interval> cabsSet = node.getCabsSet();
		cabsSet.softDelete(new Interval(
				(int) Math.ceil(event.getTimeInstant()), (int) Math.ceil(event
						.getTimeInstant()), cab));
		if (0 == cab.getPassengers()) {
			System.out.println("Something's wrong dude!");
		}
		cab.setPassengers(cab.getPassengers() - 1);
		cab.getPath().remove(0);
		if (!isPartial) {
			cabsSet.add(new Interval((int) Math.ceil(event.getTimeInstant()),
					1440, cab));
		}
	}

	private void handleReachedIntermediateNode(DialARideModel model, Event event) {
		Cab cab = event.getCab();
		Node node = model.getNodes().get(event.getNodeNumber());
		IntervalTree<Interval> cabsSet = node.getCabsSet();
		cabsSet.softDelete(new Interval(
				(int) Math.ceil(event.getTimeInstant()), (int) Math.ceil(event
						.getTimeInstant()), cab));
		cab.getPath().remove(0);
	}

	private void handleReachedPickupNode(DialARideModel model, Event event) {
		Cab cab = event.getCab();
		cab.setPickingAnyone(false);
		Node node = model.getNodes().get(event.getNodeNumber());
		IntervalTree<Interval> cabsSet = node.getCabsSet();

		cabsSet.softDelete(new Interval(
				(int) Math.ceil(event.getTimeInstant()), (int) Math.ceil(event
						.getTimeInstant()), cab));
	}

	private boolean findACab(DialARideModel model, RideRequest rideRequest,
			Queue<Event> eventQueue, int now) {
		boolean found = false;
		int source = rideRequest.getSource();
		double maxDistOfCab = (rideRequest.getTimeEnd() - rideRequest
				.getTimeStart()) / SchedulerConstants.MINUTES_PER_KM;
		Set<Integer> searchedNodes = new HashSet<Integer>();
		Queue<Node> nodeQueue = new LinkedList<Node>();
		List<Node> nodes = model.getNodes();
		nodeQueue.add(nodes.get(source));
		searchedNodes.add(source);
		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.remove();
			IntervalTree<Interval> cabsSet = node.getCabsSet();
			Interval searchInterval = new Interval(rideRequest.getTimeStart(),
					rideRequest.getTimeEnd(), null);
			Iterable<Interval> intervals = cabsSet.keys(searchInterval);
			for (Interval interval : intervals) {
				// System.out.println("Int No: " + intervalCount);
				Cab cab = interval.getCab();
				boolean feasible = true;
				if (now < interval.getStart()) {
					int actualArrivalTimeOfCab = rideRequest.getTimeStart()
							+ (interval.getStart() - now);
					int[][] costMatrix = model.getCost();
					int distFromSrcToNode = costMatrix[source][node
							.getNodeNumber()];
					actualArrivalTimeOfCab += (distFromSrcToNode * SchedulerConstants.MINUTES_PER_KM);
					if (actualArrivalTimeOfCab > rideRequest.getTimeEnd()) {
						feasible = false;
					}
				}
				if (feasible) {
					if (!cab.isPickingAnyone()
							&& (cab.getCapacity() > cab.getPassengers())) {
						found = true;
						double timeInstant = rideRequest.getTimeStart();
						if (cab.isDroppingAnyone()) {
							System.out
									.println("DROPPING SOMEONE! DROPPING SOMEONE! DROPPING SOMEONE!");
							found = checkIfPathIsSubPathAndScheduleARide(cab,
									rideRequest, model, interval, cabsSet,
									timeInstant, node, eventQueue);
							if (found) {
								cab.setPassengers(cab.getPassengers() + 1);
							}
						} else {
							if (node.getNodeNumber() != source) {
								timeInstant = scheduleAPickUp(cab, model,
										rideRequest, eventQueue, interval,
										cabsSet, node.getNodeNumber(), source,
										timeInstant);
							}
							scheduleARide(cab, model, rideRequest, eventQueue,
									timeInstant, cabsSet, interval);
							cab.setPassengers(cab.getPassengers() + 1);
						}
					}
				}
				if (found) {
					break;
				}
			}
			// Not found, find neighbors of this node and add to Queue
			if (!found) {
				SortedMap<Integer, List<Neighbour>> adjNodes = node
						.getAdjacentNodes();
				for (int nodeDist : adjNodes.keySet()) {
					List<Neighbour> neighbours = adjNodes.get(nodeDist);
					for (Neighbour neighbour : neighbours) {
						int[][] costMatrix = model.getCost();
						int distFromSrcToNode = costMatrix[source][neighbour
								.getNodeNumber()];
						// Add only if not searched already and the distance is
						// less than or equal to maxDist
						if (!searchedNodes.contains(neighbour.getNodeNumber())
								&& maxDistOfCab >= distFromSrcToNode) {
							Node newNode = nodes.get(neighbour.getNodeNumber());
							nodeQueue.add(newNode);
							searchedNodes.add(neighbour.getNodeNumber());
						}
					}
				}
			} else {
				break;
			}
		}
		return found;
	}

	private boolean checkIfPathIsSubPathAndScheduleARide(Cab cab,
			RideRequest rideRequest, DialARideModel model, Interval interval,
			IntervalTree<Interval> cabsSet, double timeInstant, Node node,
			Queue<Event> eventQueue) {
		List<Integer> myPath = new ArrayList<Integer>();
		List<IntermediateNode> thatCabsPath = cab.getPath();

		int[][] prev = model.getPrev();
		Stack<Integer> revPath = new Stack<Integer>();
		int d = rideRequest.getDestination();
		int src = rideRequest.getSource();

		while (d != src) {
			revPath.add(d);
			d = prev[src][d];
		}

		while (!revPath.isEmpty()) {
			myPath.add(revPath.pop());
		}

		printPath("MyPath: ", myPath);
		printPathNode("ThatCabsPath: ", thatCabsPath);

		int noOfHops = myPath.size();
		int finalNode = myPath.get(noOfHops - 1);
		boolean isSubPath = false;
		for (IntermediateNode hop : thatCabsPath) {
			if (hop.getNodeNumber() == finalNode) {
				isSubPath = true;
				break;
			}
		}

		boolean cabFoundInSrc = true;
		if (node.getNodeNumber() != rideRequest.getSource()) {
			cabFoundInSrc = false;
		}

		if (isSubPath) {
			// Do Scheduling & delete entry in PQ
			System.out.println("Sub Path found while Dropping 1");

			doThePickupWhileServingSomeoneAndServeAll(model, model.getNodes(),
					rideRequest, eventQueue, timeInstant,
					rideRequest.getSource(), cab, node.getNodeNumber(),
					cabFoundInSrc, cabsSet, interval);
		} else {
			noOfHops = thatCabsPath.size();
			finalNode = thatCabsPath.get(noOfHops - 1).getNodeNumber();

			for (int hop : myPath) {
				if (hop == finalNode) {
					isSubPath = true;
					break;
				}
			}
			if (isSubPath) {
				// Do Scheduling
				doThePickupWhileServingSomeoneAndServeAllWhenMyStopIsLastStop(
						model, model.getNodes(), rideRequest, eventQueue,
						timeInstant, rideRequest.getSource(), cab,
						node.getNodeNumber(), cabFoundInSrc, cabsSet, interval);
				System.out.println("Sub Path found while Dropping 2");
			} else {
				System.out
						.println("Sub Path NOT found while Dropping, DISCARDED the cab!");
			}
		}

		return isSubPath;
	}

	private void doThePickupWhileServingSomeoneAndServeAllWhenMyStopIsLastStop(
			DialARideModel model, List<Node> nodes, RideRequest rideRequest,
			Queue<Event> eventQueue, double currentTime, int pickUpDestination,
			Cab scheduledCab, int sourceOfVehicle, boolean cabFoundInSrc,
			IntervalTree<Interval> cabsSet, Interval interval) {
		int actualArrivalTimeOfCab = 0;
		Interval deleteInterval = new Interval(0, 1440, scheduledCab);
		cabsSet.softDelete(deleteInterval);

		scheduledCab.setVersion(scheduledCab.getVersion() + 1);
		if (!cabFoundInSrc) {
			scheduledCab.setPickingAnyone(true);

			actualArrivalTimeOfCab = rideRequest.getTimeStart()
					+ (interval.getStart() - (int) Math.ceil(currentTime));
			int[][] costMatrix = model.getCost();
			int distFromSrcToNode = costMatrix[sourceOfVehicle][pickUpDestination];
			actualArrivalTimeOfCab += (distFromSrcToNode * SchedulerConstants.MINUTES_PER_KM);

			scheduledCab.setCurrentNode(pickUpDestination);
			// Present only for an instant to pick up
			Node pickUpNode = model.getNodes().get(pickUpDestination);
			pickUpNode.getCabsSet().add(
					new Interval((int) Math.ceil(actualArrivalTimeOfCab),
							(int) Math.ceil(actualArrivalTimeOfCab),
							scheduledCab));
			Event event = new Event(EventTypes.REACHED_PICKUP_NODE,
					scheduledCab, rideRequest,
					Math.ceil(actualArrivalTimeOfCab), pickUpDestination,
					scheduledCab.getVersion());
			eventQueue.add(event);
		}

		List<IntermediateNode> intermediateNodes = new ArrayList<IntermediateNode>();
		for (IntermediateNode intermed : scheduledCab.getPath()) {
			intermediateNodes.add(intermed);
		}
		scheduledCab.getPath().clear();

		Interval removeInterval = new Interval(0, 1440, scheduledCab);
		int pathSource = rideRequest.getSource();
		double scheduleTime = actualArrivalTimeOfCab;
		for (IntermediateNode intermed : intermediateNodes) {
			if (intermed.getNodeNumber() == rideRequest.getDestination()) {
				intermed.setDropCount(intermed.getDropCount() + 1);
			}
			IntervalTree<Interval> it = nodes.get(intermed.getNodeNumber())
					.getCabsSet();
			it.softDelete(removeInterval);

			if (intermed.getDropCount() > 0) {
				scheduleTime = scheduleARideWithOtherPassengers(scheduledCab, model,
						pathSource, intermed.getNodeNumber(), eventQueue,
						scheduleTime, intermed.getDropCount(), false,
						rideRequest);
				pathSource = intermed.getNodeNumber();
			}
		}

		scheduleARideWithOtherPassengers(scheduledCab, model, pathSource,
				rideRequest.getDestination(), eventQueue,
				scheduleTime, 1, true, rideRequest);

	}

	private void doThePickupWhileServingSomeoneAndServeAll(
			DialARideModel model, List<Node> nodes, RideRequest rideRequest,
			Queue<Event> eventQueue, double currentTime, int pickUpDestination,
			Cab scheduledCab, int sourceOfVehicle, boolean cabFoundInSrc,
			IntervalTree<Interval> cabsSet, Interval interval) {

		int actualArrivalTimeOfCab = 0;
		Interval deleteInterval = new Interval(0, 1440, scheduledCab);
		cabsSet.softDelete(deleteInterval);

		scheduledCab.setVersion(scheduledCab.getVersion() + 1);
		if (!cabFoundInSrc) {
			scheduledCab.setPickingAnyone(true);

			actualArrivalTimeOfCab = rideRequest.getTimeStart()
					+ (interval.getStart() - (int) Math.ceil(currentTime));
			int[][] costMatrix = model.getCost();
			int distFromSrcToNode = costMatrix[sourceOfVehicle][pickUpDestination];
			actualArrivalTimeOfCab += (distFromSrcToNode * SchedulerConstants.MINUTES_PER_KM);

			scheduledCab.setCurrentNode(pickUpDestination);
			// Present only for an instant to pick up
			Node pickUpNode = model.getNodes().get(pickUpDestination);
			pickUpNode.getCabsSet().add(
					new Interval((int) Math.ceil(actualArrivalTimeOfCab),
							(int) Math.ceil(actualArrivalTimeOfCab),
							scheduledCab));
			Event event = new Event(EventTypes.REACHED_PICKUP_NODE,
					scheduledCab, rideRequest,
					Math.ceil(actualArrivalTimeOfCab), pickUpDestination,
					scheduledCab.getVersion());
			eventQueue.add(event);
		}

		List<IntermediateNode> intermediateNodes = new ArrayList<IntermediateNode>();
		for (IntermediateNode intermed : scheduledCab.getPath()) {
			intermediateNodes.add(intermed);
		}
		scheduledCab.getPath().clear();

		Interval removeInterval = new Interval(0, 1440, scheduledCab);
		int pathSource = rideRequest.getSource();
		int intSize = intermediateNodes.size();
		int count = 1;
		double scheduleTime = actualArrivalTimeOfCab;
		for (IntermediateNode intermed : intermediateNodes) {
			if (intermed.getNodeNumber() == rideRequest.getDestination()) {
				intermed.setDropCount(intermed.getDropCount() + 1);
			}
			IntervalTree<Interval> it = nodes.get(intermed.getNodeNumber())
					.getCabsSet();
			it.softDelete(removeInterval);

			if (intermed.getDropCount() > 0) {
				boolean isLast = false;
				if (count == intSize) {
					isLast = true;
				}
				scheduleTime = scheduleARideWithOtherPassengers(scheduledCab, model,
						pathSource, intermed.getNodeNumber(), eventQueue,
						scheduleTime, intermed.getDropCount(),
						isLast, rideRequest);
				pathSource = intermed.getNodeNumber();
			}
			count += 1;
		}

	}

	private void printPath(String msg, List<Integer> hops) {
		System.out.print(msg);
		for (int hop : hops) {
			System.out.print(hop + " ");
		}
		System.out.println();
	}

	private void printPathNode(String msg, List<IntermediateNode> hops) {
		System.out.print(msg);
		for (IntermediateNode hop : hops) {
			System.out.print(hop.getNodeNumber() + " ");
		}
		System.out.println();
	}

	private void scheduleARide(Cab scheduledCab, DialARideModel model,
			RideRequest rideRequest, Queue<Event> eventQueue, double timeStart,
			IntervalTree<Interval> cabsSet, Interval interval) {

		List<Node> nodes = model.getNodes();
		Stack<Integer> revPath = new Stack<Integer>();
		int[][] prev = model.getPrev();
		double timeInstant = timeStart;
		int d = rideRequest.getDestination();
		int src = rideRequest.getSource();

		while (d != src) {
			revPath.add(d);
			d = prev[src][d];
		}

		int dest = -1;

		int pathSize = revPath.size();
		int intermediateNodeNo = 1;

		src = rideRequest.getSource();
		scheduledCab.getPath().clear();

		cabsSet.softDelete(interval);
		while (!revPath.isEmpty()) {
			dest = revPath.pop();
			int val = 0;
			Node node = nodes.get(src);
			Neighbour neighbour = node.getNeighbours().get(dest);
			if (null != neighbour) {
				val = neighbour.getDistance();
			}
			double timeReq = val * SchedulerConstants.MINUTES_PER_KM;
			timeInstant += timeReq;
			Node pickUpNode = model.getNodes().get(dest);
			pickUpNode.getCabsSet().add(
					new Interval((int) Math.ceil(timeInstant), (int) Math
							.ceil(timeInstant), scheduledCab));
			Event event = null;
			if (intermediateNodeNo == pathSize) {
				event = new Event(EventTypes.REACHED_TARGET_NODE, scheduledCab,
						rideRequest, Math.ceil(timeInstant), dest,
						scheduledCab.getVersion());
				scheduledCab.getPath().add(new IntermediateNode(dest, 1));
			} else {
				event = new Event(EventTypes.REACHED_NODE, scheduledCab,
						rideRequest, Math.ceil(timeInstant), dest,
						scheduledCab.getVersion());
				scheduledCab.getPath().add(new IntermediateNode(dest, 0));
			}

			eventQueue.add(event);
			src = dest;
			intermediateNodeNo += 1;
			scheduledCab.setDroppingAnyone(true);
		}

	}

	private double scheduleARideWithOtherPassengers(Cab scheduledCab,
			DialARideModel model, int source, int destination,
			Queue<Event> eventQueue, double timeStart, int dropCount,
			boolean isLast, RideRequest rideRequest) {

		List<Node> nodes = model.getNodes();
		Stack<Integer> revPath = new Stack<Integer>();
		int[][] prev = model.getPrev();
		double timeInstant = timeStart;
		int d = destination;
		int src = source;

		while (d != src) {
			revPath.add(d);
			d = prev[src][d];
		}

		int dest = -1;

		int pathSize = revPath.size();
		int intermediateNodeNo = 1;

		src = source;

		while (!revPath.isEmpty()) {
			dest = revPath.pop();
			int val = 0;
			Node node = nodes.get(src);
			Neighbour neighbour = node.getNeighbours().get(dest);
			if (null != neighbour) {
				val = neighbour.getDistance();
			}
			double timeReq = val * SchedulerConstants.MINUTES_PER_KM;
			timeInstant += timeReq;
			Node pickUpNode = model.getNodes().get(dest);
			pickUpNode.getCabsSet().add(
					new Interval((int) Math.ceil(timeInstant), (int) Math
							.ceil(timeInstant), scheduledCab));
			Event event = null;
			if (intermediateNodeNo == pathSize) {
				for (int ctr = 0; ctr < dropCount; ctr += 1) {
					if (isLast) {
						event = new Event(EventTypes.REACHED_TARGET_NODE,
								scheduledCab, rideRequest,
								Math.ceil(timeInstant), dest,
								scheduledCab.getVersion());
					} else {
						event = new Event(
								EventTypes.REACHED_PARTIAL_TARGET_NODE,
								scheduledCab, rideRequest,
								Math.ceil(timeInstant), dest,
								scheduledCab.getVersion());
					}
				}
				scheduledCab.getPath().add(
						new IntermediateNode(dest, dropCount));
			} else {
				event = new Event(EventTypes.REACHED_NODE, scheduledCab, null,
						Math.ceil(timeInstant), dest, scheduledCab.getVersion());
				scheduledCab.getPath().add(new IntermediateNode(dest, 0));
			}

			eventQueue.add(event);
			src = dest;
			intermediateNodeNo += 1;
			scheduledCab.setDroppingAnyone(true);
		}
		return timeInstant; 
	}

	private double scheduleAPickUp(Cab scheduledCab, DialARideModel model,
			RideRequest rideRequest, Queue<Event> eventQueue,
			Interval interval, IntervalTree<Interval> cabsSet,
			int sourceOfVehicle, int pickUpDestination, double currentTime) {
		List<Node> nodes = model.getNodes();
		Stack<Integer> revPath = new Stack<Integer>();
		int[][] prev = model.getPrev();
		double timeInstant = currentTime;
		int d = pickUpDestination;

		scheduledCab.setPickingAnyone(true);

		Interval deleteInterval = new Interval(0, 1440, scheduledCab);
		nodes.get(sourceOfVehicle).getCabsSet().softDelete(deleteInterval);

		while (d != sourceOfVehicle) {
			revPath.add(d);
			d = prev[sourceOfVehicle][d];
		}
		int src = sourceOfVehicle;
		int dest = -1;

		while (!revPath.isEmpty()) {
			dest = revPath.pop();
			int val = 0;
			Neighbour neighbour = nodes.get(src).getNeighbours().get(dest);
			if (null != neighbour) {
				val = neighbour.getDistance();
			}
			double timeReq = val * SchedulerConstants.MINUTES_PER_KM;
			timeInstant += timeReq;
			src = dest;
			// TODO: add support to schedule while pickup
		}
		scheduledCab.setCurrentNode(pickUpDestination);
		// Present only for an instant to pick up
		Node pickUpNode = model.getNodes().get(pickUpDestination);
		pickUpNode.getCabsSet().add(
				new Interval((int) Math.ceil(timeInstant), (int) Math
						.ceil(timeInstant), scheduledCab));
		Event event = new Event(EventTypes.REACHED_PICKUP_NODE, scheduledCab,
				rideRequest, Math.ceil(timeInstant), pickUpDestination,
				scheduledCab.getVersion());
		eventQueue.add(event);
		return timeInstant;
	}

	private String printIT(IntervalTree<Interval> cabsSet) {
		StringBuilder sb = new StringBuilder();
		Iterable<Interval> intall = cabsSet.keys(new Interval(0, 1440, null));
		for (Interval intv : intall) {
			Cab cab = intv.getCab();
			sb.append(cab.getCabNo()).append(" (").append(intv.getStart())
					.append(",").append(intv.getEnd()).append(") ");
		}
		return sb.toString();
	}
}
