/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.container;

import ca.uhn.hl7v2.HL7Exception;
import sjl.hl7tester.sender.util.TCPAddress;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;
import sjl.hl7tester.sender.HL7Sender;
import sjl.hl7tester.sender.message.MessageGenerator;
import sjl.hl7tester.sender.send.Router;
import sjl.hl7tester.sender.util.SenderException;

/**
 *
 * @author samuel.levin
 */
public class HL7SenderFrame extends JPanel implements ActionListener, HL7Sender {
    
    private static final Logger log = LoggerFactory.getLogger(HL7SenderFrame.class);
    
    ArrayList<Router> routers = null;
    
    private Properties props;
    
    private final JLabel sendLabel;
    private final JLabel sendDesc;
    private final JButton startButton;
    private final JButton pauseButton;
    
    private boolean isInitialized;
    
    private final StateHandler stateHandler;
    
    private static final Color DARK_GREEN = new Color(0, 190, 0);
    
    
    public HL7SenderFrame() {
                
        this.setPreferredSize(new Dimension(180,385));
        this.setMinimumSize(new Dimension(180,385));
        this.setMaximumSize(new Dimension(180,385));
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
        
        sendLabel = new JLabel("Sender not running");
        sendDesc = new JLabel("");
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        
        stateHandler = new StateHandler(this);
        
        isInitialized = false;
        
        initGUI();
    }
    
    private void initGUI(){
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;

        sendLabel.setForeground(Color.red);
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.2;
        this.add(sendLabel, c);
        
        sendDesc.setForeground(Color.black);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.2;
        sendDesc.setVisible(false);
        this.add(sendDesc, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.2;
        this.add(startButton, c);
        startButton.addActionListener(this);
        
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.2;
        this.add(pauseButton, c);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
    }
    
    private void init() throws SenderException {
        
        if (!isInitialized){
              
            props = HL7TesterProperties.getSenderProperties();
            MessageGenerator.reInitialize();

            /**
             * Sending Properties
             */
            routers = new ArrayList<Router>();

            Set<String> propNames = props.stringPropertyNames();
            Iterator<String> iter = propNames.iterator();
            while (iter.hasNext()) {
                String prop = iter.next();
                if (prop.matches("(\\d\\.)?send.to.host")) {
                    String host = props.getProperty(prop);
                    String port = props.getProperty(prop.replaceAll("host", "port"));
                    if (port != null) {
                        log.debug("Creating tcp address {}:{}", host, port);
                        routers.add(new Router(stateHandler, new TCPAddress(host, Integer.parseInt(port))));
                    }
                }
            }
            isInitialized = true;
        }
    }
    

    
    public void start() throws SenderException {
        this.init();
        stateHandler.start();
        startSenderGUI();
    }
    
    public void stop() throws InterruptedException {
        stateHandler.stop().join();
        stopSenderGUI();
    }
    
    public void pause() throws InterruptedException {
        stateHandler.pause().join();
        pauseSenderGUI();
    }
    
    public synchronized void checkRouters(){
        stateHandler.checkRouters();
    }
    
    public final TCPAddress[] getSenderAddresses() {
        TCPAddress[] addresses = new TCPAddress[routers.size()];
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = routers.get(i).getSendAddress();
        }
        return addresses;
    }
    
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startButton) {
            try {
                try {
                    startButton.setEnabled(false);
                    pauseButton.setEnabled(false);
                    if (stateHandler.isRunning()) {
                        stop();

                    } else {
                        start();
                        pauseButton.setEnabled(true);
                    }

                } catch (SenderException ex) {
                    log.error("Runner received Sender Exception. Showing fail dialog", ex);
                    JOptionPane.showMessageDialog(this, "<html>Sender Failed to start<br/>" + ex.getMessage() + "</html>");
                    if (stateHandler.isRunning()) {
                        stop();
                    }
                    stopSenderGUI();
                }
            } catch (InterruptedException ex) {
                log.error("Interrupted while waiting for sender to stop", ex);
            } finally {
                startButton.setEnabled(true);
            }
        }
        if (e.getSource() == pauseButton){
            try {
                startButton.setEnabled(false);
                pauseButton.setEnabled(false);
                
                if (stateHandler.isRunning()) {
                    pause();
                } else {
                    
                    log.warn("Should not be able to push pause button while sender is not running");
                    pauseButton.setEnabled(false);
                }
                
                
            } catch (InterruptedException ex) {
                log.error("Interrupted while waiting for sender to stop", ex);
            } finally {
                startButton.setEnabled(true);
            }
        }

    }
    
    public ArrayList<Router> routers(){
        return routers;
    }
    
    void startSenderGUI() {
        startButton.setText("Stop");
        sendLabel.setText("Sender running");
        
        TCPAddress[] addresses = getSenderAddresses();
        StringBuilder labelString = new StringBuilder("<html>Sending to:<br/>");
        for (TCPAddress address : addresses) {
            String host = address.getHostName();
            int port = address.getPort();
            labelString.append(host).append(":").append(port).append("<br/>");
        }
        labelString.append("</html>");
        sendDesc.setText(labelString.toString());
        
        sendDesc.setVisible(true);
        sendLabel.setForeground(DARK_GREEN);
    }

    void stopSenderGUI() {
        startButton.setText("Start");
        sendLabel.setText("Sender not running");
        sendLabel.setForeground(Color.red);
        sendDesc.setVisible(false);
    }
    
    void pauseSenderGUI() {
        pauseButton.setText("Resume");
        sendLabel.setText("Sender paused");
        sendLabel.setForeground(Color.yellow);
    }
    
    void resumeSenderGUI() {
        pauseButton.setText("Pause");
        sendLabel.setText("Sender running");
        sendLabel.setForeground(DARK_GREEN);
    }
    
    public static void main(String[] args) {
        HL7SenderFrame send = new HL7SenderFrame();
        //send.configFilePath = "src/main/config/sender.properties";
        //System.out.println(System.getProperty("user.dir"));

        try {
            send.init();
            MessageGenerator mg = MessageGenerator.getInstance();
            mg.setLocale("intl");
            System.out.println(mg.generateAdtAdd().encode());
            System.out.println(mg.generateAdtUpdate().encode());
        } catch (SenderException ex) {
            System.err.println(ex);
            log.error("Sender Exception", ex);
        } catch (HL7Exception ex) {
            System.err.println(ex);
            log.error("HL7 Exception", ex);
        } catch (IOException ex) {
            System.err.println(ex);
            log.error("IOException", ex);
        }
    }
    
}
