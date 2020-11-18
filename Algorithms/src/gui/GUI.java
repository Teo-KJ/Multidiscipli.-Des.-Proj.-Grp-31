package gui;

import javax.swing.*;

import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import main.Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame {

	private static final long serialVersionUID = -3014581757826180255L;

	private Container mainContainer;
	private JPanel mapPanel, ctrlPanel;

	private JLabel[][] cellsUI;
	private Robot robot;
	private Map map;

	/**
	 * Constructor for <tt>GUI</tt> as a JFrame.
	 * 
	 * @param robot
	 * @param map
	 */
	public GUI(Robot robot, Map map) {
		super("MDP Group 31 - 2020 Sem 1");

		// Initialise class variables
		this.robot = robot;
		this.map = map;

		// Initialise overall layout
		initLayout();

		this.setVisible(true);
	}

	/**
	 * Ensures <tt>GUI</tt> is showing the latest instance of <tt>Robot</tt> and <tt>Map</tt>.
	 * 
	 * @param robot
	 * @param map
	 */
	public void refreshGUI(Robot robot, Map map) {
		this.robot = robot;
		this.map = map;

		mapPanel.repaint();	// Repaint mapPanel to show updated Robot position

		/* Every refresh on GUI should also refresh on Android tablet screen */
		//if (Main.isRealRun) { //if it is really running
			//Main.comms.send(TCPComm.BLUETOOTH, TCPComm.genMDFBluetooth(map, robot));
		//}
	}

	/**
	 * (Optional) Set mode of GUI visually.
	 * 
	 * @param connected
	 */
	public void setModeColour(boolean connected) {
		if (connected)
			mainContainer.setBackground(new Color(115, 221, 141));
		else
			mainContainer.setBackground(new Color(198, 105, 105));
	}

	/**
	 * Initialise the Container, JPanels and cellsUI.
	 */
	private void initLayout() {
		mapPanel = new MapPanel();
		mapPanel.setOpaque(false);
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		populateMapPanel();		// Populate Map Panel with cells from _map

		ctrlPanel = new JPanel();
		ctrlPanel.setOpaque(false);
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ctrlPanel.setLayout(new GridLayout(10, 1, 0, 10));
		populateCtrlPanel();	// Populate Control Panel with buttons

		mainContainer = this.getContentPane();
		mainContainer.setLayout(new BorderLayout(0, 0));
		mainContainer.add(mapPanel, BorderLayout.WEST);
		mainContainer.add(ctrlPanel);
		this.setSize(900, 800); // Width, Height
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit mainUI on close
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Close socket before exiting (In Real Run Mode)
				if (Main.isRealRun && Main.comms != null)
					Main.comms.close();
			}
		});

		// Launch mainUI to the right of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 1 - getSize().width / 1, dim.height / 2 - getSize().height / 2 - 20);
	}

	/**
	 * Custom class for <tt>MapPanel</tt> as a JPanel.
	 * 
	 * Responsible for producing a responsive grid map.
	 *
	 */
	private class MapPanel extends JPanel {

		private static final long serialVersionUID = 3896801036058623157L;

		public MapPanel() {
			// Additional +1 row & +1 col for axis labelling
			super(new GridLayout(Map.maxY + 1, Map.maxX + 1, 2, 2));
		}

		/**
		 * Override the getPreferredSize() of JPanel to always ensure that MapAndRobotJPanel has a dynamic preferred
		 * size so that it maintains the original aspect ratio. In this case, it will return a JPanel that is always
		 * sized with an aspect ratio of 3:4 (15 columns by 20 rows).
		 * 
		 * Referenced from: https://stackoverflow.com/questions/21142686/making-a-robust-resizable-swing-chess-gui
		 */
		@Override
		public final Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			Component c = getParent();

			if (c == null)
				return d;

			else {
				// Final Dimension should be 16:21 (W:H) aspect ratio for 16 cells by 21 cells
				// (including axis labelling)
				// Use of double for more accuracy -> then round to nearest integer
				double numOfCellsWidth = Map.maxX + 1;	// 16 = 15 + 1
				double numOfCellsHeight = Map.maxY + 1;	// 21 = 20 + 1

				int prefHeight = (int) c.getHeight();
				int prefWidth = (int) Math.round(prefHeight / numOfCellsHeight * numOfCellsWidth);

				return (new Dimension(prefWidth, prefHeight));
			}
		}
	}

	/**
	 * Populate cells and robot location in the <tt>mapPanel</tt>.
	 * 
	 * Responsible for colouring cells by <tt>cellType</tt> and painting of cells occupied by robot.
	 */
	private void populateMapPanel() {
		cellsUI = new JLabel[Map.maxY][Map.maxX];

		// Populate Map Panel
		for (int y = Map.maxY; y >= 0; y--) {		// Additional loop for axis labelling
			for (int x = 0; x <= Map.maxX; x++) {
				int actualY = y - 1;
				int actualX = x - 1;

				JLabel newCell = new JLabel("", JLabel.CENTER) {
					private static final long serialVersionUID = -4788108468649278480L;

					/**
					 * Paint robot, visited, etc. on the cells.
					 */
					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);

						/* Don't paint on axis labelling */
						if (actualY >= 0 && actualY < Map.maxY) {
							if (actualX >= 0 && actualX < Map.maxX) {
								Cell currCell = map.getCell(new Coordinate(actualY, actualX));

								paintVisited(this.getWidth(), currCell, g); //TAKE NOTE
								paintRobot(this.getWidth(), actualY, actualX, robot.getFootprint(), g);

								setCellBG(currCell, this);
								labelPermanentFlag(currCell, this);
							}
						}
					}
				};
				newCell.setPreferredSize(new Dimension(40, 40)); // Ensure cell is a square
				newCell.setOpaque(true);

				// If it's the first row / col, perform axis labelling
				if (y == 0 && x == 0) {
					newCell.setText("y/x");
					mapPanel.add(newCell);
				} else if (x == 0) {
					newCell.setText(Integer.toString(actualY));
					mapPanel.add(newCell);
				} else if (y == 0) {
					newCell.setText(Integer.toString(actualX));
					mapPanel.add(newCell);
				}

				// Actual Map Population
				else {
					newCell.setOpaque(true);
					newCell.setBackground(cellColour(map.getCell(new Coordinate(y - 1, x - 1))));

					cellsUI[actualY][actualX] = newCell;
					mapPanel.add(cellsUI[actualY][actualX]);
				}
			}
		}
	}

	/**
	 * Populate Labels and Buttons in the <tt>ctrlPanel</tt>.
	 */
	private void populateCtrlPanel() {
		/* Display Mode */
		String mode = Main.isRealRun ? "Real Run" : "Simulation";
		ctrlPanel.add(new JLabel("MODE: " + mode, JLabel.CENTER));

		/* Explore (per step) button */
		JButton explorePerStep = new JButton("Explore step by step");
		
		Font explorePerStepFont=new Font(explorePerStep.getFont().getName(),explorePerStep.getFont().getStyle(),20);  
		explorePerStep.setFont(explorePerStepFont);
		explorePerStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runSimExplorePerStep();
			}
		});

		
		/* Perform the checklist */
		JButton exploreAll = new JButton("Perform the Checklists");
		
		Font exploreAllFont=new Font(exploreAll.getFont().getName(),exploreAll.getFont().getStyle(),20);  
		exploreAll.setFont(exploreAllFont);
		
		exploreAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runSimulator();//TAKE NOTE
			}
		});
		
		/* Reset the Simulator button */
		JButton reset = new JButton("Reset the Map");
		
		Font resetFont=new Font(exploreAll.getFont().getName(),exploreAll.getFont().getStyle(),20);
		reset.setFont(resetFont);
		
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.resetSimulator();//TAKE NOTE
			}
		});
		
		/*Explore by Percentage Button */
