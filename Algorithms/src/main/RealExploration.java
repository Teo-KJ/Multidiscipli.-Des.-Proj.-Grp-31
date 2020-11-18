package main;

import java.util.Scanner;

import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import python_output_tester.Tester;
public class RealExploration implements Runnable {
	
	private boolean done = false;
	@Override
	public void run() {
		//send S, receive sensor value, run actual reveal, receive A, print end step
		Coordinate waypoint = Main.exploredMap.getWaypoint();
		Main.exploredMap.getCell(waypoint).setCellType(Cell.UNKNOWN);
		Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		Main.comms.send(TCPComm.ARDUINO, "Z");
		String fromArduino = Main.comms.readFrom(TCPComm.ARDUINO); // Wait for reading
		Main.exploredMap.actualReveal(Main.robot, fromArduino);
		Main.explorer.executeOneStep(Main.robot, Main.exploredMap);
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
					done = Main.explorer.executeOneStep(Main.robot, Main.exploredMap);	// Send next movement
					Main.gui.refreshGUI(Main.robot, Main.exploredMap);					// Show it on GUI
					
					
					System.out.println(
							"Robot is at: " + Main.robot.getCurrPos().getY() + " " + Main.robot.getCurrPos().getX());
					System.out.println("============= END STEP =============\n");
					Main.comms.send(TCPComm.BLUETOOTH, Main.exploredMap.getP1Descriptors() + ":" + Main.exploredMap.getP2Descriptors() + ":" +
							                                   Main.robot.getCurrPos().getX() + ":" + Main.robot.getCurrPos().getY() + ":" + Main.robot.changeFormOfDirection(Main.robot.getCurrDir()));
					fromArduinoA = Main.comms.readFrom(TCPComm.ARDUINO);
					if (Main.robot.getCurrPos().getX() == 1 && Main.robot.getCurrPos().getY() == 1) {
						Main.exploredMap.getCell(waypoint).setCellType(Cell.WAYPOINT);
						Main.gui.refreshGUI(Main.robot, Main.exploredMap);
						break;
					}
				}
//			}
		} catch (Exception e) {
			// IGNORE ALL INTERRUPTS TO THIS THREAD.
			// EXPLORATION MUST RUN TO THE END.
		}
		Main.comms.send(TCPComm.ARDUINO, "G");
		if (Main.comms.readFrom(TCPComm.ARDUINO) == "G"){
//			Main.exploredMap.selfCalibration(Main.robot);
//			Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		}
		Main.comms.send(TCPComm.BLUETOOTH, "Explored");
		Main.gui.refreshGUI(Main.robot,Main.exploredMap);
		
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
		
	}
}
