import org.iiitb.dialaride.controller.DialARideController;

public class MT2013026 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DialARideController dialARideController = new DialARideController();
		dialARideController.readDataFromFile(args[0]);
	}

}
