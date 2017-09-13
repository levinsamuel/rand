/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import sjl.mlbapp.gui.util.ColorFactory;
import sjl.mlbapp.main.AppRunner;
import sjl.mlbapp.gui.util.GBConstrManager;
import sjl.mlbapp.gui.util.LabelFactory;

/**
 *
 * @author samlevin
 */
public class LoginPage extends JFrame implements ActionListener {
    
    private JTextField url;
    private JTextField user;
    private JPasswordField pw;
    private GBConstrManager gbcMgr;
    
    public static final String PW_FIELD_NAME = "LoginPage-DBPasswordField";
    
    public LoginPage(AppRunner app, String title){
        super(title);
        
        this.setSize(500,300);
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(ColorFactory.BACKGROUND);
        gbcMgr = new GBConstrManager();
        
        gbcMgr.setWeights(0.3, 0.5);
        this.add(LabelFactory.getNewDefaultLabel("URL:"), gbcMgr.getConstr(0, 0));
        this.add(LabelFactory.getNewDefaultLabel("User:"), gbcMgr.getConstr(0, 1));
        this.add(LabelFactory.getNewDefaultLabel("Password:"), gbcMgr.getConstr(0, 2));
        
        gbcMgr.setWeights(0.7, 0.5);
        
        url = new JTextField("jdbc:mysql://localhost:3306");
        url.setPreferredSize(new Dimension(200, 40));
        this.add(url, gbcMgr.newConstr(1, 0));
        
        user = new JTextField();
        user.setPreferredSize(new Dimension(200, 40));
        this.add(user, gbcMgr.newConstr(1, 1));
        
        pw = new JPasswordField();
        pw.setName(PW_FIELD_NAME);
        
        //pw.setAction();
        pw.setPreferredSize(new Dimension(200, 40));
        pw.addActionListener(app);
        this.add(pw, gbcMgr.newConstr(1, 2));
        
        url.requestFocusInWindow();
        
    }
    
    public String getURL(){
        return url.getText();
    }
    
    public String getUser(){
        return user.getText();
    }
    
    public String getPassword(){
        return String.valueOf(pw.getPassword());
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
}
