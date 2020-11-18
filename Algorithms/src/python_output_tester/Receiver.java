package python_output_tester;
import python_output_tester.Tester;

public class Receiver{
	public static boolean msgReceived() {
		System.out.println("String Received! Object Id = " + Tester.obj_id);
		Tester.obj_id = null;
		System.out.println("Terminating... obj_id = " + Tester.obj_id);
		return true;
	}
}