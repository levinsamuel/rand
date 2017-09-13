/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.send;

import sjl.hl7tester.sender.message.MessageGenerator;
import sjl.hl7tester.sender.util.SenderException;
import sjl.hl7tester.sender.util.TCPAddress;
import ca.uhn.hl7v2.llp.LLPException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;
import sjl.hl7tester.sender.HL7Sender;
import sjl.hl7tester.sender.container.StateHandler;

/**
 *
 * @author samuel.levin
 */
public class Router implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Router.class);
    private final StateHandler stateHandler;
    private final TCPAddress address;
    private final int threadCount;
    public boolean running;
    private MessageSender[] senders;
    private HashMap<String, Long> idCounts;
    
    private Thread routerThread;

    public Router(StateHandler state, TCPAddress address) throws SenderException {
        this.address = address;
        this.stateHandler = state;

        Properties props = HL7TesterProperties.getSenderProperties();

        threadCount = Integer.parseInt(props.getProperty("sender.thread.count", "1"));
        if (threadCount < 1) {
            throw new SenderException("Must start at least 1 thread");
        }

        MessageGenerator mg = MessageGenerator.getInstance();

        idCounts = new HashMap<String, Long>();
        long baseIdNum = mg.getBaseIdNum();
        List<String> systems = mg.getSystems();

        for (String system : systems) {
            idCounts.put(system, baseIdNum);
        }

    }

    public void start() throws SenderException {
        running = true;
        senders = new MessageSender[threadCount];
        log.info("Starting {} new MessageSender threads to address {}:{}", new Object[]{threadCount, address.getHostName(), address.getPort()});
        for (int i = 0; i < senders.length; i++) {
            senders[i] = new MessageSender(this, address, "MessageSender-thread-" + i);
            try {
                senders[i].init();
            } catch (IOException ex) {
                log.error("Error intializing a MessageSender object");
                throw new SenderException(ex.getMessage());
            } catch (LLPException ex) {
                log.error("Error intializing a MessageSender object");
                throw new SenderException(ex.getMessage());
            }

        }
        routerThread = new Thread(this);
        routerThread.start();
    }

    public synchronized void stop() {
        if (!running){
            return;
        }
        running = false;
    }
    
    public synchronized void stopAndCheck() {
        if (running){
            running = false;
        }
       // for (int i = 0; i < senders.length; i++) {
         //   senders[i].stopSender();
        //}
        stateHandler.checkRouters();
    }

    public boolean isRunning() {
        return running;
    }
    
    public MessageSender[] getSenders(){
        return senders;
    }

    public TCPAddress getSendAddress() {
        return address;
    }
    
    public Map<String,Long> getIdCounts(){
        return idCounts;
    }
    
    public Thread getRouterThread(){
        return routerThread;
    }
    
    private void throwMessageSenderException(Throwable thr) throws SenderException{
        throw new SenderException(thr);
    }

    public void run() {
        for (int i = 0; i < senders.length; i++) {
            senders[i].start();
        }
        for (int i = 0; i < senders.length; i++) {
            try {
                log.trace("Joining sender thread {}",senders[i].getName());
                senders[i].join();
                
            } catch (InterruptedException ex) {
                log.error("Router Interrupted while wating for Sender to stop");
                
            }
        }
        log.trace("Joined with Message Senders, stopping Router");
        running = false;
        stateHandler.checkRouters();
    }
}
