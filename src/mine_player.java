import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Yilun Hua (1428927), Shen Wang (1571169), Antony Chen ()
 * Player side program
 * MAYBE NEED SOCKETCHANNEL INSTEAD OF SOCKET
 */
public class mine_player {

	public static int player_id;
	public static boolean madeMove = false;  // can be replaced by checking if color is mycolor
	public static int[] mycolor;

	
	// handles the server's initial packet
	// ia[0] is ack
	// ia[1] is size
	// ia[2],ia[3],ia[4] is color
	// ia[5] is color
	// return whether user servive
	public static int handleFirstPacket(int[] ia) {
		if (ia[0] == 0) {  // failed to join
			System.out.println("Fail to join the game!");
			// disconnect
			//return false;

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
		return ia[0];
	}

	

	// get the request and prepare the packet
	public static byte[] action(int row, int col) {
		ByteBuffer bb = ByteBuffer.allocate(24);
		bb.putInt(player_id).putInt(row).putInt(col);
		madeMove = true;  // it made a move, set it to true
		return bb.array();
	}

	// decode the server packet
	public static int[] decodePacket(ByteBuffer bb) {
		int[] res = new int[6];
		for (int i = 0; i < 6; i++) {
			res[i] = bb.getInt(i * 8);
		}
		return null;
	}

	

}
