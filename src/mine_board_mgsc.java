import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.swing.*;
import javax.swing.border.*;

public class mine_board_mgsc extends Frame implements ActionListener{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JPanel mineBoard;
    private int size; // = 25;  // HERE
    private static JButton[][] buttons; // = new JButton[size][size];  // HERE
    public static SocketChannel socket;
    public static SocketChannel game_socket;
	public static MinePlayerClass mp;
	public static int[] change_color = new int[3];

    mine_board_mgsc(String hostname, int port_num) throws IOException {
    	InetAddress IPAddress = InetAddress.getByName(hostname);

		// connect the server 
		socket = SocketChannel.open(); 
		socket.connect(new InetSocketAddress(IPAddress, port_num));
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
					System.exit(error_count);
				}
			}
		}
		System.out.println("start initialize mp");
		mp = new MinePlayerClass();
		// read server packet
		ByteBuffer data = ByteBuffer.allocate(4);
		socket.read(data);
		System.out.println("finish read");
		int socket_num = data.getInt(0);
		System.out.println("socket num: " + socket_num);
		// if failed quit
		if (socket_num == -1) System.exit(ABORT);
		mine_board_mgsc.game_socket = SocketChannel.open(); 
		game_socket.connect(new InetSocketAddress(IPAddress, socket_num));
		// game_socket.configureBlocking(false);
		error_count = 0;
		while(true) {
			if(game_socket.isConnected()) {
				break;
			} else {
				error_count++;
				if(error_count == max_error) {
					game_socket.close();
					System.out.println("Failed");
					System.exit(error_count);
				}
			}
		}
		System.out.println(game_socket.isConnected());
		ByteBuffer gdata = ByteBuffer.allocate(24);
		game_socket.read(gdata);
		int[] iah = mp.decodePacket(gdata);
		for (int i = 0; i < iah.length; i++) {
			System.out.println(iah[i]);
		}
		if ((this.size = mp.handleFirstPacket(iah)) == -1) System.exit(ABORT);;
		mine_board_mgsc.buttons = new JButton[size][size];
		System.out.println(size);
        initializeGui();
    }
    
    public void initializeGui() {
        // set up the main GUI
    	System.out.println("inside initialize gui");
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        // gui.add(new JLabel("?"), BorderLayout.LINE_START);

        mineBoard = new JPanel(new GridLayout(0, size));
        mineBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(mineBoard);

        // create the chess board squares
        Insets buttonMargin = new Insets(0,0,0,0);
        
        for (int ii = 0; ii < size; ii++) {
            for (int jj = 0; jj < size; jj++) {
            	JButton b = new JButton();
                b.setMargin(buttonMargin);
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                buttons[ii][jj] = b;
                b.setOpaque(true);
                b.setBorderPainted(true);
                b.addActionListener(this);
                // b.addActionListener(this);  // ONLY CHANGE COLOR WHEN THE SERVER ASKS TO
                b.setBackground(Color.white); 
                mineBoard.add(buttons[ii][jj]);
            }
        }
        System.out.println("end initialization");
        
    }

    public final JComponent getmineBoard() {
        return mineBoard;
    }

    public final JComponent getGui() {
        return gui;
    }
    
    // handles server's response
 	// ia[0] is ack: whether a client is dead or not
 	// ia[1], ia[2]: coordinate
 	// ia[3], ia[4], ia[5] are colors
 	public static boolean handleServerPacket(int[] ia) throws IOException {
 		// Win, put all board to the winner's color
 		if (ia[0] == 2) {
 			System.out.println("Winner!!!!!!");
 			for (int i = 0; i < buttons.length; i++) {
 				for (int j = 0; j < buttons[0].length; j++) {
 					setColor(i, j, MinePlayerClass.mycolor);
 				}
 			}
 			// but game still ends, thus return false
 			return false;
 		}
 		if (ia[0] == -1) {
 			System.out.println("Error in game, exiting...");
 			return false;
 		} else if (ia[0] == 0) {  // explode case
 			setColor(ia[1], ia[2], new int[]{0, 0, 0});
 			if (ia[3] == MinePlayerClass.mycolor[0]
 					&& ia[4] == MinePlayerClass.mycolor[1]
 					&& ia[5] == MinePlayerClass.mycolor[2]) {  // it was this user who exploded, disconnect
 				System.out.println("YOU EXPLODED");
 				return false;
 			}
 			return true;
 		} else {
 			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
 			return true;
 		}
 	}
 	
 	public static void setColor(int row, int col, int[] color) throws IOException {
 		JButton selectedButton = buttons[row][col];
 		selectedButton.setBackground(new Color(color[0], color[1], color[2])); 
 		// set the right color
 	}

 	public static void readFromServer() throws IOException {
 		while (true) {
 			System.out.println("read from server");
 			ByteBuffer data = ByteBuffer.allocate(24);
 			game_socket.read(data);
 			int[] a = mp.decodePacket(data);
 			if (!handleServerPacket(a)) break;
 		}
 		game_socket.close();
 		socket.close();
 		System.out.println("Your game is ended");
 	}

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        Runnable r = new Runnable() {

            @Override
            public void run() {
            	// Scanner scan = new Scanner(System.in);
            	// System.out.println("Enter host name: ");
            	// String host_name = scan.nextLine();
            	String host_name = "attu1.cs.washington.edu";
            	// System.out.println("Enter port number: ");
            	// int portnum = scan.nextInt();
            	// scan.close();
            	int portnum = 11223;
                mine_board_mgsc cb = null;
				try {
					cb = new mine_board_mgsc(host_name, portnum);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("abort");
					System.exit(ABORT);
				}

                JFrame f = new JFrame("mine_board_mgsc");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);
                f.setResizable(true);
                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
                System.out.println("done setting up gui");
                /*
                try {
					readFromServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                */
            }
        };
        // SwingUtilities.invokeLater(r);
        SwingUtilities.invokeAndWait(r);
        try {
			readFromServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void actionPerformed(ActionEvent e) {
		JButton selectedButton = (JButton) e.getSource();
		boolean done = false;
		System.out.println("in listener");
		for (int row = 0; row < buttons.length; row++) {
			for (int col = 0; col < buttons[row].length; col++) {
				if (buttons[row][col] == selectedButton) {
					try{
						System.out.println(row + " " + col);
						byte[] res = mp.action(row, col);
						System.out.println(game_socket.isConnected());
						System.out.println(game_socket.isBlocking());
						ByteBuffer resbb = ByteBuffer.wrap(res);
						for (int i = 0; i < 3; i++) {
							System.out.println(resbb.getInt(i * 4));
						}
						// scoket send packet to server
						game_socket.write(resbb);
						System.out.println("after write");
						done = true;
						break;
					}catch(IOException i) {
						System.out.println("IOEception");
					}
				}
			}
			if (done) break;
		}
	}
}


