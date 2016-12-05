import java.util.Random;

/**
 * 
 */

/**
 * @author Yilun Hua (1428927), Shen Wang (1571169), Antony Chen ()
 *	-1 is mine, players are numbered 1 to n depending on how many players
 */
public class MineGridClass {
	private int[][] grid;
	private int players_size;
	private int remaining_grid_space;
	
	public MineGridClass(int grid_size, int player_size, int max_mine_size) {
		if (max_mine_size > grid_size * grid_size) {
			System.out.println("Cannot have too many mines! Use default maximum mine settings");
			max_mine_size = grid_size * grid_size;
		}
		this.grid = new int[grid_size][grid_size];
		this.players_size = player_size;
		this.remaining_grid_space -= max_mine_size;
		initializeGrid(grid_size, max_mine_size);
	}
	
	// initialize the grid with m mines
	public void initializeGrid(int n, int m) {
		Random rand = new Random();
		for (int i = 0; i < m; i++) {
			int next_col = rand.nextInt(n);
			int next_row = rand.nextInt(n);
			while (grid[next_row][next_col] == -1) {
				next_col = rand.nextInt(n);
				next_row = rand.nextInt(n);
			}
			grid[next_row][next_col] = -1;
		}
	}
	
	// return 1 for success occupy
	// return 0 for enemy occupied, unable to gain
	// return -1 for player death
	// return -2 for error
	public int makeMove(int player_num, int row, int col) {
		if (player_num > players_size) {
			return -2;
		}
		if (grid[row][col] == -1) {
			players_size--;
			return -1;
		}
		if (grid[row][col] > 0) {
			return 0;
		}
		remaining_grid_space--;
		grid[row][col] = player_num;
		return 1;
	}
	
	// winning condition: only one player survives or all space is occupied
	public boolean isEnd() {
		return players_size <= 1 || remaining_grid_space == 0;
	}
}
