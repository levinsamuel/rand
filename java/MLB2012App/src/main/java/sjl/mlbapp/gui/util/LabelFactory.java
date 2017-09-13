/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui.util;

import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author samlevin
 */
public abstract class LabelFactory {
    
    public static JLabel getNewDefaultLabel(String text){
        JLabel ret = new JLabel(text);
        ret.setForeground(ColorFactory.TEXT_COLOR);
        ret.setBackground(ColorFactory.HIGHLIGHT);
        return ret;
    }
    
}
