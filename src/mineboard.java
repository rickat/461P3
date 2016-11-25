import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
	
	
	mineboard(int size){
		this.buttons = new JButton[size][size];
		this.size = size;
        initializeGui();
    }

	public final void initializeGui() {
		
		mine_board = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
