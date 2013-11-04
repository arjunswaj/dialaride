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
import org.iiitb.dialaride.model.bean.Cab;
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
    // System.out.println("Max Possible value: " + total);
    List<Integer> distVals = new ArrayList<Integer>(vals);
    Collections.sort(distVals);
    int ctr = 0;
    // for (Integer value : distVals) {
    // System.out.println(ctr + ". " + value);
    // ctr += 1;
    // }
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
      int revenue = 0;
      int optimalThresholdValue = 0;
      for (Integer thresholdValue : distVals) {
        CabScheduler cabScheduler = new CabScheduler();
        cabScheduler.schedule(model, thresholdValue);

        if (model.getMaxRevenue() > revenue) {
          revenue = model.getMaxRevenue();
          optimalThresholdValue = thresholdValue;
        }
        model = fileParser.parseDataFromFile(file);
        dijkstra.computeShortestPaths(model);
      }

      model = fileParser.parseDataFromFile(file);
      dijkstra.computeShortestPaths(model);
      CabScheduler cabScheduler = new CabScheduler();
      cabScheduler.schedule(model, optimalThresholdValue);

      int counter = 1;
      int sumOfRevenue = 0;
      for (Integer cabNo : model.getCabPath().keySet()) {
        List<Path> pathList = model.getCabPath().get(cabNo);
        Cab cab = model.getCabsLookUp().get(cabNo);
        StringBuilder sb = new StringBuilder();
        // sb.append(counter).append(": ");
        for (Path path : pathList) {
          sb.append(path.getNodeNumber()).append(" ")
              .append(path.getTimeInstant()).append(" ");
        }
        sb.append(" ").append(cab.getRevenue());
        sumOfRevenue += cab.getRevenue();
        System.out.println(sb.toString());
        counter += 1;
      }
      System.out.println(model.getMaxRevenue());
      // System.out.println("Successfully scheduled: " +
      // model.getSuccessfullyScheduledRequests()
      // + "\nRejected Requests: " + model.getRejectedRequests() + "\nRevenue: "
      // + model.getMaxRevenue());
      // System.out.println("Total travelled: " + model.getTotalDistance());

      // System.out.println("Summation: " + sumOfRevenue);
      // System.out.println(model);
      // for (Node node : model.getNodes()) {
      // System.out.println(node);
      // }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
