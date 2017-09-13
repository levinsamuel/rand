/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.runner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.receiver.HL7Receiver;
import sjl.hl7tester.receiver.ReceiverException;
import sjl.hl7tester.sender.container.HL7SenderFrame;
import sjl.hl7tester.sender.util.SenderException;
import sjl.hl7tester.sender.util.TCPAddress;

/**
 *
 * @author samuel.levin
 */
public class HL7Runner extends JFrame implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(HL7Runner.class);
    private static HL7Receiver receiver;
    private static HL7SenderFrame sender;
    private static HL7Runner runner;
    private static JLabel recvLabel;
    private static JLabel recvDesc;
    private static JButton recvButton;
    public static boolean senderOn;
    public static boolean receiverOn;
    private static final Color DARK_GREEN = new Color(0, 190, 0);

    private HL7Runner() {
        super("HL7 Tester");
        setSize(new Dimension(500, 400));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        init();
    }

    private void init() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        
        log.debug("Creating HL7Sender");
        sender = new HL7SenderFrame();
        c.gridheight = 3;
        c.weighty = 0.2;
        c.gridx = 0;
        c.gridy = 0;
        this.add(sender, c);
        
        c.gridheight = 1;
        
        recvLabel = new JLabel("Receiver not running");
        recvLabel.setForeground(Color.red);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.2;
        this.add(recvLabel, c);

        recvDesc = new JLabel("");
        recvDesc.setForeground(Color.black);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.2;
        recvDesc.setVisible(false);
        this.add(recvDesc, c);

        recvButton = new JButton("Start");
        recvButton.addActionListener(this);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.5;
        this.add(recvButton, c);

        senderOn = false;
        receiverOn = false;
    }

    public static void main(String[] args) {

        log.debug("Creating HL7Runner");
        runner = new HL7Runner();
        log.debug("Creating HL7Receiver");
        receiver = new HL7Receiver();
        log.debug("Creating GUICheck");
        GUICheck gc = new GUICheck(runner, sender, receiver);
        Thread gcThread = new Thread(gc);
        //gcThread.start();

        log.info("Configuration files located at {}{}config", System.getProperty("user.dir"), File.separator);

        runner.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        

        if (e.getSource() == recvButton) {
            try {
                recvButton.setEnabled(false);
                if (receiver.isRunning()) {
                    runner.stopReceiverGUI();
                    receiver.stop();
                } else {
                    receiver.start();
                    runner.startReceiverGUI();
                }
                
            } catch (RuntimeException ex) {
                log.error("Runner received Runtime Exception.", ex);
                JOptionPane.showMessageDialog(this, "<html>Receiver Failed to start<br/>" + ex.getMessage() + "</html>");
                if (receiver.isRunning()) {
                    receiver.stop();
                }
                runner.stopReceiverGUI();
            } catch (ReceiverException ex) {
                log.error("Caught Receiver exception during startup", ex);
                JOptionPane.showMessageDialog(this, "<html>Receiver Failed to start<br/>" + ex.getMessage() + "</html>");
                if (receiver.isRunning()) {
                    receiver.stop();
                }
                runner.stopReceiverGUI();
            } finally {
                recvButton.setEnabled(true);
            }
        }
    }

    public void startReceiverGUI() {
        recvButton.setText("Stop");
        recvLabel.setText("Receiver running");
        recvDesc.setText("Listening on port " + receiver.getPort());
        recvDesc.setVisible(true);
        recvLabel.setForeground(DARK_GREEN);
        receiverOn = true;
    }

    public void stopReceiverGUI() {
        recvButton.setText("Start");
        recvLabel.setText("Receiver not running");
        recvLabel.setForeground(Color.red);
        recvDesc.setVisible(false);
        receiverOn = false;
    }
}
