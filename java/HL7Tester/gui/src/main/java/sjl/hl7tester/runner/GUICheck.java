/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.runner;

import org.slf4j.LoggerFactory;
import sjl.hl7tester.receiver.HL7Receiver;
import sjl.hl7tester.sender.container.HL7SenderFrame;

/**
 *
 * @author samuel.levin
 */
public class GUICheck implements Runnable {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GUICheck.class);
    private boolean running;
    private HL7SenderFrame sender;
    private HL7Receiver receiver;
    private HL7Runner runner;
    
    public GUICheck(HL7Runner runner, HL7SenderFrame sender, HL7Receiver receiver){
        this.runner = runner;
        this.sender = sender;
        this.receiver = receiver;
        
        running = false;
    }
    
    public void run() {
        running = true;
        while (running){
            try {
                Thread.sleep(1000);
                if (receiver.isRunning() && !HL7Runner.receiverOn){
                    runner.startReceiverGUI();
                } else if (!receiver.isRunning() && HL7Runner.receiverOn){
                    runner.stopReceiverGUI();
                }
            } catch (InterruptedException ex) {
                log.error("",ex);
            }
            
            
        }
    }
    
}
