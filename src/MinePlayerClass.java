import java.nio.ByteBuffer;

/**
 * 
 */

/**
 * @author Yilun Hua (1428927), Shen Wang (1571169), Antony Chen ()
 * Player side program
 * MAYBE NEED SOCKETCHANNEL INSTEAD OF SOCKET
 */
public class MinePlayerClass {

	public static int player_id;
	public static int[] mycolor;

	
	// handles the server's initial packet
	// ia[0] is ack
	// ia[1] is size
	// ia[2],ia[3],ia[4] is color
	// ia[5] is color
	// return whether user servive
	public int handleFirstPacket(int[] ia) {
		if (ia[0] == 0) {  // failed to join
			System.out.println("Fail to join the game!");
			// disconnect
			//return false;
			return -1;
		} else {  // success
			mycolor = new int[3];
			// get color
			for (int i = 0; i < 3; i++) {
				mycolor[i] = ia[i + 2];
			}
			// get id
			player_id = ia[5];
			// draw the GUI
			//createPanel(ia[1]);
			//return true;

		}
		return ia[1];
	}

	

	// get the request and prepare the packet
	public byte[] action(int row, int col) {
		ByteBuffer bb = ByteBuffer.allocate(12);
		bb.putInt(0, player_id).putInt(4, row).putInt(8, col);
		return bb.array();
	}

	// decode the server packet
	public int[] decodePacket(ByteBuffer bb) {
		int[] res = new int[6];
		for (int i = 0; i < 6; i++) {
			res[i] = bb.getInt(i * 4);
		}
		return res;
	}

	

}

