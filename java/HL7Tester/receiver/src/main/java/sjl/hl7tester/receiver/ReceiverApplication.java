/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.receiver;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;

/**
 *
 * @author samuel.levin
 */
public class ReceiverApplication implements Application {

    
    private static final Logger log = LoggerFactory.getLogger(ReceiverApplication.class);
    private boolean journal;

    public ReceiverApplication() throws ReceiverException {
        Properties props = HL7TesterProperties.getReceiverProperties();
        try {
            journal = Boolean.parseBoolean(props.getProperty("journal.received.messages", "false"));
            if (journal) {
                
                log.info("Journaling Enabled");
                JournalHandler.init();
                
            } else {
                log.info("Journaling disabled");
            }
        } catch (IOException ex) {
            log.error("Failed to load file", ex);
            throw new ReceiverException(ex);
        }
    }

    @Override
    public Message processMessage(Message msg) throws ApplicationException, HL7Exception {
        String encodedMessage = new DefaultHapiContext().getPipeParser().encode(msg);
        log.debug("Accepted Message: {}; thread id = {}", encodedMessage, Thread.currentThread().getId());
        
        try {
            if (journal){
                JournalHandler.journal(encodedMessage);
            }
            
            return msg.generateACK();
        } catch (IOException ex) {
            throw new HL7Exception(ex);
        } catch (ReceiverException ex) {
            throw new ApplicationException(ex);
        }
    }

    @Override
    public boolean canProcess(Message msg) {
        return true;
    }
    
    public void close() throws IOException{
        JournalHandler.close();
    }
}
