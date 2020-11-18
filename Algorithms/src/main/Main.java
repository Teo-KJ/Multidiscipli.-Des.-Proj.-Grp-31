package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


import algorithms.Exploration;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Coordinate;
import entities.Map;
import entities.Node;
import entities.Robot;
import gui.GUI;
import python_output_tester.ReadOutput;
import python_output_tester.ImageDisplay;
public class Main {

	/** Set Mode here **/
	public static boolean isRealRun;
	public static boolean isImageDetected;
	/* Shared Variables */
	public static Robot robot;
	public static Map exploredMap;
	public static Map testedMap;
	public static GUI gui;
	public static Exploration explorer;
	public static FastestPath fp;
	public static ReadOutput readOutput;
	public static boolean findImage = false;
	/* Simulation Only Variables */
	public static Map testMap;
	public static Thread tSimExplore;
	public static int percentage;
	public static int speed;
	public static int time;
	/* Real Run Only Variables */
	public static TCPComm comms;
	public static Thread tStandbyWaypoint;
	public static Thread tStandbyRealExplore;
	public static Thread tRealExplore;
	public static Thread tRealExplorewithImage;
	public static Thread tStandbyRealFastestPath;
	
	
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Do you want to run the algorithm on the robot?\n[y/n]");
		
		Scanner sc = new Scanner(System.in);
		char option = sc.nextLine().charAt(0);
//		sc.nextLine();
		if (option == 'y' || option == 'Y')
			isRealRun = true;
		else
			isRealRun = false;
//		if (isRealRun){
//			System.out.println("Do you want to run the algorithm for  Image Recognition Exploration?\n[y/n]");
//			Scanner sc2 = new Scanner(System.in);
//			char optionForImage = sc.nextLine().charAt(0);
//			if (optionForImage == 'y' || option == 'Y')
//				isImageDetected = true;
//			else
//				isImageDetected = false;
//
//		}
		isImageDetected = true;
		/* 1. Initialise Variables */
		robot = new Robot(isRealRun);			// Default starting position of robot (1,1 facing East)
		exploredMap = new Map("unknown.txt");	// Set exploredMap (starts from an unknown state)
		testedMap = new Map("real_maze.txt");
		/* 2. Initialise GUI */
		gui = new GUI(robot, exploredMap);//initial layout is the original layout
		
		/* 3. Initialise algorithms */
		explorer = new Exploration(exploredMap);	// Exploration algorithm

