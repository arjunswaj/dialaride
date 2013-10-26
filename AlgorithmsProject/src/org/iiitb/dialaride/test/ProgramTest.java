package org.iiitb.dialaride.test;

import org.iiitb.dialaride.controller.DialARideController;

public class ProgramTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DialARideController dialARideController = new DialARideController();
		dialARideController.readDataFromFile("data4.txt");
	}

}
