package entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import communications.TCPComm;
import entities.Cell;
import entities.Robot.Rotate;
import python_output_tester.Tester;
import main.Main;

public class Map {
	public static final int maxY = 20;
	public static final int maxX = 15;

	private Cell cells[][];
	private Coordinate startCoord;
	private Coordinate goalCoord;
	private Coordinate waypoint;

	/**
	 * Map constructor.
	 * 
	 * Default Grid: y=20 by x=15.
	 */
	public Map() {
		// Initialise cells
		cells = new Cell[maxY][maxX];

		startCoord = new Coordinate(1, 1);	// Always y:1, x:1
		goalCoord = new Coordinate(18, 13);	// Always y:18, x:13

		// Initialise each cell
		for (int y = maxY - 1; y >= 0; y--) {
			for (int x = 0; x < maxX; x++) {
				cells[y][x] = new Cell(y, x);
			}
		}
	}

	/**
	 * Map constructor with import.
	 * 
	 * Default Grid: y=20 by x=15.
	 * 
	 * @param fileName
	 */
	public Map(String fileName) {
		this();
		this.importMap(fileName);
	}

	/**
	 * Import Map from txt file.
	 * 
	 * @param fileName (E.g. "empty.txt")
	 */
	public void importMap(String fileName) {
		try {

			String filePath = new File("").getAbsolutePath();
			Scanner s = new Scanner(new BufferedReader(new FileReader(filePath.concat("/virtual_arena/" + fileName))));

			while (s.hasNext()) {
				for (int y = maxY - 1; y >= 0; y--) {
					for (int x = 0; x < maxX; x++) {
						char type = s.next().charAt(0);
						cells[y][x].setCellType(type);

						/* Take note of waypoint */
						if (type == Cell.WAYPOINT)
							this.waypoint = new Coordinate(y, x);
					}
				}
			}

			s.close();
		} catch (IOException e) {
			System.err.format("Import Map IOException: %s%n", e);
		}
	}

	/**
	 * Get specific cell from Map.
	 * 
	 * @param coordinate
	 * @return Requested <tt>Cell</tt>.
	 */
	public Cell getCell(Coordinate coordinate) {
		return cells[coordinate.getY()][coordinate.getX()];
	}
	
	public Cell getWaypoint(Coordinate coordinate){
		return cells[coordinate.getX()][coordinate.getY()];
	}

	/**
	 * Start coordinate is always set to (1, 1).
	 * 
	 * @return <tt>Coordinate</tt> of the start position.
	 */
	public Coordinate getStartCoord() {
		return this.startCoord;
	}

	/**
	 * Goal coordinate is always set to (18, 13).
	 * 
	 * @return <tt>Coordinate</tt> of the goal position.
	 */
	public Coordinate getGoalCoord() {
		return this.goalCoord;
	}

	/**
	 * Set <tt>Coordinate</tt> of the waypoint.
	 * 
	 */
	public void setWaypoint(Coordinate waypoint) {
		this.cells[waypoint.getY()][waypoint.getX()].setCellType(Cell.WAYPOINT);
		this.waypoint = waypoint;
	}

	/**
	 * Waypoint must be set first.
	 * 
	 * @return <tt>Coordinate</tt> of the waypoint.
	 */
	public Coordinate getWaypoint() {
		return this.waypoint;
	}


	/**
	 * Get P1 descriptors.
	 */
	//used in TCPComm.java
	/**public static String genMDFBluetooth(Map map, Robot robot) {
		String toReturn = new String();

		toReturn = "MDF" + "|" + map.getP1Descriptors() + "|" + map.getP2Descriptors() + "|" + robot.getCurrDir() + "|"
				+ (19 - robot.getCurrPos().getY()) + "|" + robot.getCurrPos().getX() + "|" + "0";

		return toReturn;
	}**/ //for easier reference
	
