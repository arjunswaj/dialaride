package org.iiitb.dialaride.controller.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.iiitb.dialaride.model.DialARideModel;
import org.iiitb.dialaride.model.bean.Cab;
import org.iiitb.dialaride.model.bean.Neighbour;
import org.iiitb.dialaride.model.bean.Node;
import org.iiitb.dialaride.model.bean.Path;
import org.iiitb.dialaride.model.bean.RideRequest;
import org.iiitb.dialaride.model.datastructures.Interval;
import org.iiitb.dialaride.model.datastructures.consts.EventTypes;

public class FileParser {

	private static final String MINUS_ONE = "-1";

	public DialARideModel parseDataFromFile(File file)
			throws FileNotFoundException, IOException {
		int numOfNodes = 0;
		int numOfVehicles = 0;
		int capacityOfVehicles = 0;
		int noOfRequests = 0;

		DialARideModel model = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = bufferedReader.readLine();
			// System.out.println(line);
			StringTokenizer st = new StringTokenizer(line);
			numOfNodes = Integer.parseInt(st.nextToken());
			numOfVehicles = Integer.parseInt(st.nextToken());
			capacityOfVehicles = Integer.parseInt(st.nextToken());
			noOfRequests = Integer.parseInt(st.nextToken());
			/*
			 * System.out.println("numOfNodes: " + numOfNodes +
			 * " numOfVehicles: " + numOfVehicles + " capacityOfVehicles: " +
			 * capacityOfVehicles + " noOfRequests: " + noOfRequests);
			 */
			List<Node> nodes = new ArrayList<Node>(numOfNodes);
			Map<Integer, Set<Cab>> cabs = new HashMap<Integer, Set<Cab>>();
			SortedMap<Integer, List<RideRequest>> rideRequests = new TreeMap<Integer, List<RideRequest>>();
			Map<Integer, Cab> cabsLookUp = new HashMap<Integer, Cab>();
			Map<Integer, List<Path>> cabPath = new HashMap<Integer, List<Path>>();
			
			for (int index = 0; index < numOfNodes; index += 1) {
				Node mainNode = new Node(index);
				line = bufferedReader.readLine();
				// System.out.println(line);
				st = new StringTokenizer(line);
				int adjNodeNo = 0;
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (!token.equals(MINUS_ONE)) {
						int distance = Integer.parseInt(token);
						Neighbour neighbour = new Neighbour(adjNodeNo, distance);

						// First copy based on Node No
						Map<Integer, Neighbour> neighbours = mainNode
								.getNeighbours();
						neighbours.put(adjNodeNo, neighbour);

						// Second Copy sorted by distance in non decreasing
						// order
						SortedMap<Integer, List<Neighbour>> adjNodesSet = mainNode
								.getAdjacentNodes();
						List<Neighbour> adjNodes = adjNodesSet.get(distance);
						if (null == adjNodes) {
							adjNodes = new ArrayList<Neighbour>();
							adjNodesSet.put(distance, adjNodes);
						}
						adjNodes.add(neighbour);
					}
					adjNodeNo += 1;
				}
				nodes.add(mainNode);
			}

			line = bufferedReader.readLine();
			// System.out.println(line);
			st = new StringTokenizer(line);
			for (int index = 0; index < numOfVehicles; index += 1) {
				int cabNo = index + 1;
				int nodeNo = Integer.parseInt(st.nextToken()) - 1;
				// System.out.println("Cab No: " + cabNo + " nodeNo: " +
				// nodeNo);
				Cab cab = new Cab(cabNo, nodeNo, capacityOfVehicles, false, 1);
				cabsLookUp.put(cabNo, cab);
				Set<Cab> cabList = cabs.get(nodeNo);
				if (null == cabList) {
					cabList = new HashSet<Cab>();
					cabs.put(nodeNo, cabList);
				}
				cabList.add(cab);
				List<Path> pathList = new ArrayList<Path>();
				pathList.add(new Path(nodeNo, 0, EventTypes.HOME));
				cabPath.put(cabNo, pathList);
			}

			for (int index = 0; index < noOfRequests; index += 1) {
				line = bufferedReader.readLine();
				// System.out.println(line);
				st = new StringTokenizer(line);
				int source = Integer.parseInt(st.nextToken()) - 1;
				int destination = Integer.parseInt(st.nextToken()) - 1;
				int timeStart = Integer.parseInt(st.nextToken());
				int timeEnd = Integer.parseInt(st.nextToken());
				RideRequest rideRequest = new RideRequest(timeStart, timeEnd,
						source, destination);
				List<RideRequest> rideReqs = rideRequests.get(timeStart);
				if (null == rideReqs) {
					rideReqs = new ArrayList<RideRequest>();
					rideRequests.put(timeStart, rideReqs);
				}
				rideReqs.add(rideRequest);
			}

			for (Node node : nodes) {
				int nodeNumber = node.getNodeNumber();
				Set<Cab> cabSet = cabs.get(nodeNumber);
				if (null != cabSet) {
					int t = 0;
					for (Cab cab : cabSet) {						
						Interval interval = new Interval(t, t + 1440, cab);
						node.getCabsSet().add(interval);						
					}
				}
			}

			model = new DialARideModel(cabs, nodes, rideRequests, cabPath, cabsLookUp);
		} finally {
			if (null != bufferedReader) {
				bufferedReader.close();
			}
		}
		return model;
	}

}
