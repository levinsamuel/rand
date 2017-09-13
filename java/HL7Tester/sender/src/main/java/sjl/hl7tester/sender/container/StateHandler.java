/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sjl.hl7tester.sender.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.sender.HL7Sender;
import sjl.hl7tester.sender.message.MessageGenerator;
import sjl.hl7tester.sender.send.MessageSender;
import sjl.hl7tester.sender.send.Router;
import sjl.hl7tester.sender.util.SenderException;

/**
 *
 * @author samuel.levin
 */
public class StateHandler {
    
    private static final Logger log = LoggerFactory.getLogger(StateHandler.class);

    
    private State status;
    private final HL7Sender sender;
    private final static Object statusLock = new Object();
    
    enum State {
        STOPPED, STOPPING, PAUSED, STARTING, STARTED
    }
    
    public StateHandler(HL7Sender sender){
        status = State.STOPPED;
        this.sender = sender;
    }
    
    public void start() throws SenderException {
        synchronized (statusLock){
            status = State.STARTING;
            log.info("Starting HL7Sender");
            
            run();
            log.info("********************Sender startup completed successfully***************************");
            status = State.STARTED;
            statusLock.notifyAll();
        }
        
    }
    
    public Thread stop() {
        synchronized (statusLock){
            while (status == State.STOPPING){
                try {
                    statusLock.wait();
                } catch (InterruptedException ex){
                    log.error("Interrupted while waiting for status update", ex);
                    return null;
                }
            }
            if (status == State.STOPPED){
                return null;
            }
            status = State.STOPPING;
        }
        Thread ret = stopSending();

        MessageGenerator.resetMessageCount();

        return ret;
    }
    
    public Thread pause(){
        synchronized (statusLock){
            while (status == State.STOPPING || status == State.STARTING){
                try {
                    statusLock.wait();
                } catch (InterruptedException ex){
                    log.error("Interrupted while waiting for status update", ex);
                    return null;
                }
            }
            if (status != State.STARTED){
                return null;
            }
            status = State.PAUSED;
        }
        return stopSending();
    }
    
    public boolean isRunning() {
        synchronized (statusLock){
            while (status == State.STARTING){
                try {
                    statusLock.wait();
                } catch (InterruptedException ex){
                    log.error("Interrupted while waiting for status update", ex);
                    return false;
                }
            }
        }
        if (status != State.STARTING && status != State.STARTED) {
            return false;
        }
        boolean anyRunning = false;
        for (Router router : sender.routers()) {
            if (router.isRunning()) {
                anyRunning = true;
            }
        }
        if (!anyRunning) {
            stop();
        }
        return status == State.STARTED;
    }
    
    public synchronized void checkRouters(){
        log.trace("Checking Routers");
        for (Router router : sender.routers()) {
            if (router.isRunning()){
                log.debug("Router found to be running, HL7Sender will keep running");
                return;
            }
        }
        log.trace("No Routers Running");
        if (status != State.STARTING){
            log.debug("Stopping HL7Sender");
            this.stop();
        }
    }
    
    private void run() throws SenderException {
        try {
            for (Router router : sender.routers()) {
                router.start();
            }
        } catch (SenderException ex) {
            log.error("Could not start Router");
            stop();
            throw ex;
        }
        
    }
    
    private Thread stopSending(){
        Thread thd = new Thread(new StopperThread());
        thd.start();
        return thd;
    }
    
    public class StopperThread implements Runnable {
    
        public StopperThread(){
            
        }

        public void run() {
            
            for (Router router : sender.routers()) {
                if (router.isRunning()){
                    router.stop();
                }
                MessageSender[] senders = router.getSenders();
                for (MessageSender sender : senders) {
                    try {
                        sender.join();
                    } catch (InterruptedException ex) {
                        log.error("Stopper Thread interrupted while joining MessageSenders", ex);
                    }
                }
            }
            log.info("********************Stopping HL7Sender**************************");
            log.info("Total messages sent: {}", MessageGenerator.getMessageCount());
            
            synchronized (statusLock){
                if (status == State.STOPPING){
                    status = State.STOPPED;
                } else if (status == State.PAUSED){
                }
                statusLock.notifyAll();
            }
        }
    
    }
    
}