		/* 4. Check if current mode */
		/* ======================== REAL RUN MODE ======================== */
		if (isRealRun) {
			gui.setModeColour(false);
			comms = new TCPComm();		// Initialise TCP Communication (Will wait...)
			gui.setModeColour(comms.isConnected());
			
			try {
				Thread.sleep(2000);		// Raspberry Pi needs time to get ready
			} catch (Exception e) {
		}
			/** R1. Wait for Bluetooth to send WAYPOINT **/
			tStandbyWaypoint = new Thread(new StandbyWaypoint());
			tStandbyWaypoint.start();
			try {
				tStandbyWaypoint.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			/** R2. Wait for Bluetooth to send E **/
			tStandbyRealExplore = new Thread(new StandbyRealExploration());
			tStandbyRealExplore.start();

			try {
				tStandbyRealExplore.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/** R3. RUNNING REAL EXPLORATION **/
			if (isImageDetected == false){
			tRealExplore = new Thread(new RealExploration());
			tRealExplore.start();

			try {
				tRealExplore.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
			else{
				tRealExplorewithImage = new Thread(new RealExplorationWithImage());
				tRealExplorewithImage.start();

				try {
					tRealExplorewithImage.join();	// Wait until above thread ends...
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		 
//
			/** R4. RUNNING STANDBY FASTEST PATH and wait for Android to send STARTF... **/
			tStandbyRealFastestPath = new Thread(new StandbyRealFastestPath());
			tStandbyRealFastestPath.start();

			try {
				tStandbyRealFastestPath.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//
//			/** 5. 5END STRING TO SERIAL  **/
//			comms.send(TCPComm.ARDUINO, "FP");
		}

		/* ======================== SIMULATION MODE ======================== */
		else {
			// Load testMap
			/* 1. Initialise Variables again so the new map will be the simulatedMap */
			testMap = new Map("sample_whatsapp.txt");	// Set simulatedMap for use
			gui.refreshGUI(robot, testMap); 		// Display testMap first if simulation mode
		}
	}

	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void runSimExplorePerStep() {
//		System.out.println(Main.exploredMap.getP1Descriptors() + ":" + Main.exploredMap.getP2Descriptors() + ":" +
//				                   Main.robot.getCurrPos().getX() + ":" + Main.robot.getCurrPos().getY() + Main.robot.changeFormOfDirection(Main.robot.getCurrDir()));
//		exploredMap.actualReveal(robot, "PC,AR,000000");
		exploredMap.simulatedReveal(robot, exploredMap);
		gui.refreshGUI(robot, exploredMap);
		
		/* Run exploration for one step */
		boolean done = explorer.executeOneStep(robot, exploredMap);
		
//		exploredMap.actualReveal(robot, "PC,AR,222005");
		exploredMap.simulatedReveal(robot, exploredMap);
		gui.refreshGUI(robot, exploredMap);
		int percentage = exploredMap.countExplored();
		System.out.println("Has explored "+percentage+ "% of the map");
		if (done) {
			// Reset all objects to a clean state
			robot = new Robot(isRealRun);
			exploredMap = new Map("unknown.txt");
			explorer = new Exploration(exploredMap);
			gui.refreshGUI(robot, exploredMap);
		}
		
	}
	
	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Start new <tt>SimulateExploration</tt> thread when "Explore all" button is pressed.
	 */
	
	public static void runSimulator(){

		robot = new Robot(isRealRun);
		exploredMap = new Map("unknown.txt");
		explorer = new Exploration(exploredMap);
		gui.refreshGUI(robot, exploredMap);
		System.out.println("::Welcome to MDP-GROUP-31 Simulator!::");
		System.out.println("Input the percentage you want to set(0 by default): ");
		Scanner percentageScanner = new Scanner(System.in);
		percentage = Integer.parseInt(percentageScanner.nextLine());
		System.out.println("Input the speed of the exploration(X steps per second, 0 by default): ");
		Scanner speedScanner = new Scanner(System.in);
		speed = Integer.parseInt(speedScanner.nextLine());
		System.out.println("Input the time limit you want to set(by second, 0 by default): ");
		Scanner timeScanner = new Scanner(System.in);
		time = Integer.parseInt(timeScanner.nextLine()) * 1000;
		if (percentage == 0){
			if (speed == 0){
				if (tSimExplore == null || !tSimExplore.isAlive()) {
						tSimExplore = new Thread(new SimulateExploration());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
			}
			else{
				if (time == 0){
					if (tSimExplore == null || !tSimExplore.isAlive()) {
						tSimExplore = new Thread(new SimulateExplorationBySpeed());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
				}
				else{
					if (tSimExplore == null || !tSimExplore.isAlive()) {
						tSimExplore = new Thread(new SimulateExplorationBySpeed());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
					
				}
			}
		}
		// percentage != 0
		else{
			if (speed == 0){
				if (time == 0){
					if (tSimExplore == null || !tSimExplore.isAlive() || exploredMap.countExplored() < percentage) {
						tSimExplore = new Thread(new SimulateExplorationPercentage());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
				}
				else{
					if (tSimExplore == null || !tSimExplore.isAlive() || exploredMap.countExplored() < percentage) {
						tSimExplore = new Thread(new SimulateExplorationPercentage());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
				}
			}
			else{
				if (time == 0){
					if (tSimExplore == null || !tSimExplore.isAlive() || exploredMap.countExplored() < percentage) {
						tSimExplore = new Thread(new SimulateExplorationPercentage());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
				}
				else{
					if (tSimExplore == null || !tSimExplore.isAlive() || exploredMap.countExplored() < percentage) {
						tSimExplore = new Thread(new SimulateExplorationPercentage());
						tSimExplore.start();
					} else {
						tSimExplore.interrupt();
					}
					
				}
			}
		}
	}
	
	public static void resetSimulator(){

		
		boolean done = true;

		if (done) {
			// Reset all objects to a clean state
			robot = new Robot(isRealRun);
			exploredMap = new Map("unknown.txt");
			explorer = new Exploration(exploredMap);
			gui.refreshGUI(robot, exploredMap);
		}
	}

	


	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Reveal Fastest Path on GUI. Run Exploration first.
	 */
	public static void runShowFastestPath() {
//		fp = new FastestPath(exploredMap, new Coordinate(1, 1), new Coordinate(18, 13));

		ArrayList<Node> finalPath = new ArrayList<Node>();
		
		fp = new FastestPath(exploredMap, new Coordinate(1, 1), new Coordinate(18,13));

		finalPath.addAll(fp.runAStar());	// Append
		LinkedList<String> navigateStep = fp.navigateSteps(finalPath);
		
		
		String pointer = navigateStep.poll();
		String forArduino = "";
		while (pointer != null){
			int numOfForwards = 0;
			while (pointer == "F"){
				numOfForwards++;
				pointer = navigateStep.poll();
			}
			if (numOfForwards > 9){
				while (numOfForwards>0 && numOfForwards/9 > 0){
					forArduino += String.format("%d",9);
					numOfForwards = numOfForwards - 9;
				}
				forArduino += String.format("%d",numOfForwards);
			}
			else{
				forArduino += String.format("%d",numOfForwards);
			}
			if (pointer != null){
				forArduino += pointer;
			}
			pointer = navigateStep.poll();
		}
		System.out.println(forArduino);
		
		exploredMap.finalPathReveal(finalPath);
		gui.refreshGUI(robot, exploredMap);
	}

}
