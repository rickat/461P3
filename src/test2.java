import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.swing.*;
import javax.swing.border.*;

public class test2 extends Frame implements ActionListener{

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JPanel chessBoard;
    private static int size = 50;
    private static JButton[][] buttons = new JButton[size][size];

    test2() {
        initializeGui();
    }

    public void actionPerformed(ActionEvent e) {
 		JButton selectedButton = (JButton) e.getSource();
 		boolean done = false;
 		for (int row = 0; row < buttons.length; row++) {
 			for (int col = 0; col < buttons[row].length; col++) {
 				if (buttons[row][col] == selectedButton) {
 					System.out.println("Row: " + row + ", Col: " + col);
 					done = true;
 					break;
 				}
 			}
 			if (done) break;
 		}
 	}
    
    public void initializeGui() {
        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        // gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel(new GridLayout(0, size));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(chessBoard);

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
               b.addActionListener(this);
               chessBoard.add(buttons[ii][jj]);
            }
        }
        
        
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }
    
 // handles server's response
 	// ia[0] is ack: whether a client is dead or not
 	// ia[1], ia[2]: coordinate
 	// ia[3], ia[4], ia[5] are colors
 	public static boolean handleServerPacket(int[] ia) throws IOException {
 		if (ia[0] == -1) {  // error case, do nothing
 			return false;
 		} else if (ia[0] == 0) {  // explode case
 			setColor(ia[1], ia[2], new int[]{ia[3], ia[4], ia[5]});
 			if (ia[3] == mine_player.mycolor[0]
 					&& ia[4] == mine_player.mycolor[1]
 					&& ia[5] == mine_player.mycolor[2]) {  // it was this user who exploded, disconnect
 				System.out.println("YOU EXPLODED");
 				// disconnect
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
 		// set the right color
 	}

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                test2 cb =
                        new test2();

                JFrame f = new JFrame("test2");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}
