import org.iiitb.dialaride.controller.DialARideController;

public class ProgramTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DialARideController dialARideController = new DialARideController();
		dialARideController.readDataFromFile(args[0]);
	}

}