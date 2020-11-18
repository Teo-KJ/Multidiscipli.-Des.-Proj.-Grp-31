package python_output_tester;
import java.io.*;
import communications.TCPComm;
import entities.*;
import main.Main;

public class Tester {
	
	public static String obj_id = null;
	public static boolean objFlag = false;
	public static boolean isReceived = false;
	public static Coordinate imageCoordinate;
	
	
	public void runPython(){
		ProcessBuilder builder = new ProcessBuilder("py", "-u", "C:\\Users\\lowbe\\Documents\\tensorflow1\\models\\research\\object_detection\\final_server_ben.py");
		builder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try{
			while ((obj_id = reader.readLine()) != null){
				Main.findImage = false;
				if (obj_id != null || obj_id.length()!= 0) {
					isReceived = Receiver.msgReceived();
					objFlag = true;
					Main.findImage = true;
					System.out.println(obj_id);
					switch(Main.robot.getCurrDir()){
						case 0://north
							for (int i = Main.robot.getCurrPos().getX(); i > 0; i--){
								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY(), i)).getCellType()== Cell.WALL){
									this.imageCoordinate.setY(Main.robot.getCurrPos().getY());
									this.imageCoordinate.setX(i);
//									System.out.println(":: Object Detected, the object is at (" + i + ", " + Main.robot.getCurrPos().getY() + ") ::");
//									System.out.println(":: Object Detected, the object id is " + obj_id + "::" );
//									Main.comms.send(TCPComm.BLUETOOTH, obj_id);
									break;
								}
							}
						case 1://east
							for (int i = Main.robot.getCurrPos().getY(); i < Map.maxY; i++){
								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY(), i)).getCellType()== Cell.WALL){
									this.imageCoordinate.setY(i);
									this.imageCoordinate.setX(Main.robot.getCurrPos().getX());
//									System.out.println(":: Object Detected, the object is at (" + Main.robot.getCurrPos().getX() +", " + i +  ") ::");
//									Main.comms.send(TCPComm.BLUETOOTH, String.valueOf(Main.robot.getCurrPos().getX()) +":" + String.valueOf(i));
//									System.out.println(":: Object Detected, the object id is " + obj_id+ "::" );
//									Main.comms.send(TCPComm.BLUETOOTH, obj_id);
									break;
								}
							}
						case 2://south
							for (int i = Main.robot.getCurrPos().getX(); i < Map.maxX; i++){
								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY(), i)).getCellType()== Cell.WALL){
									this.imageCoordinate.setY(Main.robot.getCurrPos().getY());
									this.imageCoordinate.setX(i);
//									System.out.println(":: Object Detected, the object is at (" + Main.robot.getCurrPos().getX() +", " + i +  ") ::");
//									System.out.println(":: Object Detected, the object id is " + obj_id + "::" );
//									Main.comms.send(TCPComm.BLUETOOTH, String.valueOf(i) +":" + String.valueOf(Main.robot.getCurrPos().getY()));
//									Main.comms.send(TCPComm.BLUETOOTH, obj_id);
									break;
								}
							}
						case 3://west
							for (int i = Main.robot.getCurrPos().getY(); i > 0; i++){
								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY(), i)).getCellType()== Cell.WALL){
									this.imageCoordinate.setY(i);
									this.imageCoordinate.setX(Main.robot.getCurrPos().getX());
//									System.out.println(":: Object Detected, the object is at (" + Main.robot.getCurrPos().getX() +", " + i +  ") ::");
//									Main.comms.send(TCPComm.BLUETOOTH, String.valueOf(Main.robot.getCurrPos().getX()) +":" + String.valueOf(i));
//									System.out.println(":: Object Detected, the object id is " + obj_id + "::" );
//									Main.comms.send(TCPComm.BLUETOOTH, obj_id);
									break;
								}
							}
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
}
