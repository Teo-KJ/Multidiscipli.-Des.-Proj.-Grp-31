package main;

import algorithms.Exploration;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class SimulateExplorationPercentage implements  Runnable{
	private GUI gui;
	private Robot robot;
	private Map exploredMap, testMap;
	private Exploration explorer;
	private int percentage;
	private int speed;
	private int time;
	
	public SimulateExplorationPercentage(){
		this.gui = Main.gui;
		this.testMap = Main.testMap;
		
		// Reset all objects to a clean state
		robot = Main.robot;
		exploredMap = Main.exploredMap;
		explorer = Main.explorer;
		percentage = Main.percentage;
		speed = Main.speed;
		time = Main.time;
	}
	
	@Override
	public void run(){
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		long start = System.currentTimeMillis();
		if (speed == 0){
			if (time == 0){
				while (!Thread.currentThread().isInterrupted() && exploredMap.countExplored() < percentage) {
					try {
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						// Run exploration for one step
						boolean done = explorer.executeOneStep(robot, exploredMap);
						
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						if (done)
							break;
						
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			else{
				while (!Thread.currentThread().isInterrupted() && exploredMap.countExplored() < percentage && System.currentTimeMillis() < start + time) {
					try {
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						// Run exploration for one step
						boolean done = explorer.executeOneStep(robot, exploredMap);
						
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						if (done)
							break;
						
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
		else{
			if (time == 0){
				while (!Thread.currentThread().isInterrupted() && exploredMap.countExplored() < percentage) {
					try {
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						// Run exploration for one step
						boolean done = explorer.executeOneStep(robot, exploredMap);
						
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						if (done)
							break;
						
						Thread.sleep(1000/speed);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			else{
				while (!Thread.currentThread().isInterrupted() && exploredMap.countExplored() < percentage && System.currentTimeMillis() < start + time) {
					try {
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						// Run exploration for one step
						boolean done = explorer.executeOneStep(robot, exploredMap);
						
						exploredMap.simulatedReveal(robot, testMap);
						gui.refreshGUI(robot, exploredMap);
						
						if (done)
							break;
						
						Thread.sleep(1000/speed);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
		System.out.println(":: Has explored: " + exploredMap.countExplored() + "% of the map ::");
		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}





