package entities;

public class Cell extends Coordinate {
	public static final char UNKNOWN = 'A', WALL = 'B', START = 'C', GOAL = 'D', PATH = 'E', FINAL_PATH = 'F',
			WAYPOINT = 'G', IMAGE = 'I';
//	public static final int UP_ARROW = 1, DOWN_ARROW = 2, RIGHT_ARROW = 3, LEFT_ARROW = 4, STOP = 5, ONE = 6, TWO = 7,
//			THREE = 8, FOUR = 9, FIVE = 10, ALPHABET_A = 11, ALPHABET_B = 12, ALPHABET_C = 13, ALPHABET_D = 14,
//			ALPHABET_E = 15;

	private char cellType; 				// Cell type listed above
	private boolean permanentCellType;	// Permanent cell type, do not change after set
	private boolean isVisited;			// Whether cell has been visited

	/**
	 * <tt>Cell</tt> constructor which extends <tt>Coordinate</tt>.
	 * 
	 * @param y
	 * @param x
	 */
	public Cell(int y, int x) {
		super(y, x); 					// Coordinate constructor to set Y and X
		this.cellType = UNKNOWN;		// Default to UNKNOWN
		this.permanentCellType = false;	// Assume not permanent yet
		this.isVisited = false;			// Default to unvisited
	}

	/**
	 * Get the current cellType.
	 * 
	 * @return
	 */
	public char getCellType() {
		return cellType;
	}

	/**
	 * Set the cellType.
	 * 
	 * @param cellType
	 */
	public void setCellType(char cellType) {
		if (cellType == FINAL_PATH) {
			this.cellType = cellType;
		}

		/* START and GOAL cellTypes should be permanent */
		// For cases where sensor falsely detects a wall in START or GOAL area
		else if (cellType == START || cellType == GOAL) {
			this.permanentCellType = true;
			this.cellType = cellType;
		}

		/* Can only set cellType if permanent flag is not set */
		else if (this.permanentCellType == false) {
			this.cellType = cellType;
		}
	}

	/**
	 * Check if <tt>Cell</tt> has been visited.
	 * 
	 * @return <i>true</i> or <i>false</i>
	 */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * Set <tt>Cell</tt> to visited.
	 */
	public void setVisited() {
		this.isVisited = true;

		/* Cells that are visited should be set permanently as a path */
		// For cases where sensor falsely detects a wall in a isVisited cell
		if (!(this.cellType == START || this.cellType == GOAL))
			this.cellType = PATH;

		this.permanentCellType = true;
	}

	/**
	 * Set <tt>Cell</tt> isVisted to true or false.
	 * 
	 * @param bool
	 */
	public void setVisited(boolean bool) {
		this.isVisited = bool;
	}

	public boolean isPermanentCellType() {
		return permanentCellType;
	}

	public void setPermanentCellType(boolean permanentCellType) {
		this.permanentCellType = permanentCellType;
	}

}
