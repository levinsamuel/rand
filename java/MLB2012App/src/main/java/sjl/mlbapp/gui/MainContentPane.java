/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import sjl.mlbapp.gui.util.ColorFactory;
import sjl.mlbapp.gui.util.GBConstrManager;

/**
 *
 * @author samlevin
 */
public class MainContentPane extends JPanel implements ActionListener {
    
    private Dimension searchSize;
    private Dimension resultSize;
    private Dimension preferredSize;
    private GBConstrManager gbcMgr;
    private SearchPane searchPane;
    private ResultsPane resultsPane;
    
    public MainContentPane(){
        searchSize = new Dimension(400,250);
        resultSize = new Dimension(400,350);
        preferredSize = new Dimension(500,350);
        gbcMgr = new GBConstrManager();
        
        this.setBackground(ColorFactory.BACKGROUND);
        this.setLayout(new GridBagLayout());
        
        searchPane = new SearchPane(this, searchSize);
        searchPane.create();
        gbcMgr.setInsets(new Insets(50,75,20,75));
        //gbcMgr.setPadding(60, 50);
        this.add(searchPane, gbcMgr.getConstrByWeight(0, 0, 0.5f, 0.3f));
        
        resultsPane = new ResultsPane(this, resultSize);
        resultsPane.create();
        gbcMgr.setInsets(new Insets(40,40,40,40));
        this.add(resultsPane, gbcMgr.getConstrByWeight(0, 1, 0.5f, 0.7f));
        
        
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchPane.searchField){
            
                String player = searchPane.searchField.getText();
                resultsPane.showSearchResults(player);
                //HashMap stats = data.getStatsByPlayerName(nameTokens[0], nameTokens[1]);
                //System.out.println(stats);
                searchPane.searchField.setText("");
            
        }
    }
    
}
