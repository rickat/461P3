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
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter host name");
		String hostname = scan.nextLine();
		InetAddress IPAddress = InetAddress.getByName(hostname);
		int port_num = scan.nextInt();
		// connect the server 
		Socket socket = new Socket(IPAddress, port_num);
		int error_count = 0;
		int max_error = 100;
		while(true) {
			if(socket.isConnected()) {
				break;
			} else {
				error_count++;
				if(error_count == max_error) {
					socket.close();
					System.out.println("Failed");
				}
			}
		}
		// read server packet
		InputStream in = socket.getInputStream();
		DataInputStream dis = new DataInputStream(in);
		byte[] data = new byte[48];
		dis.readFully(data);
		// if failed quit
		if (!handleFirstPacket(decodePacket(ByteBuffer.wrap(data)))) return;
		
	}
	
	// handles the server's initial packet
	public static boolean handleFirstPacket(int[] ia) {
		if (ia[0] == 0) {  // failed to join
			System.out.println("Fail to join the game!");
			// disconnect
			return false;
		} else {  // success
			mycolor = new int[3];
			// get color
			for (int i = 0; i < 3; i++) {
				mycolor[i] = ia[i + 2];
			}
			// get id
			player_id = ia[5];
			// draw the GUI
			createPanel(ia[1]);
			return true;
		}
	}
	
	// handles server's response
	public static void handleServerPacket(int[] ia) {
		if (ia[0] == -1) {  // error case, do nothing
			return;
		} else if (ia[0] == 0) {  // explode case
			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
			if (madeMove) {  // it was this user who exploded, disconnect
				System.out.println("YOU EXPLODED");
				// disconnect
				return;
			}
		} else {
			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
			madeMove = false;  // move completed
		}
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
	
	// utility function to set color to [row, col]
	public static void setColor(int row, int col, int[] color) {
		
	}
	
	// utility function to create an actual GUI on client screen
	public static void createPanel(int size) {
		// create a graphical grid
	}

}
