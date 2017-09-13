package sjl.gol;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class LifeBox extends JPanel implements MouseListener {

	private boolean yellow;
    private boolean locked;
    
    public static final LifeBox DEFAULT_BOX;
    
    static {
        DEFAULT_BOX = new LifeBox();
        DEFAULT_BOX.lock();
    }

    /**
     * Default constructor. Starts gray.
     */
	public LifeBox(){
        
        yellow = false;
		setBackground(Color.GRAY);
        locked = false;

		addMouseListener(this);
    }

    /**
     * Is box yellow? Yellow = alive.
     * @return true if this box is yellow.
     */
	public final boolean isYellow(){
		return yellow;
	}

    /**
     * Called when this box is clicked. Response is to change the color of the box.
     * @param e No mouse event information needed.
     */
	public void mouseClicked(MouseEvent e){
		change();
	}

    /**
     * Change both the painted color and color status.
     */
	public void change(){
        if (!locked){
            
            if (!yellow){
                setBackground(Color.YELLOW);
                yellow = true;
            }
            else {
                setBackground(Color.GRAY);
                yellow = false;
            }
        }
	}
	
    /**
     * Make this box gray.
     */
	public void gray(){
		setBackground(Color.GRAY);
		yellow = false;
	}
	
    /**
     * Make this box yellow.
     */
	public void yellow(){
		setBackground(Color.YELLOW);
		yellow = true;
	}

    /**
     * Change color without changing color status.
     * 
     * Hint: all boxes in turn n are painted according to status on turn n-1.
     * This method is used to change the colors without changing the original status,
     * which is needed to calculate the status of the surrounding boxes.
     */
	public void changeColor(){
        if (!locked){
        
            if (!yellow){
                setBackground(Color.YELLOW);
            }
            else {
                setBackground(Color.GRAY);
            }
        }
	}

    /**
     * Change color status to agree with painted color.
     */
	public void fixColorStatus(){
		if (getBackground() == Color.YELLOW) yellow = true;
		else yellow = false;
	}

    /**
     * Lock current color and color status, will not respond to change calls.
     */
    public void lock(){
        locked = true;
    }

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
