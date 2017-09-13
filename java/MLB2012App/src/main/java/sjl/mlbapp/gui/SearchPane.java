/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import sjl.mlbapp.data.DataManager;
import sjl.mlbapp.gui.util.ColorFactory;
import sjl.mlbapp.gui.util.GBConstrManager;
import sjl.mlbapp.gui.util.LabelFactory;

/**
 *
 * @author samlevin
 */
public class SearchPane extends JPanel implements ActionListener {
    
    private GBConstrManager gbcMgr;
    private DataManager data;
    public JTextField searchField;
    private MainContentPane parent;

    public SearchPane(MainContentPane parent, Dimension minSize){
        gbcMgr = new GBConstrManager();
        data = new DataManager();
        
        this.setLayout(new GridBagLayout());
        this.setMinimumSize(minSize);
        this.setPreferredSize(minSize);
        this.setBackground(ColorFactory.FOREGROUND);
        
        this.parent = parent;
        
    }
    
    public void create(){
        JLabel label = LabelFactory.getNewDefaultLabel("Enter Player Name");
        label.setLabelFor(this);
        JPanel labelBG = new JPanel();
        labelBG.setPreferredSize(new Dimension(250,75));
        labelBG.setBackground(ColorFactory.HIGHLIGHT);
        labelBG.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        labelBG.setLayout(new GridBagLayout());
        labelBG.add(label, gbcMgr.newConstr(0, 0));
        this.add(labelBG, gbcMgr.newConstr(0, 0));
        
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200,50));
        searchField.addActionListener(parent);
        
        this.add(searchField, gbcMgr.newConstr(0, 1));
        
        
        
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
}
