package org.iiitb.dialaride.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.iiitb.dialaride.controller.utils.FileParser;
import org.iiitb.dialaride.model.DialARideModel;
import org.iiitb.dialaride.model.bean.Node;
import org.iiitb.dialaride.model.bean.Path;
import org.iiitb.dialaride.model.bean.RideRequest;
import org.iiitb.dialaride.model.util.CabScheduler;
import org.iiitb.dialaride.model.util.ShortestPathDijkstra;

public class DialARideController {

	private DialARideModel model;

	public List<Integer> testModule(DialARideModel model) {
		SortedMap<Integer, List<RideRequest>> rideRequests = model
				.getRideRequests();
		Set<Integer> vals = new HashSet<Integer>();
		int total = 0;
		for (Integer timeStart : rideRequests.keySet()) {
			List<RideRequest> rideReqs = rideRequests.get(timeStart);
			for (RideRequest rideRequest : rideReqs) {
				vals.add(model.getCost()[rideRequest.getSource()][rideRequest
						.getDestination()]);
				total += model.getCost()[rideRequest.getSource()][rideRequest
				                          						.getDestination()];
			}
		}
		System.out.println("Max Possible value: " + total);
		List<Integer> distVals = new ArrayList<Integer>(vals);
		Collections.sort(distVals);
		int ctr = 0;
		for (Integer value : distVals) {
			System.out.println(ctr + ". " + value);
			ctr += 1;
		}
		return distVals;
	}

	public void readDataFromFile(String filename) {
		FileParser fileParser = new FileParser();
		File file = new File(filename);
		try {
			model = fileParser.parseDataFromFile(file);
			ShortestPathDijkstra dijkstra = new ShortestPathDijkstra();
			dijkstra.computeShortestPaths(model);
			// System.out.println(model);
			List<Integer> distVals = testModule(model);
			for (Integer thresholdValue : distVals) {
				CabScheduler cabScheduler = new CabScheduler();
				cabScheduler.schedule(model, thresholdValue);
	
				int counter = 1;
	//			for (Integer cabNo : model.getCabPath().keySet()) {
	//				List<Path> pathList = model.getCabPath().get(cabNo);
	//				StringBuilder sb = new StringBuilder();
	//				sb.append(counter).append(": ");
	//				for (Path path : pathList) {
	//					sb.append("(").append(path.getNodeNumber()).append(", ")
	//							.append(path.getTimeInstant()).append(", ")
	//							.append(path.getEventTypes()).append(") ");
	//				}
	//				System.out.println(sb.toString());
	//				counter += 1;
	//			}
	
				model = fileParser.parseDataFromFile(file);
				dijkstra.computeShortestPaths(model);
				System.out.println();
			 }
			// System.out.println(model);
			for (Node node : model.getNodes()) {
				// System.out.println(node);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
