package python_output_tester;
import java.io.*;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import main.Main;
public class ReadOutput {
	public static String obj_id;
	public static String last = "null";
	public static boolean isDetected;
	public static Coordinate imageCoordinate;
	public static void readString(String fileName) {
//		String last = "null";
		try{
//			System.out.println("hello");
			BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\lowbe\\Desktop\\object_detection.txt"));
			while ((obj_id = bufferedReader.readLine())!=null){
				if (!last.equals("null") && last.equals(obj_id)){
					System.out.println("No new image detected! " );
					isDetected = false;


//					last = obj_id;
				}
				else{
					last = obj_id;
					isDetected = true;
					System.out.println("last is: " + last);
					System.out.println("Image id is: "+obj_id);

//					switch(Main.robot.getCurrDir()){
//						case 0://north
//							for (int i = Main.robot.getCurrPos().getX(); i < Map.maxX; i++){
//								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()+1, i)).getCellType()== Cell.WALL || Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()+1, i)).getCellType()== Cell.UNKNOWN){
//									imageCoordinate.setY(Main.robot.getCurrPos().getY()+1);
//									imageCoordinate.setX(i);
//									break;
//								}
//							}
//						case 1://east
//							for (int i = Main.robot.getCurrPos().getY(); i > 0; i--){
//								if (Main.exploredMap.getCell(new Coordinate(i,Main.robot.getCurrPos().getX()+1)).getCellType()== Cell.WALL|| Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()+1, i)).getCellType()== Cell.UNKNOWN){
//									imageCoordinate.setY(i);
//									imageCoordinate.setX(Main.robot.getCurrPos().getX()+1);
//									break;
//								}
//							}
//						case 2://south
//							for (int i = Main.robot.getCurrPos().getX(); i >0 ; i--){
//								if (Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()-1, i)).getCellType()== Cell.WALL|| Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()+1, i)).getCellType()== Cell.UNKNOWN){
//									imageCoordinate.setY(Main.robot.getCurrPos().getY()-1);
//									imageCoordinate.setX(i);
//									break;
//								}
//							}
//						case 3://west
//							for (int i = Main.robot.getCurrPos().getY(); i < Map.maxY; i++){
//								if (Main.exploredMap.getCell(new Coordinate(i, Main.robot.getCurrPos().getY()-1)).getCellType()== Cell.WALL|| Main.exploredMap.getCell(new Coordinate(Main.robot.getCurrPos().getY()+1, i)).getCellType()== Cell.UNKNOWN){
//									imageCoordinate.setY(i);
//									imageCoordinate.setX(Main.robot.getCurrPos().getX()-1);
//									break;
//								}
//							}
//
//					}
				}
				break;
			}
			
		}catch (IOException e){
			System.err.format("Read String IOException: %s%n", e);
		}
	}


}
