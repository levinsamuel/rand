/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.receiver;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.validation.builder.support.NoValidationBuilder;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;

/**
 *
 * @author samuel.levin
 */
public class HL7Receiver implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HL7Receiver.class);
    private HL7Service server;
    private ReceiverApplication handler;
    private boolean startupSuccessful;
    private boolean running;
    private final HapiContext context;
    private int port;
    private final String[] messageTypes;
    
    public HL7Receiver() {
        context = new DefaultHapiContext();
        context.setValidationRuleBuilder(new NoValidationBuilder());
        context.setModelClassFactory(new DefaultModelClassFactory());
        this.running = false;
        messageTypes = new String[]{"A01","A04","A08","A31","A39","A40","A44"};
    }

    public void init() throws ReceiverException {
        //log.debug("Sr");
        startupSuccessful = false;

        Properties props = HL7TesterProperties.getReceiverProperties();
        port = Integer.parseInt(props.getProperty("receiver.listen.port"));
        log.info("listening on port " + port);

        server = context.newServer(port, false);

        handler = new ReceiverApplication();
        for (String messageType : messageTypes) {
            server.registerApplication("ADT", messageType, handler);
            log.info("Registering message type " + messageType);
        }

        startupSuccessful = true;
    }

    public void run() {
        server.start();
    }

    public void start() throws RuntimeException, ReceiverException {
        if (running){
            log.error("Trying to start a running HL7Receiver");
            throw new RuntimeException("Receiver is already running when start was called. Stop running server first");
        }
        
        this.init();
        if (startupSuccessful){
            running = true;
            Thread t = new Thread(this);
            t.start();
        }
        log.info("*************************Receiver Startup Successful*************************");
    }

    public void stop(){
        if (!running){
            log.warn("Stop called, Receiver not running");
        }
        running = false;
        server.stop();
        try {
            handler.close();
        } catch (IOException ex){
            log.error("Error closing journal stream",ex);
        }
        log.info("*************************Receiver Shutdown completed successfully*************************");
    }
    
    public boolean isRunning(){
        return running;
    }
    
    public final int getPort(){
        return port;
    }
/*
     public void registerApplication(MessageTypeRouter rout, String messageType, String eventType, Application handler){
     rout.registerApplication(messageType, eventType, handler);
     if (!acceptedEvents.contains(eventType)){
     acceptedEvents.add(eventType);
     }
     }
     */
}
