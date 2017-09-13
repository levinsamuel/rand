/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.send;

import sjl.hl7tester.sender.message.MessageGenerator;
import sjl.hl7tester.sender.util.SenderException;
import sjl.hl7tester.sender.util.TCPAddress;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.HL7Reader;
import ca.uhn.hl7v2.llp.HL7Writer;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;

/**
 *
 * @author samuel.levin
 */
public class MessageSender extends Thread {

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private HL7Writer writer;
    private HL7Reader reader;
    private final TCPAddress address;
    private Socket socket;
    private boolean running;
    private int minWait;
    private int maxWait;
    private int timeout;
    private String timeoutAction;
    private int timeoutLimit;
    private boolean A08Enabled;
    
    private final Router router;

    public MessageSender(Router router, TCPAddress address) {
        this.address = address;
        this.router = router;
    }
    
    public MessageSender(Router router, TCPAddress address, String name) {
        super(name);
        this.address = address;
        this.router = router;
    }

    public void init() throws IOException, LLPException {
        
        Properties props = HL7TesterProperties.getSenderProperties();

        log.debug("Initializing Message Sender.");
        minWait = Integer.parseInt(props.getProperty("min.wait.time"));
        maxWait = Integer.parseInt(props.getProperty("max.wait.time"));

        if (minWait > maxWait) {
            log.warn("Minimum wait time set higher than max time. Switching min and max");
            int temp = minWait;
            minWait = maxWait;
            maxWait = temp;
        }

        timeout = Integer.parseInt(props.getProperty("sender.timeout", "10000"));
        log.debug("Timeout set to {} seconds", timeout / 1000.0);

        timeoutAction = props.getProperty("sender.timeout.action", "skip");
        log.debug("Timeout action set to {}", timeoutAction);
        
        timeoutLimit = Integer.parseInt(props.getProperty("sender.timeout.tries.limit", "5"));
        if (timeoutAction.equals("skip")){
            log.debug("Number of timeouts allowed set to {}", timeoutLimit);
        }

        this.socket = new Socket(address.getHostName(), address.getPort());
        this.socket.setSoTimeout(timeout);
        MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
        this.writer = mllp.getWriter(socket.getOutputStream());
        this.reader = mllp.getReader(socket.getInputStream());
        
        String messageTypes = props.getProperty("message.type");
        if (messageTypes.contains("A08")){
            A08Enabled = true;
            log.debug("A08 messages enabled");
        } else {
            A08Enabled = false;
            log.debug("A08 messages disabled");
        }
    }

    @Override
    public void run() {

        log.debug("Sending to {}:{}", socket.getInetAddress().getHostName(), socket.getPort());
        running = true;
        int timeouts = 0;

        String response;
        while (router.running && running) {
            try {
                int waitTime = minWait;
                if (minWait != maxWait) {
                    double rand = Math.random();
                    waitTime = (int) ((rand * (maxWait - minWait)) + minWait);
                    if (waitTime > maxWait || waitTime < minWait) {
                        throw new SenderException("Sleep time invalid " + waitTime);
                    }
                }
                log.debug("Waiting for {} milliseconds", waitTime);

                Thread.sleep(waitTime);
                MessageGenerator mg = MessageGenerator.getInstance();
                
                String message;
                Message msg = null;
                if (!A08Enabled){
                    msg = mg.generateAdtAdd(router.getIdCounts());
                    if (msg == null){
                        log.info("Sender stopped because max ids reached");
                        stopSender();
                        break;
                    } else {
                        message = msg.encode();
                    }
                } else {
                    double rand = Math.random();
                    if (rand < 0.5f){
                        log.debug("Generated ADT add");
                        msg = mg.generateAdtAdd(router.getIdCounts());
                    }
                    if (msg == null){
                        log.debug("Generated ADT update");
                        msg = mg.generateAdtUpdate(router.getIdCounts());
                    }
                    message = msg.encode();
                }

                log.trace("Sending message");
                writer.writeMessage(message);

                response = reader.getMessage();

                if (response == null) {
                    log.debug("null response");
                    if (timeoutAction.equals("error") || (timeouts == timeoutLimit && timeoutLimit != 0)) {
                        log.error("Timeout action set to error or max timeouts reached");
                        throw new IOException("No response received from socket " + socket.getInetAddress().getHostName()
                                + ":" + socket.getPort());
                    } else if (timeoutAction.equals("skip")) {
                        log.warn("Sender timout. Skipping message");
                        timeouts++;
                    }
                } else {
                    log.debug("Response received from {}:{}: {}", new Object[]{socket.getInetAddress().getHostName(), socket.getPort(), response.replaceAll("\r", "\n")});
                }


            } catch (IOException ex) {
                log.error("IOException during message processing", ex);
                stopRouter();
                log.debug("Stopping due to exception");
            } catch (LLPException ex) {
                log.error("IOException during message processing", ex);
                stopRouter();
                log.debug("Stopping due to exception");
            } catch (HL7Exception ex) {
                log.error("IOException during message processing", ex);
                stopRouter();
                log.debug("Stopping due to exception");
            } catch (InterruptedException ex) {
                log.error("IOException during message processing", ex);
                stopRouter();
                log.debug("Stopping due to exception");
            } catch (SenderException ex) {
                log.error("IOException during message processing", ex);
                stopRouter();
                log.debug("Stopping due to exception");
            }
            log.trace("Finished Sender loop");
        }
        try {
            socket.close();
            log.trace("Exiting MessageSender");
        } catch (IOException ex) {
            log.error("Error trying to close socket", ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isRunning() {
        return running;
    }
    /*
     public void start() {
     log.info("*****Starting message sender*******");
     this.init();
     if (startupSuccessful) {
     running = true;
     Thread thd = new Thread(this);
     thd.start();
     } else {
     log.error("MessageSender failed to initialize");
     }
     }
     */

    public final void stopSender() {
        log.info("********************Stopping Sender Thread {}**************************", Thread.currentThread().getName());
        running = false;
    }
    
     public final void stopRouter() {
        running = false;
        router.stopAndCheck();
        log.info("********************Stopping Sender Thread {}**************************", Thread.currentThread().getName());
    }
}
