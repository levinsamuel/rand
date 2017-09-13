/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 *
 * @author samlevin
 */
public class GBConstrManager extends GridBagConstraints {
    
   
    
    public GBConstrManager (){
        super();
    }
    
    /**
     * The methods containing the word 'new' will wipe an existing
     * configuration by calling resetDefaults. To configure
     * the constraints before retrieving, call 'get'
     * @param xPos
     * @param yPos
     * @return 
     */
    public GridBagConstraints newConstr(int xPos, int yPos){
        GridBagConstraints ret = new GridBagConstraints();
        resetDefaults(ret);
        ret.gridx = xPos;
        ret.gridy = yPos;
        return ret;
    }
    
     public GridBagConstraints newConstrByWeight(int xPos, int yPos, double xWgt, double yWgt){
        GridBagConstraints ret = new GridBagConstraints();
        resetDefaults(ret);
        ret.gridx = xPos;
        ret.gridy = yPos;
        ret.weightx = xWgt;
        ret.weighty = yWgt;
        return ret;
    }
    
    public static void resetDefaults(GridBagConstraints gbc){
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.insets = new Insets(0,0,0,0);
    }
    
    public GridBagConstraints getConstr(int xPos, int yPos){
        this.gridx = xPos;
        this.gridy = yPos;
        return this;
    }
    
    public GridBagConstraints getConstrByWeight(int xPos, int yPos, double xWgt, double yWgt){
        this.gridx = xPos;
        this.gridy = yPos;
        this.weightx = xWgt;
        this.weighty = yWgt;
        return this;
    }
    
    public void setInsets(Insets ins){
        this.insets = ins;
    }
    
    public void setPadding(int xPad, int yPad){
        this.ipadx = xPad;
        this.ipady = yPad;
    }
    
    public void setWeights(double xW, double yW){
        this.weightx = xW;
        this.weighty = yW;
    }
}
