import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;



/**
 * 
 */

/**
 * @author ylh96
 *
 */
public class mineboard {

	private final JPanel gui = new JPanel();
	private JButton[][] buttons;
	private int size;
	private JPanel mine_board;
	public Socket socket;
	mine_player mp;

	mineboard(String hostname, int port_num) throws IOException{
		InetAddress IPAddress = InetAddress.getByName(hostname);
		
		// connect the server 
		this.socket = new Socket(IPAddress, port_num);
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
		mp = new mine_player();
		// read server packet
		InputStream in = socket.getInputStream();
		DataInputStream dis = new DataInputStream(in);
		byte[] data = new byte[48];
		dis.readFully(data);
		// if failed quit
		if ((this.size = mp.handleFirstPacket(mp.decodePacket(ByteBuffer.wrap(data)))) == -1) return;
		this.buttons = new JButton[size][size];
		initializeGui();
	}

	public final void initializeGui() {

		mine_board = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JButton selectedButton = (JButton) e.getSource();
				for (int row = 0; row < buttons.length; row++) {
					for (int col = 0; col < buttons[row].length; col++) {
						if (buttons[row][col] == selectedButton) {
							setColor(row, col, mp.mycolor);
							break;
						}
					}
				}
			}
		
			/**
			 * Override the preferred size to return the largest it can, in
			 * a square shape.  Must (must, must) be added to a GridBagLayout
			 * as the only component (it uses the parent as a guide to size)
			 * with no GridBagConstaint (so it is centered).
			 */
			@Override
			public final Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				Dimension prefSize = null;
				Component c = getParent();
				if (c == null) {
					prefSize = new Dimension(
							(int)d.getWidth(),(int)d.getHeight());
				} else if (c!=null &&
						c.getWidth()>d.getWidth() &&
						c.getHeight()>d.getHeight()) {
					prefSize = c.getSize();
				} else {
					prefSize = d;
				}
				int w = (int) prefSize.getWidth();
				int h = (int) prefSize.getHeight();
				// the smaller of the two sizes
				int s = (w>h ? h : w);
				return new Dimension(s,s);
			}
		};

		mine_board.setBorder(new CompoundBorder(
				new EmptyBorder(size,size,size,size),
				new LineBorder(Color.BLACK)
				));
	
		mine_board.setBackground(new Color(255,255,255));
		JPanel boardConstrain = new JPanel(new GridBagLayout());
		boardConstrain.setBackground(new Color(255,255,255));
		boardConstrain.add(mine_board);
		gui.add(boardConstrain);
	
		// create the mine board squares
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		for (int ii = 0; ii < buttons.length; ii++) {
			for (int jj = 0; jj < buttons[ii].length; jj++) {
				JButton b = new JButton();
				b.setMargin(buttonMargin);
				buttons[jj][ii] = b;
			}
		}

	}

	// handles server's response
	// ia[0] is ack: whether a client is dead or not
	// ia[1], ia[2]: coordinate
	// ia[3], ia[4], ia[5] are colors
	public static void handleServerPacket(int[] ia) {
		if (ia[0] == -1) {  // error case, do nothing
			return;
		} else if (ia[0] == 0) {  // explode case
			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
			if (mine_player.madeMove) {  // it was this user who exploded, disconnect
				System.out.println("YOU EXPLODED");
				// disconnect
				return;
			}
		} else {
			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
			mine_player.madeMove = false;  // move completed
		}
	}
	
	// utility function to set color to [row, col]
	public static void setColor(int row, int col, int[] color) {
	
	}
	
	// utility function to create an actual GUI on client screen
	public static void createPanel(int size) {
		// create a graphical grid
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter host name");
		String hostname = scan.nextLine();
		int port_num = scan.nextInt();
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mineboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mineboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mineboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mineboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
					new mineboard(hostname, port_num);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	
	
	
	
	}

}
