package main;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Node;
import entities.Robot;

import java.util.ArrayList;

public class StandbyWaypoint implements Runnable {
	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		ArrayList<Node> finalPath = new ArrayList<Node>();
		// Start to Waypoint
		
		/* True running using the explored Map.*/

		System.out.println("Waiting for Bluetooth to send Waypoint...");

		/* STRICTLY Wait for Android to send START EXPLORATION command */
		

		while(true) {
			String received = Main.comms.readFrom(TCPComm.BLUETOOTH);
			if (received != null) {
				String wayPoint[]=received.split(":");
				if (wayPoint[0].toString().equals("WP")) {
					Coordinate wp = new Coordinate(Integer.parseInt(wayPoint[2]), Integer.parseInt(wayPoint[1]));
					Main.exploredMap.setWaypoint(wp);
//					Main.gui.refreshGUI(Main.robot,Main.exploredMap);
				}
				break;
			}
		}
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}

}
