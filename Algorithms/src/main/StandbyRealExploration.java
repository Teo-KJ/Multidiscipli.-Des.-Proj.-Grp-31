package main;

import communications.TCPComm;
import entities.Coordinate;

public class StandbyRealExploration implements Runnable {

	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");

		System.out.println("Waiting for Bluetooth to send START E...");

		/* STRICTLY Wait for Android to send START EXPLORATION command */
		while (true){
			if (Main.comms.readFrom(TCPComm.BLUETOOTH).equals("E"))
				break;
		}
		
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}
