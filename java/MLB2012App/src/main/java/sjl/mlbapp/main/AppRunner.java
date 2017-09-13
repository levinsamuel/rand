/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.mlbapp.data.ConnectionManager;
import sjl.mlbapp.gui.LoginPage;
import sjl.mlbapp.gui.MainPage;

/**
 *
 * @author samlevin
 */
public class AppRunner implements ActionListener, Runnable {
    
    private LoginPage login;
    private MainPage mainPage;
    private static AppRunner app;
    
    private static Connection con;
    
    public static final Logger log = LoggerFactory.getLogger(AppRunner.class);
    
    public AppRunner(){
        
    }
    
    
    public static void main(String[] args){
        
        app = new AppRunner();
        app.start();
       
    }
    
    public void start(){
        Thread thd = new Thread(this);
        thd.start();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source.getClass().equals(JPasswordField.class)){
            JPasswordField pwField = (JPasswordField)source;
            if (pwField.getName().equals(LoginPage.PW_FIELD_NAME)){
                String url = login.getURL();
                String user = login.getUser();
                String password = login.getPassword();
                executeLogin(url, user, password);
            }
        }
    }
    
    public void executeLogin(String url, String user, String password) {
        
        try {
            con = DriverManager.getConnection(url, user, password);
            executeMain();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(login, "Invalid Database Credentials");
        }
    }
    
    public void executeMain(){
        login.setEnabled(false);
        login.setVisible(false);
        
        mainPage = new MainPage(con);
        mainPage.init();
    }

    @Override
    public void run() {
        
        login = new LoginPage(app, "Enter Database URL and credentials");
        login.setVisible(true);
    }
    
}
