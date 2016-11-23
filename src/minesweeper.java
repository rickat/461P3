import java.util.List;

/**
 * 
 */

/**
 * @author ylh96
 *
 */

// each player have their own board with buttons on them but no functions
public class minesweeper {

	// public GUI panel; <++++++++++++++++++++++++++++++++++++++++++++++++++++++++HERE!
	/**
	 * @param args
	 */
	public static int[] conquered_area;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MineGridClass game = new MineGridClass(100, 4, 50);
		// for recording each user's land
		conquered_area = new int[4];
		/*
		for (user : user set) {
			createPanel(100);
		}
		*/
		int[][] player_color= assignPlayerColor(4);
		// listen for players to join
		// send out initial message: board size for user to create board on their end
		/*
		 * Following space will be handling user's request and show them on the panel
		 * Get player move by calling game.makeMove()
		 * paint the board accroding to the value returned
		 * 
		 * Use while loop to continuously get player respond, 4 request from 4 players one round in the while
		 * loop, condition set to isEnd() THINKING ABOUT LOCKING WHEN ONE PLAYER IS USING
		 * 
		 * if end:
		 * case 1: only one player survived, report the winner
		 * case 2: more than one survived, player with more land wins, report the winner
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
