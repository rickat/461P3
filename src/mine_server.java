import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
/**
 * 
 */
import java.util.Set;

/**
 * @author Yilun Hua (1428927), Shen Wang (1571169), Antony Chen ()
 * Server side program
 */

// each player have their own board with buttons on them but no functions
public class mine_server {

	public static final int PLAYER = 4;
	public static final int GRIDSIZE = 100;
	public static final int MINENUM = 50;
	
	// public GUI panel; <++++++++++++++++++++++++++++++++++++++++++++++++++++++++HERE!
	/**
	 * @param args
	 */
	public static Selector sel;
	// maps a socket to player ID
	// public static HashMap<Socket, Integer> client_info;
	
	/*
	 * Initial packet from server to client: 48 bytes
	 * ACK is 1 for success
	 *     is 0 for unable to join
	 * -----------------------------------------
	 * |ACK|GRID SIZE|PLAYER COLOR[3]|PLAYER ID|
	 * -----------------------------------------
	 * 
	 * User request packet: 24 bytes
	 * player ID wants [row][col]
	 * -----------------------
	 * |Player ID|row #|col #|
	 * -----------------------
	 * 
	 * Server to client later packet: 48 bytes
	 * user update [row][col] to color 
	 * ACK is -1 for error, change nothing
	 *     is 0 for a player death, turn block into black
	 *     is 1 for success, turn block into specific color
	 *     is 2 for someone won, turn all block to that color
	 * --------------------------
	 * |ACK|row #|col #|color[3]|
	 * --------------------------
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// get port number
		Scanner scan = new Scanner(System.in);
		int port_num = scan.nextInt();
		try {
			sel = Selector.open();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		scan.close();
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
		InetSocketAddress portnum = new InetSocketAddress(port_num);
		ServerSocketChannel scs = ServerSocketChannel.open();
		try {
			scs.socket().bind(portnum);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + port_num);
			System.exit(-1);
		}
		// accepts PLAYER players into game
		int count = 0;
		ServerSocket ngs = new ServerSocket(0);
		ByteBuffer bb1 = ByteBuffer.allocate(8);
		bb1.putInt(ngs.getLocalPort());
		new Thread(new Client_handler(ngs.getChannel())).start();
		while(true){
			SocketChannel client = scs.accept();
			if (client != null) {
				count++;
				client.write(bb1);
				if (count == PLAYER) {
					ngs = new ServerSocket(0);
					count = 0;
					bb1 = ByteBuffer.allocate(8);
					bb1.putInt(ngs.getLocalPort());
					new Thread(new Client_handler(ngs.getChannel())).start();
				}
			}
		}
	}
	
	static class Client_handler implements Runnable {

		public ServerSocketChannel game_server;
		public int[] conquered_area;
		public int[][] player_color;
		public MineGridClass game;
		public int player_count = 0;
		// public Socket[] player_sockets;  // store player socket
		public HashMap<SocketChannel, Integer> socket_map;
		public Selector select;
		
		public Client_handler(ServerSocketChannel game_server) throws Exception {
			this.game_server = game_server;
			// MineGridClass game = new MineGridClass(100, 4, 50);
			game = new MineGridClass(GRIDSIZE, PLAYER, MINENUM);
			// for recording each user's land
			conquered_area = new int[PLAYER];
			// player_color = new int[PLAYER][3];
			assignPlayerColor();
			try {
				select = Selector.open();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			socket_map = new HashMap<>();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (player_count < PLAYER) {
				SocketChannel client = null;
				try {
					 client = game_server.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (client != null) {
					try {
						client.configureBlocking(false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						client.register(select, SelectionKey.OP_READ);
					} catch (ClosedChannelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// player_sockets[player_count] = client.socket();
					socket_map.put(client, player_count);
					byte[] ba = initialPacket();
					try {
						client.write(ByteBuffer.wrap(ba));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					player_count++;
				}
			}
			int isClosed = 0;
			ByteBuffer bb = ByteBuffer.allocate(24);
			while (isClosed < PLAYER) {
				int readyChannels = 0;
				try {
					readyChannels = select.select();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (readyChannels == 0) continue;
				Set<SelectionKey> selectedKeys = sel.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while(keyIterator.hasNext()) {
					bb.clear();
					SelectionKey key = keyIterator.next();
					SocketChannel sc = (SocketChannel)key.channel();
					try{
						if (socket_map.containsKey(sc)) {
							int readlen = sc.read(bb);
							if (readlen == -1) {
								isClosed++;
								break;
							}
							bb.flip();
							while (bb.hasRemaining()) {
								try {
									int[] res = decodePacket(bb);
									ByteBuffer bb1 = reportUser(res[0], res[1], res[2]);
									for (SocketChannel scc : socket_map.keySet()) {
										ByteBuffer bb2 = clone(bb1);  // clone and send out the bytebuffer to
																	 // everyone
										scc.write(bb2);
									}
								} catch(IOException e) {
									isClosed++;
									break;
								}
							}
						}
					}catch(IOException i){
						// abort quietly
					}

				}
				selectedKeys.clear();
			}
		}
		
		// decode the server packet
		public int[] decodePacket(ByteBuffer bb) {
			int[] res = new int[3];
			for (int i = 0; i < 3; i++) {
				res[i] = bb.getInt(i * 8);
			}
			return res;
		}
		
		public ByteBuffer clone(ByteBuffer original) {
		       ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		       original.rewind();//copy from the beginning
		       clone.put(original);
		       original.rewind();
		       clone.flip();
		       return clone;
		}
		
		// create initial packet
		// if player_count == PLAYER then ACK is 0
		// else ACK is 1 and return back the information needed
		public byte[] initialPacket() {
			ByteBuffer bb = ByteBuffer.allocate(48);
			if (player_count < PLAYER) {
				player_count++;
				// maps them MAY NEED IT, BUT NOT FOR NOW
				// client_info.put(s, player_count);
				// add information
				bb.putInt(1).putInt(GRIDSIZE);
				// put in the color
				for (int i = 0; i < 3; i++) {
					bb.putInt(player_color[player_count][i]);
				}
				bb.putInt(player_count);
			} else {  // cannot fit
				// ERROR message
				bb.putInt(0).putInt(-1).putInt(-1).putInt(-1).putInt(-1).putInt(-1);
			}
			return bb.array();
		}
		
		// returns a byte array for sending back result to user;
		public ByteBuffer reportUser(int player_num, int row, int col) {
			int res = game.makeMove(player_num, row, col);
			// success
			if (res == 1) {
				conquered_area[player_num]++;  // increment the area count
			} else if (res == -1) {  // player explodes
				conquered_area[player_num] = -1;  // indicates player_num dies
				ByteBuffer bb = ByteBuffer.allocate(48);
				// tells the users someone dies at [row, col] and tell the users that
				// [row, col] needs to be turned into black
				bb.putInt(0).putInt(row).putInt(col).putInt(0).putInt(0).putInt(0);
				return bb;
			} else {  // other cases, nothing changes
				ByteBuffer bb = ByteBuffer.allocate(48);
				// ACK is -1, telling something wasn't right and change nothing
				bb.putInt(-1).putInt(-1).putInt(-1).putInt(-1).putInt(-1).putInt(-1);
				return bb;
			}
			ByteBuffer bb = ByteBuffer.allocate(48);
			// success, tell the users to change [row, col] into a specific color
			bb.putInt(1).putInt(row).putInt(col);
			// put in the color
			for (int i = 0; i < 3; i++) {
				bb.putInt(player_color[player_num][i]);
			}
			return bb;
		}
		
		public void assignPlayerColor() {
			// assign each player a color, each has three values represents the red, green and blue
			// so that it will be easier for GUI to generate color.
			// example color:
			//				1: 255,0,0
			//				2: 0,255,0
			//				3: 0,0,255
			//				4: 255,0,255
			//				5: 0,255,255
			//				6: 255,255,0
			// Board exploeded into black grid (0,0,0)
			// Board initialized to all white grid (255,255,255)
			ArrayList<HashSet<Integer>> colors = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				HashSet<Integer> ci = new HashSet<>();
				colors.add(ci);
			}
			// randomly assign colors to each player, can be deleted if want to use default settings like above through
			// hard coding
			Random rand = new Random();
			for (int i = 0; i < PLAYER; i++) {
				for (int j = 0; j < 3; j++) {
					int cur_color = rand.nextInt(256);
					while (colors.get(j).contains(cur_color)) {
						cur_color = rand.nextInt(256);	
					}
					player_color[i][j] = cur_color;
					colors.get(j).add(cur_color);
				}
			}
		}
		
	}
}

