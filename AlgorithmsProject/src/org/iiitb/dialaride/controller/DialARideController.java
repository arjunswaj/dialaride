package org.iiitb.dialaride.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.iiitb.dialaride.controller.utils.FileParser;
import org.iiitb.dialaride.model.DialARideModel;
import org.iiitb.dialaride.model.bean.Node;
import org.iiitb.dialaride.model.util.CabScheduler;
import org.iiitb.dialaride.model.util.ShortestPathDijkstra;

public class DialARideController {

	private DialARideModel model;

	public void readDataFromFile(String filename) {
		FileParser fileParser = new FileParser();
		File file = new File(filename);
		try {
			model = fileParser.parseDataFromFile(file);
			ShortestPathDijkstra dijkstra = new ShortestPathDijkstra();
			dijkstra.computeShortestPaths(model);
			//System.out.println(model);		
			CabScheduler cabScheduler = new CabScheduler();
			cabScheduler.schedule(model);
			//System.out.println(model);
			for (Node node : model.getNodes()) {
				System.out.println(node);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
