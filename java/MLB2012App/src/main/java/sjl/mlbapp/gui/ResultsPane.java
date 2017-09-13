/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import sjl.mlbapp.data.DataManager;
import sjl.mlbapp.gui.util.ColorFactory;
import sjl.mlbapp.gui.util.GBConstrManager;
import sjl.mlbapp.gui.util.LabelFactory;
import sjl.mlbapp.objects.PlayerName;

/**
 *
 * @author samlevin
 */
public class ResultsPane extends JPanel {
    
    private GBConstrManager gbcMgr;
    private DataManager data;
    private JScrollPane scroller;
    private JList resultsPane;
    private MainContentPane parent;

    
    public ResultsPane(MainContentPane parent, Dimension minSize){
        
        gbcMgr = new GBConstrManager();
        data = new DataManager();
        
        this.setMinimumSize(minSize);
        this.setPreferredSize(minSize);
        this.setLayout(new GridBagLayout());
        this.setBackground(ColorFactory.FOREGROUND);
        
        this.parent = parent;
    }
    
    public void create(){
        JLabel label = LabelFactory.getNewDefaultLabel("Results");
        label.setLabelFor(this);
        JPanel labelBG = new JPanel();
        labelBG.setPreferredSize(new Dimension(200,40));
        labelBG.setBackground(ColorFactory.HIGHLIGHT);
        labelBG.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        labelBG.setLayout(new GridBagLayout());
        labelBG.add(label, gbcMgr.newConstr(0, 0));
        this.add(labelBG, gbcMgr.newConstr(0, 0));
        
        resultsPane = new JList();
        //resultsPane.setMinimumSize(new Dimension(250,100));
        resultsPane.setPreferredSize(new Dimension(250,500));
        resultsPane.setLayoutOrientation(JList.VERTICAL);
        resultsPane.setVisibleRowCount(100);
        
        scroller = new JScrollPane(resultsPane);
        //scroller.setMinimumSize(resultsPane.getPreferredScrollableViewportSize());
        scroller.setPreferredSize(new Dimension(270,100));
        this.add(scroller,gbcMgr.newConstr(0, 1));
        
        
        this.setVisible(true);
    }
    
    public void showSearchResults(String name){
        
        ArrayList<PlayerName> players = data.findPlayers(name);
        
        resultsPane.setListData(players.toArray(new PlayerName[0]));
        
        resultsPane.validate();
        
    }
    
}
