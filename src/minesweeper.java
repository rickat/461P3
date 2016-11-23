import java.util.List;

/**
 * 
 */

/**
 * @author ylh96
 *
 */
public class minesweeper {

	// public GUI panel; <++++++++++++++++++++++++++++++++++++++++++++++++++++++++HERE!
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MineGridClass game = new MineGridClass(100, 4, 50);
		createPanel(100);
		int[][] player_color= assignPlayerColor(4);
		/*
		 * Following space will be handling user's request and show them on the panel
		 */
	}
	
	public static int[][] assignPlayerColor(int player) {
		// assign each player a color, each has three values represents the red, green and blue
		// so that it will be easier for GUI to generate color.
		// default color:
		//				1: 255,0,0
		//				2: 0,255,0
		//				3: 0,0,255
		//				4: 255,0,255
		//				5: 0,255,255
		//				6: 255,255,0
		// Board exploeded into black grid (255,255,255)
		// Board initialized to all white grid (0,0,0)
		int[][] player_color = new int[player][3];
		return player_color;
	}
	
	public static void createPanel(int size) {
		// create a graphical grid
	}

}
