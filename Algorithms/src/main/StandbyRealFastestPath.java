package main;

import java.util.ArrayList;
import java.util.LinkedList;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Node;
import entities.Robot.Rotate;

public class StandbyRealFastestPath implements Runnable {

	private FastestPath fp = Main.fp;

	/**
	 * Standby <tt>Robot</tt> for the Fastest Path run. Assumes <tt>Robot</tt> is at start position (1, 1) facing
	 * South.<br>
	 * <ol>
	 * <li>Run calibration</li>
	 * <li>Rotate to direction of fastest path</li>
	 * </ol>
	 */
	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		ArrayList<Node> finalPath = new ArrayList<Node>();
		
		this.fp = new FastestPath(Main.exploredMap, Main.exploredMap.getStartCoord(), Main.exploredMap.getWaypoint());
		finalPath.addAll(this.fp.runAStar());	// Append
		this.fp = new FastestPath(Main.exploredMap, Main.exploredMap.getWaypoint(), Main.exploredMap.getGoalCoord());
		finalPath.addAll(this.fp.runAStar());
		if (this.fp.canFind == false){
			this.fp = new FastestPath(Main.exploredMap, Main.exploredMap.getStartCoord(), Main.exploredMap.getGoalCoord());
			finalPath.addAll(this.fp.runAStar());	// Append
		}

		Main.exploredMap.finalPathReveal(finalPath);

		Main.gui.refreshGUI(Main.robot, Main.exploredMap);
		
		/*Testing fastest path using the tested map real_maze.txt */
//		this.fp = new FastestPath(Main.testedMap, Main.testedMap.getStartCoord(), Main.testedMap.getWaypoint());
//		finalPath.addAll(this.fp.runAStar());	// Append
//		// Waypoint to Goal
//		this.fp = new FastestPath(Main.testedMap, Main.testedMap.getWaypoint(), Main.testedMap.getGoalCoord());
//		finalPath.addAll(this.fp.runAStar());
//
////		Main.fp = fp;
//
//		Main.testedMap.finalPathReveal(finalPath);
//
//		Main.gui.refreshGUI(Main.robot, Main.testedMap);

		
		
		System.out.println("Waiting for Bluetooth to send the start signal of the fastest path...");
		
		while (true){
			if (Main.comms.readFrom(TCPComm.BLUETOOTH).contains("FP")){
//				Main.comms.sendFastestPath(this.fp.navigateSteps(finalPath));
				Cell secondCell = finalPath.get(1).getCell();		// Second step
//
				try {
					// Fastest Path goes North
					if (secondCell.getY() == 2 && secondCell.getX() == 1) {
						Main.comms.send(TCPComm.BLUETOOTH, "RP:1:1:N");
						Thread.sleep(1000);
						Main.comms.send(TCPComm.ARDUINO, "L");
						Thread.sleep(100);
						Main.comms.sendFastestPathOnebyOne(this.fp.navigateSteps(finalPath));
						Main.gui.refreshGUI(Main.robot, Main.exploredMap);
//
					}
					// Fastest Path goes East
					else{
						Main.comms.send(TCPComm.BLUETOOTH, "RP:1:1:E");
						Thread.sleep(1000);
						Main.comms.sendFastestPathOnebyOne(this.fp.navigateSteps(finalPath));
						
					}
				} catch (Exception e) {
				}
				break;
			}
		}
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}
