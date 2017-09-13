package sjl.gol;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
/**
 * Conway's game of life.
 */
public class Life extends JFrame implements ActionListener {

	public Life(String s) {
        super(s);
	}

	private static Life gameOfLife;
	private static JButton reset;
	private static JButton start;
	private static JButton random;
	private static JButton next;
	private static JComboBox speed;
	private static JComboBox size;
	private static LifeGrid grid;
	private static Thread mThread = null;
	private static int time = 1000;

    /**
     * Construct the Game of Life GUI application.
     * @param args 
     */
	public static void main(String[] args) {

		gameOfLife = new Life("Conway's Game of Life");
		gameOfLife.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameOfLife.setSize(610, 750);
		gameOfLife.setResizable(false);
		gameOfLife.setLayout(new BoxLayout(gameOfLife.getContentPane(), BoxLayout.PAGE_AXIS));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setMaximumSize(new Dimension(600,50));
		reset = new JButton("Reset");
		start = new JButton("Start");
		next = new JButton("Next");
		random = new JButton("Random");

		buttonPanel.add(reset);
		buttonPanel.add(start);
		buttonPanel.add(next);
		buttonPanel.add(random);
		
		grid = new LifeGrid(45, 45);
		
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.LINE_AXIS));
		comboPanel.setMaximumSize(new Dimension(600,50));
		
		String[] speeds = {"Slow", "Medium", "Fast"};
		speed = new JComboBox(speeds);
		speed.setMaximumSize(new Dimension(200, 50));
		speed.setSelectedIndex(0);
		String[] sizes = {"Small", "Medium", "Large"};
		size = new JComboBox(sizes);
		size.setMaximumSize(new Dimension(200, 50));
		size.setSelectedIndex(1);
		
		comboPanel.add(speed);
		comboPanel.add(size);
		
		reset.addActionListener(gameOfLife);
		start.addActionListener(gameOfLife);
		next.addActionListener(gameOfLife);
		speed.addActionListener(gameOfLife);
		size.addActionListener(gameOfLife);
		random.addActionListener(gameOfLife);

		gameOfLife.add(grid);
		gameOfLife.add(buttonPanel);
		gameOfLife.add(comboPanel);

		gameOfLife.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == reset){
			grid.gray();
		} else if (arg0.getSource() == start){
            //Run until interrupted if idle, interrupt if live.
			if (mThread == null || !mThread.isAlive()) {
				reset.setEnabled(false);
				random.setEnabled(false);
				next.setEnabled(false);
                mThread = new Thread(new Changer());
				mThread.start();
				start.setText("Stop");
			} else {
				mThread.interrupt();
				start.setText("Start");
				reset.setEnabled(true);
				random.setEnabled(true);
				next.setEnabled(true);
			}
		} else if (arg0.getSource() == next){
            //Run one generation.
			grid.run();
			grid.fixColorStatus();
		} else if (arg0.getSource() == random){
            //Randomly assign values to grid.
			grid.randomize();
		} else if (arg0.getSource() == speed){
            //Change speed, or wait time between generations.
	        String selection = (String)speed.getSelectedItem();
	        if (selection.equals("Fast")) time = 20;
	        if (selection.equals("Medium")) time = 100;
	        if (selection.equals("Slow")) time = 1000;
		} else {
            //Change size of grid, i.e. number of boxes.
			String selection = (String)size.getSelectedItem();
	        if (selection.equals("Small")){
	        	gameOfLife.remove(grid);
	        	grid = new LifeGrid(30,30);
	        	gameOfLife.add(grid,0);
	        	gameOfLife.validate();
	        }
	        if (selection.equals("Medium")) {
	        	gameOfLife.remove(grid);
	        	grid = new LifeGrid(45,45);
	        	gameOfLife.add(grid,0);
	        	gameOfLife.validate();
	        }
	        if (selection.equals("Large")) {
	        	gameOfLife.remove(grid);
	        	grid = new LifeGrid(60,60);
	        	gameOfLife.add(grid,0);
	        	gameOfLife.validate();
	        }
		}
	}

    /**
     * Runs generations of the Game Of Life until interrupted, or until the
     * game reaches a static point.
     */
	private static class Changer implements Runnable {
		public void run() {
			try {
				boolean more = true;
				while (more){
					more = grid.run();
					grid.fixColorStatus();
					Thread.sleep(time);
				}
			} catch (InterruptedException e) {
				//e.printStackTrace();
				//Expected interruptions during GUI use.
                return;
			}
            start.setText("Start");
            reset.setEnabled(true);
            random.setEnabled(true);
            next.setEnabled(true);
		}
	}
}
