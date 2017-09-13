package sjl.gol;

import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

@SuppressWarnings("serial")
public class LifeGrid extends JPanel{
    
	private final LifeBox[] boxes;
	private final int width;
	private final int height;
    
    private static final Logger mLogger = Logger.getLogger(LifeGrid.class.getName());
	
    /**
     * Creates grid of width w and height h.
     * @param w Width.
     * @param h Height.
     */
	public LifeGrid (int w, int h){
		width = w; height = h;
		boxes = new LifeBox[width*height];
		
		setMaximumSize(new Dimension(600,600));
		setLayout(new GridLayout(width, height, 1, 1));
		
		for (int i = 0; i < width*height; i++){
			boxes[i] = new LifeBox();
			add(boxes[i]);
		}
		
		
	}
    
    /**
     * Runs one generation of the game on the grid, given current configuration.
     * @return true if there were any changes between current generation and previous.
     */
    public boolean run(){
        return run(1,1);
    }
	
    /**
     * Recursively runs one generation of the game on the grid, given current configuration.
     * This method checks and changes (if necessary) the color status of the box
     * identified by the grid position arguments and then calls itself on the box below.
     * Also calls itself on the box to the right, if this box is on the top row.
     * In this way all boxes are checked, but none are duplicated.
     * @param x Horizontal position of box to check.
     * @param y Vertical position of box to check
     * @return true if this box was changed, or if any box deeper in the recursion
     * from this box was changed.
     */
	private boolean run(int x, int y){
        
		boolean change = false, changeX = false, changeY = false;
        //check the box below if there are any.
		if (y < height) changeY = run(x,y+1);
        //check the box to the right if there are any, and if we are currently
        //on the top row.
		if (y == 1 && x < width) changeX = run(x+1,y);
        
		if (getPos(x,y).isYellow()){
            mLogger.log(Level.FINE, "Block: ({0},{1}), Yellow, Count = {2}", new Object[]{x, y, liveCount(x,y)});
            if (liveCount(x,y) < 2 || liveCount(x,y) > 3){
                getPos(x,y).changeColor();
                change = true;
            }
        } else {
            mLogger.log(Level.FINE, "Block: ({0},{1}), Not Yellow, Count = {2}", new Object[]{x, y, liveCount(x,y)});
            if (liveCount(x,y) == 3){
                getPos(x,y).changeColor();
                change = true;
            }
        }
		return change || changeX || changeY;
	}
	
    /**
     * Each box has a 50/50 shot of either changing or remaining the same.
     */
	public void randomize(){
		for (int i = 0; i < width*height; i++){
			if (new Random().nextInt(2) == 1) boxes[i].change();
		}
	}
	
    /**
     * Changes color status of all boxes to agree with their painted color.
     */
	public void fixColorStatus(){
		for (int i = 0; i < width*height; i++){
			boxes[i].fixColorStatus();
		}
	}
	
    /**
     * Make all boxes gray (dead).
     */
	public void gray(){
		for (int i = 0; i < width*height; i++){
			boxes[i].gray();
		}
	}
	
    /**
     * Make all boxes yellow (live).
     */
	public void yellow(){
		for (int i = 0; i < width*height; i++){
			boxes[i].yellow();
		}
	}
    
    /**
     * Get the LifeBox in the array given the grid coordinates. If a box outside the
     * border is requested, the default box is returned.
     * @param x Horizonal position of box in grid.
     * @param y Vertical position of box in grid.
     * @return The LifeBox object in that position. Returns a default (gray) box
     * if box requested is out of bounds.
     */
    private LifeBox getPos(int x, int y){
		if (x < 1 || x > width || y < 1 || y > height)
			return LifeBox.DEFAULT_BOX;
		return boxes[width*(y-1) + x - 1];
	}
	
	private int liveCount(int x, int y){
		int ycount = 0;
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				if (getPos(x-1+i,y-1+j).isYellow() && (i != 1 || j != 1)){
					ycount++;
				}
			}
		}
		return ycount;
	}
	
	
}