	public String getP1Descriptors() {
		String P1 = new String();

		P1 += "11";		// Padding sequence

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = this.getCell(new Coordinate(y, x)).getCellType();

				if (cellType == Cell.UNKNOWN)
					P1 += "0";
				else
					P1 += "1";
			}
		}

		P1 += "11";		// Padding sequence

		// Convert to Hexadecimal
		String hexString = new String();
		for (int i = 0; i < 304; i += 4) {
			String binOf4Bits = P1.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(binOf4Bits, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}

	/**
	 * Get P2 descriptors.
	 */
	//used in TCPComm.java//for easier reference refer above
	public String getP2Descriptors() {
		String P2 = new String();

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = this.getCell(new Coordinate(y, x)).getCellType();

				if (cellType != Cell.UNKNOWN) {
					if (cellType == Cell.WALL)
						P2 += "1";
					else
						P2 += "0";
				}
			}
		}

		// Normalise P2 Binary
		int remainder = P2.length() % 4;
		String lastBit = new String();
		String padding = new String();

		switch (remainder) {
		case 1:
			lastBit = P2.substring(P2.length() - 1);
			padding = "000";
			P2 = P2.substring(0, P2.length() - 1).concat(padding).concat(lastBit);
			break;
		case 2:
			lastBit = P2.substring(P2.length() - 2);
			padding = "00";
			P2 = P2.substring(0, P2.length() - 2).concat(padding).concat(lastBit);
			break;
		case 3:
			lastBit = P2.substring(P2.length() - 3);
			padding = "0";
			P2 = P2.substring(0, P2.length() - 3).concat(padding).concat(lastBit);
			break;
		default: // Do nothing
		}

		// Convert to Hexadecimal
		String hexString = new String();
		for (int i = 0; i < P2.length(); i += 4) {
			String binOf4Bits = P2.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(binOf4Bits, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}

	/**
	 * Simulated reveal of cells around the <tt>Robot</tt> based on the provided <tt>testMap</tt>.
	 * 
	 * @param robot
	 * @param testMap Set to <tt>null</tt> for real run
	 */
	public void simulatedReveal(Robot robot, Map testMap) {
		Sensor[] sensors = robot.getAllSensors();
		Coordinate[] coordinates;

		/* Simulation Mode */
		for (int i = 0; i < sensors.length; i++) {
			coordinates = sensors[i].getFacingCoordinates(robot);

			// Only when sensor sees some coordinates
			if (coordinates != null) {
				for (int j = 0; j < coordinates.length; j++) {
					Cell unknownCell = this.getCell(coordinates[j]);
					Cell simulatedCell = testMap.getCell(coordinates[j]);
					unknownCell.setCellType(simulatedCell.getCellType());

					// Sensor should not be able to see past walls
					if (simulatedCell.getCellType() == Cell.WALL)
						break;
				}
			}
		}
	}

	/**
	 * Actual reveal of cells around the <tt>Robot</tt> based on the provided sensor values from Arduino.
	 * 
	 * @param robot
	 * @param incomingReadings
	 */
	
	public void actualReveal(Robot robot, String incomingReadings){
		Sensor[] sensors = robot.getAllSensors();
		int[] arduinoSensorValues = Sensor.cleanAllReadings(sensors, incomingReadings);
		//102214
		Coordinate[] coordinates;
		if (arduinoSensorValues.length != sensors.length){
			System.err.println("Incorrect sensor format received from Arduino!");
			return;
		}
		for (int i = 0; i < sensors.length; i++){
			coordinates = sensors[i].getFacingCoordinates(robot);
			
			if (coordinates != null){
				if (arduinoSensorValues[i] < coordinates.length){
					for (int j = 0; j < coordinates.length; j++){
						if (j == arduinoSensorValues[i]){
							if (this.getCell(coordinates[j]).getCellType() == Cell.UNKNOWN)
								this.getCell(coordinates[j]).setCellType(Cell.WALL);
							else if ((i == 0 || i == 2) && this.getCell(coordinates[j]).getCellType() == Cell.PATH)
								this.getCell(coordinates[j]).setCellType(Cell.PATH);
//							else if ((i == 5) && this.getCell(coordinates[j]).getCellType() == Cell.PATH)
//								this.getCell(coordinates[j]).setCellType(Cell.WALL);
							else if (this.getCell(coordinates[j]).getCellType() == Cell.WALL)
								this.getCell(coordinates[j]).setCellType(Cell.WALL);
							
							if (i==0 || i == 2){
								this.getCell(coordinates[j]).setPermanentCellType(true);
							}
							break;
						}
						else{
							if ((i==0 || i ==2) && this.getCell(coordinates[j]).getCellType() == Cell.WALL)
								this.getCell(coordinates[j]).setCellType(Cell.WALL);
							else
								this.getCell(coordinates[j]).setCellType(Cell.PATH);
							
							if (i ==0 || i == 2){
								this.getCell(coordinates[j]).setPermanentCellType(true);
							}
						}
					}
				}
				else if (arduinoSensorValues[i] == coordinates.length){
					for (int j = 0; j < coordinates.length; j++){
						this.getCell(coordinates[j]).setCellType(Cell.PATH);
						
						if (i == 0 || i == 2){
							this.getCell(coordinates[j]).setPermanentCellType(true);
						}
					}
				}
			}
		}
	}
	//camera at front right of the robot facing left
	public void actualImageDetectionReveal(Robot robot, String incomingReadings)  {
		int imageX;
		int imageY;
		Sensor[] sensors = robot.getAllSensors();
		boolean isDetected = false;
		int[] arduinoSensorValues = Sensor.cleanAllReadings(sensors, incomingReadings.substring(5));
		Coordinate[] coordinates;
		if (arduinoSensorValues.length != sensors.length){
			System.err.println("Incorrect sensor format received from Arduino!");
			return;
		}
		
		for (int i = 0; i < sensors.length; i++){
			//coordinates: facing coordinate of sensor #i
			coordinates = sensors[i].getFacingCoordinates(robot);
			
			if (coordinates != null){
				if (arduinoSensorValues[i] < coordinates.length){
					for (int j = 0; j < coordinates.length; j++){
						if (j == arduinoSensorValues[i]){
							this.getCell(coordinates[j]).setCellType(Cell.WALL);
							if (rotateForImage(robot, i) == true){
								this.getCell(coordinates[j]).setCellType(Cell.IMAGE);
								imageX = this.getCell(coordinates[j]).getX();
								imageY = this.getCell(coordinates[j]).getY();
							}
							
							if (i==0 || i == 2){
								this.getCell(coordinates[j]).setPermanentCellType(true);
							}
							break;
							
						}
						else{
							this.getCell(coordinates[j]).setCellType(Cell.PATH);
							
							if (i == 0 || i == 2){
								this.getCell(coordinates[j]).setPermanentCellType(true);
								
							}
						}
					}
				}
			}
		}
	}
	//string of sensors
		//Short_FrontLeft_North : 0
//		//Short_FrontCenter_North : 1
//		//Short_FrontRight_North : 2
//		//Short_FrontRight_East : 3
//		//Short_MiddleRight_East : 4
//		//Long_FrontLeft_West : 5
	//arduinoSensorValues: string of values seen by sensors
	public boolean rotateForImage(Robot robot, int sensorID)  {
		Sensor[] sensors = robot.getAllSensors();
		switch(sensorID){
			case 2:
				robot.rotate(Rotate.RIGHT);
				robot.rotate(Rotate.LEFT);
			case 3:
				if (sensors[2].getFacingCoordinates(robot).length ==2){
					robot.moveForward(2);
					robot.rotate(Rotate.LEFT);
					robot.rotate(Rotate.LEFT);
					robot.moveForward(2);
					robot.rotate(Rotate.LEFT);
					robot.rotate(Rotate.LEFT);
				}
			case 4:
				if (sensors[2].getFacingCoordinates(robot).length >= 1) {
					robot.moveForward(1);
					robot.rotate(Rotate.LEFT);
					robot.rotate(Rotate.LEFT);
					robot.moveForward(1);
					robot.rotate(Rotate.LEFT);
					robot.rotate(Rotate.LEFT);
				}
				
			default://do nothing
				;
		}
		return false;
	}

	public void finalPathReveal(ArrayList<Node> finalPath) {
		/* Clear any remnants of previously computed final path */
		for (int y = 0; y < maxY; y++) {
			for (int x = 0; x < maxX; x++) {
				if (cells[y][x].getCellType() == Cell.FINAL_PATH)
					cells[y][x].setCellType(Cell.PATH);
			}
		}

		/* Mark cellType as FINAL_PATH */
		for (int i = 0; i < finalPath.size(); i++) {
			Node n = finalPath.get(i);
			Cell cell = this.cells[n.getCell().getY()][n.getCell().getX()];
			cell.setCellType(Cell.FINAL_PATH);
		}
	}
	
	public int countExplored() {
		int count = 0;
		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = this.getCell(new Coordinate(y, x)).getCellType();
				if (cellType != Cell.UNKNOWN)
					count++;
			}
		}
		return count/3;
	}
	
	public void selfCalibration(Robot robot){
		Sensor[] sensors = robot.getAllSensors();
		boolean rightPlace = false;
		Main.comms.send(TCPComm.ARDUINO, "SC");
		System.out.println(sensors);
		while(!rightPlace){
			String incomingString = Main.comms.readFrom(TCPComm.ARDUINO);
			int[] arduinoSensorValues = Sensor.cleanAllReadings(sensors, incomingString);
			if (arduinoSensorValues[3] + arduinoSensorValues[4] ==0){
				if (arduinoSensorValues[0] > 1 && arduinoSensorValues[1] > 1 && arduinoSensorValues[2] > 1)
					rightPlace = true;
			}
			else{
				robot.rotate(Rotate.RIGHT);
				Main.gui.refreshGUI(Main.robot,Main.exploredMap);
				if (Main.comms.readFrom(TCPComm.ARDUINO)!=null)
					Main.comms.send(TCPComm.ARDUINO, "S");
			}
		}
		Main.gui.refreshGUI(Main.robot,Main.exploredMap);
	}
}
