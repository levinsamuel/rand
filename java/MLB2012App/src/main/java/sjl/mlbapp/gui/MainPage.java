/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.mlbapp.main.AppRunner;

/**
 *
 * @author samlevin
 */
public class MainPage extends JFrame {
    
    private static final Logger log = LoggerFactory.getLogger(MainPage.class);
    public static Connection con;

    
    public MainPage(Connection con){
        super("MLB Data Page Home Test");
        
        this.con = con;
        
    }
    
    public void init(){
        
        
        Dimension frameSize = new Dimension(600,400);
        this.setLayout(new GridBagLayout());
        //this.setSize(frameSize);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MainContentPane contentPane = new MainContentPane();
        this.getRootPane().setContentPane(contentPane);
        //this.add(searchPane, gbcMgr.newDefault(0, 0));
        
        this.setMinimumSize(this.getContentPane().getMinimumSize());
        this.setVisible(true);
        
        
    }
    
    public static void main(String[] args){
        
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MLB", "samlevin", "calamity");
            MainPage mp = new MainPage(con);
            mp.init();
        } catch (SQLException ex){
            
        }
    }
    
    
}