//		JButton explorePercentage = new JButton("Explore by Percentage");
//		Font explorePercentageFont=new Font(exploreAll.getFont().getName(),exploreAll.getFont().getStyle(),20);
//		explorePercentage.setFont(explorePercentageFont);;
//
//		explorePercentage.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Main.runSimExplorePercentage(); //TAKE NOTE
//			}
//		});
		
		
//		/*Explore by Speed Button */
//		JButton exploreBySpeed = new JButton("Explore by Speed");
//		Font exploreBySpeedFont=new Font(exploreAll.getFont().getName(),exploreAll.getFont().getStyle(),20);
//		exploreBySpeed.setFont(exploreBySpeedFont);
//		exploreBySpeed.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Main.runSimulator(); //TAKE NOTE
//			}
//		});

		/* Show the Fastest Path */
		JButton fastestPath = new JButton("Show A*Star Fastest Path");
		Font fastestPathFont=new Font(fastestPath.getFont().getName(),fastestPath.getFont().getStyle(),20);  
		fastestPath.setFont(fastestPathFont);
		
		fastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runShowFastestPath();
			}
		});

		/* Standby for Fastest Path */
		JButton standbyFastestPath = new JButton("Standby for Fastest Path");
		standbyFastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		/* Start Fastest Path */
		JButton startRealFastestPath = new JButton("Start Fastest Path");
		startRealFastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		/* P1 and P2 Descriptors */
		JButton printDescriptors = new JButton("Print P1 and P2");
		Font printDescriptorsFont=new Font(printDescriptors.getFont().getName(),printDescriptors.getFont().getStyle(),20);  
		printDescriptors.setFont(printDescriptorsFont);
		
		printDescriptors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("P1: " + map.getP1Descriptors());
				System.out.println("P2: " + map.getP2Descriptors());
			}
		});

		/* Real Run: Start Explore */
		JButton startRealExplore = new JButton("START EXPLORATION");
		startRealExplore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				Main.runRealStartExploration();
			}
		});

		/* Exploration Section */
		JLabel exploration_label= new JLabel("=== Exploration ===", JLabel.CENTER);
		exploration_label.setFont(new Font("Serif", Font.PLAIN, 20));
		ctrlPanel.add(exploration_label);		
		//ctrlPanel.add(new JLabel("=== Exploration ===", JLabel.CENTER));
		
		
		
		if (Main.isRealRun) {
//			ctrlPanel.add(startRealExplore);
		} else {
			ctrlPanel.add(explorePerStep);
			ctrlPanel.add(exploreAll);
			ctrlPanel.add(reset);
//			ctrlPanel.add(explorePercentage);
//			ctrlPanel.add(exploreBySpeed);
			/* Fastest Path Section */
			JLabel fastest_path_label= new JLabel("=== Fastest Path ===", JLabel.CENTER);
			fastest_path_label.setFont(new Font("Serif", Font.PLAIN, 20));
			ctrlPanel.add(fastest_path_label);
			//ctrlPanel.add(new JLabel("=== Fastest Path ===", JLabel.CENTER));
			ctrlPanel.add(fastestPath);
		}

		/* About Map */
		JLabel about_map_label= new JLabel("=== About Map ===", JLabel.CENTER);
		about_map_label.setFont(new Font("Serif", Font.PLAIN, 20));
		ctrlPanel.add(about_map_label);
		//ctrlPanel.add(new JLabel("=== About Map ===", JLabel.CENTER));
		ctrlPanel.add(printDescriptors);
	}

	/**
	 * Paint <tt>Robot</tt> with provided Graphics <tt>g</tt> if <tt>actualY</tt> and <tt>actualX</tt> matches
	 * <tt>robotFootprint</tt>.
	 * 
	 * @param actualY
	 * @param actualX
	 * @param robotFootprint
	 * @param g
	 */
	
	//two circles meant that there is a sensor there
	private void paintRobot(int width, int actualY, int actualX, Coordinate[] robotFootprint, Graphics g) {
		width /= 2;		// For a responsive UI
//		g.setColor(Color.GREEN);
//		g.drawOval(actualX, actualY, width, width);
		// Paint FRONT_LEFT of Robot
		if (actualY == robotFootprint[Robot.FRONT_LEFT].getY() && actualX == robotFootprint[Robot.FRONT_LEFT].getX()) {
			g.setColor(Color.red);
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint FRONT_CENTER of Robot
		if (actualY == robotFootprint[Robot.FRONT_CENTER].getY()
				&& actualX == robotFootprint[Robot.FRONT_CENTER].getX()) {
			g.setColor(Color.DARK_GRAY);//Color.DARK_GRAY
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
			
		}

		// Paint FRONT_RIGHT of Robot
		if (actualY == robotFootprint[Robot.FRONT_RIGHT].getY()
				&& actualX == robotFootprint[Robot.FRONT_RIGHT].getX()) {
			g.setColor(Color.red);
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint MIDDLE_LEFT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_LEFT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint MIDDLE_CENTER of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_CENTER].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);//width / 2, width / 2, width, width
			System.out.print("X-coordinate: "+actualX+" ");
			System.out.print("Y-coordinate: "+actualY +"\n");
//			g.fillOval(actualX, actualY, width*2, width*2);
		}

		// Paint MIDDLE_RIGHT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_RIGHT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint BACK_LEFT of Robot
		if (actualY == robotFootprint[Robot.BACK_LEFT].getY() && actualX == robotFootprint[Robot.BACK_LEFT].getX()) {
			g.setColor(Color.red);//new Color(127, 204, 196)
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint BACK_CENTER of Robot
		if (actualY == robotFootprint[Robot.BACK_CENTER].getY()
				&& actualX == robotFootprint[Robot.BACK_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint BACK_RIGHT of Robot
		if (actualY == robotFootprint[Robot.BACK_RIGHT].getY() && actualX == robotFootprint[Robot.BACK_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));//127, 204, 196
			g.fillOval(width / 2, width / 2, width, width);
		}
	}

	/**
	 * Paint with provided Graphics <tt>g</tt> to represent visited cells.
	 * 
	 * @param width
	 * @param cell
	 * @param g
	 */
	private void paintVisited(int width, Cell cell, Graphics g) {
		width /= 2;		// For a responsive UI

		if (cell.isVisited() == true) {
			g.setColor(new Color(144,238,191));
			g.fillRect(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));
		}
	}

	/**
	 * Set background colour of cell.
	 * 
	 * @param currCell
	 * @param jl
	 */
	private void setCellBG(Cell currCell, JLabel jl) {
		jl.setBackground(cellColour(currCell));
	}

	/**
	 * Cell colour rule based on <tt>cellType</tt>.
	 * 
	 * @param cell
	 * @return
	 */
	private Color cellColour(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.WAYPOINT:
			return new Color(127, 204, 196);
		case Cell.FINAL_PATH:	// Prioritise showing FINAL_PATH
			return Color.BLUE;
		case Cell.START:
			return Color.YELLOW;
		case Cell.GOAL:
			return Color.GREEN;
		case Cell.WALL:
			return Color.BLACK;
		case Cell.PATH:
			return Color.WHITE;
		default:				// Unknown cells
			return new Color(128,0,0);
		}
	}

	/**
	 * Label permanent flag as "P" on GUI.
	 * 
	 * @param currCell
	 * @param jl
	 */
	private void labelPermanentFlag(Cell currCell, JLabel jl) {
		/* Show isPermanent flag */
		if (currCell.isPermanentCellType()) {
			//jl.setForeground(new Color(250,128,114));
			jl.setText("");//CHANGED
		} else
			jl.setText("");
	}
}
