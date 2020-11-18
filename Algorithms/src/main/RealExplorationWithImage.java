package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import python_output_tester.ReadOutput;
import python_output_tester.ImageDisplay;
public class RealExplorationWithImage implements Runnable{
	private boolean done = false;
	private ReadOutput read = new ReadOutput();


	@Override
	public void run() {
		ImageDisplay lastImage = new ImageDisplay();

		//send S, receive sensor value, run actual reveal, receive A, print end step
		ReadOutput.readString("object_detection.txt");
		Coordinate waypoint = Main.exploredMap.getWaypoint();
		Main.exploredMap.getCell(waypoint).setCellType(Cell.UNKNOWN);
		Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		Main.comms.send(TCPComm.ARDUINO, "Z");
		Main.comms.send(TCPComm.ARDUINO,"L");
		String fromArduino = Main.comms.readFrom(TCPComm.ARDUINO); // Wait for reading
//		Main.exploredMap.actualReveal(Main.robot, fromArduino);
//		Main.explorer.executeOneStep(Main.robot, Main.exploredMap);
		
		Main.gui.refreshGUI(Main.robot, Main.exploredMap);
		System.out.println(
				"Robot is at: " + Main.robot.getCurrPos().getY() + " " + Main.robot.getCurrPos().getX());
		String fromArduinoA = Main.comms.readFrom(TCPComm.ARDUINO);
		if (fromArduinoA != null) {
			System.out.println("============= END STEP =============\n");
		}
		try {
			while (!done) {
				Main.comms.send(TCPComm.ARDUINO, "S\n");
				
				
				fromArduino = Main.comms.readFrom(TCPComm.ARDUINO);
				if (fromArduino.length() == 6){
					Main.exploredMap.actualReveal(Main.robot, fromArduino);
				}
				else{
					fromArduino = Main.comms.readFrom(TCPComm.ARDUINO);
					Main.exploredMap.actualReveal(Main.robot, fromArduino);
				}
				/* Run exploration for one step */
				if (Main.robot.getCurrPos().equals(new Coordinate(1,13)) && Main.robot.getCurrDir() == Robot.EAST){
					Main.comms.send(TCPComm.ARDUINO, "G");
					if(Main.comms.readFrom(TCPComm.ARDUINO).equals("G"))
						Thread.sleep(100);
				}
				else if (Main.robot.getCurrPos().equals(new Coordinate(18,13)) && Main.robot.getCurrDir() == Robot.NORTH){
					Main.comms.send(TCPComm.ARDUINO, "G");
					if(Main.comms.readFrom(TCPComm.ARDUINO).equals("G"))
						Thread.sleep(100);
				}
				else if (Main.robot.getCurrPos().equals(new Coordinate(18,1)) && Main.robot.getCurrDir() == Robot.WEST){
					Main.comms.send(TCPComm.ARDUINO, "G");
					if(Main.comms.readFrom(TCPComm.ARDUINO).equals("G"))
						Thread.sleep(100);
				}
				done = Main.explorer.executeOneStep(Main.robot, Main.exploredMap);	// Send next movement
				
				Main.gui.refreshGUI(Main.robot, Main.exploredMap);					// Show it on GUI

				
				System.out.println(
						"Robot is at: " + Main.robot.getCurrPos().getX() + " " + Main.robot.getCurrPos().getY());
				ReadOutput.readString("object_detection.txt");
				if (ReadOutput.isDetected == false){
					Main.comms.send(TCPComm.BLUETOOTH, Main.exploredMap.getP1Descriptors() + ":" + Main.exploredMap.getP2Descriptors() + ":" +
							                                   Main.robot.getCurrPos().getX() + ":" + Main.robot.getCurrPos().getY() + ":" + Main.robot.changeFormOfDirection(Main.robot.getCurrDir()));
				}
				else{
					Main.comms.send(TCPComm.BLUETOOTH, Main.exploredMap.getP1Descriptors() + ":" + Main.exploredMap.getP2Descriptors() + ":" +
							                                   Main.robot.getCurrPos().getX() + ":" + Main.robot.getCurrPos().getY() + ":" + Main.robot.changeFormOfDirection(Main.robot.getCurrDir())
													+ ":" + String.valueOf(Main.robot.getCurrPos().getX()) + ":" + String.valueOf(Main.robot.getCurrPos().getY()) + ":" + ReadOutput.obj_id);
//					Main.comms.send(TCPComm.BLUETOOTH, Main.exploredMap.getP1Descriptors() + ":" + Main.exploredMap.getP2Descriptors() + ":" +
//							Main.robot.getCurrPos().getX() + ":" + Main.robot.getCurrPos().getY() + ":" + Main.robot.changeFormOfDirection(Main.robot.getCurrDir())
//							+ ":" + String.valueOf(ReadOutput.imageCoordinate.getX()) + ":" + String.valueOf(ReadOutput.imageCoordinate.getY()) + ":" + ReadOutput.obj_id);
				}
				
				fromArduinoA = Main.comms.readFrom(TCPComm.ARDUINO);
				if (Main.robot.getCurrPos().getX() == 1 && Main.robot.getCurrPos().getY() == 1 && Main.robot.getCurrDir() == Robot.SOUTH) {
					Main.exploredMap.getCell(waypoint).setCellType(Cell.WAYPOINT);
					Main.gui.refreshGUI(Main.robot, Main.exploredMap);
					break;
				}
				else if (Main.robot.getCurrPos().getX() == 1 && Main.robot.getCurrPos().getY() == 1 && Main.robot.getCurrDir() == Robot.WEST){
					Main.exploredMap.getCell(waypoint).setCellType(Cell.WAYPOINT);
					Main.comms.send(TCPComm.ARDUINO, "L");
					Main.gui.refreshGUI(Main.robot, Main.exploredMap);
					break;
				}
				System.out.println("============= END STEP =============\n");
				System.out.println();
				Thread.sleep(500);
			}
//			}
		} catch (Exception e) {
			// IGNORE ALL INTERRUPTS TO THIS THREAD.
			// EXPLORATION MUST RUN TO THE END.
		}
		Main.comms.send(TCPComm.ARDUINO, "G");
		if (Main.comms.readFrom(TCPComm.ARDUINO).equals("G")) {
			Main.comms.send(TCPComm.ARDUINO, "L");
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.comms.send(TCPComm.ARDUINO, "H");
//			Main.exploredMap.selfCalibration(Main.robot);
//			Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.comms.send(TCPComm.BLUETOOTH, "Explored");
		Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		try {
			lastImage.DisplayImage("C:\\Users\\lowbe\\Documents\\tensorflow1\\tiled_img\\concat_detect.jpeg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
		
		
	}
}
