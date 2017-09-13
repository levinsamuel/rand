/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.message;

import sjl.hl7tester.sender.util.SenderException;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.XAD;
import ca.uhn.hl7v2.model.v231.message.ADT_A04;
import ca.uhn.hl7v2.model.v231.segment.EVN;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;

/**
 *
 * @author samuel.levin
 */
public class MessageGenerator {

    private static final Logger log = LoggerFactory.getLogger(MessageGenerator.class);
    private final Object idSync;
    private final Object countSync;
    private Properties props;
    private int numfn;
    private int numln;
    private int numad;
    private long maxIdNum;
    private ArrayList<String> firstNames;
    private ArrayList<String> lastNames;
    private ArrayList<String> addresses;
    private String sendingApp, sendingFac, receivingApp, receivingFac;
    private String locale;
    private ArrayList<String> systems;
    private String sysString;
    private long baseIdNum;
    private HashMap<String, Long> idCounts;
    private static ArrayList<String> nonUniqueIdList;
    private static MessageGenerator messageGen = null;
    private static ArrayList<Message> messagesSent;
    private static long messageCount;
    private static int messagesToKeep;

    private MessageGenerator() {
        idSync = new Object();
        countSync = new Object();
        messagesSent = new ArrayList<Message>(10001);
    }

    public synchronized static MessageGenerator getInstance() {
        if (messageGen == null) {
            messageGen = new MessageGenerator();
            messageGen.init();
        }

        return messageGen;
    }
    
    public synchronized static void reInitialize(){
        if (messageGen == null) {
            messageGen = new MessageGenerator();
        }
        messageGen.init();
    }

    /**
     * Populates stored patient names to array. Reads in system codes
     */
    private void init() {
        try {
            messageCount = 0;

            props = HL7TesterProperties.getSenderProperties();

            firstNames = new ArrayList<String>();
            populateArray(firstNames, new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("fnames.txt"))));

            lastNames = new ArrayList<String>();
            populateArray(lastNames, new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("lnames.txt"))));

            addresses = new ArrayList<String>();
            populateArray(addresses, new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("addresses.txt"))));

            numfn = firstNames.size();
            numln = lastNames.size();
            numad = addresses.size();
            log.debug("Number of first names imported: {}", numfn);
            log.debug("Number of last names imported: {}", numln);
            log.debug("Number of addresses imported: {}", numad);

            /**
             * Data Generation Properties
             */
            sysString = props.getProperty("system.codes", "SYS_A,SYS_B");
            systems = new ArrayList<String>(Arrays.asList(sysString.split(",")));

            log.debug("Imported following system codes [{}]", sysString);

            sendingApp = props.getProperty("sending.application", "NEXTGATE");
            sendingFac = props.getProperty("sending.facility", "NEXTGATE");
            receivingApp = props.getProperty("receiving.application", "MATCHMETRIX");
            receivingFac = props.getProperty("receiving.facility", "MATCHMETRIX");

            locale = props.getProperty("hl7.locale");

            baseIdNum = Long.parseLong(props.getProperty("local.id.start.value", "1"));
            maxIdNum = Long.parseLong(props.getProperty("local.id.max.value", "-1"));
            if (baseIdNum < 0) {
                throw new SenderException("Starting ID value must not be negative");
            } else if (maxIdNum < baseIdNum && maxIdNum >= 0) {
                throw new SenderException("Max ID cannot be less than starting ID. For no max ID, unset property or set to -1");
            }

            idCounts = new HashMap<String, Long>();

            for (String system : systems) {
                idCounts.put(system, baseIdNum);
            }
            nonUniqueIdList = null;
            if (locale.equals("intl")) {
                String idList = props.getProperty("non.unique.identifiers");
                if (idList != null && !idList.isEmpty()) {
                    nonUniqueIdList = new ArrayList<String>(Arrays.asList(idList.split(",")));
                }
            }
            
            messagesToKeep = Integer.parseInt(props.getProperty("messages.to.keep","500"));
            if (messagesToKeep > 10000){
                log.warn("Cannot exceed message limit of 10,000. Setting messages to keep limit to 10,000");
                messagesToKeep = 10000;
            } else if (messagesToKeep < 10){
                log.warn("Messages to keep set to lower than minimum of 10. Setting messages to keep limit to 10");
                messagesToKeep = 10;
            } else {
                log.info("Messages to keep set to {}", messagesToKeep);
            }
            
            //Remove messages sent from previous runs.
            messagesSent.clear();

            //log.debug("Printing test message\n{}", generateAdtAdd().encode());
        } catch (IOException ex) {
            log.error("IOException", ex);
        } catch (SenderException ex) {
            log.error("SenderException", ex);
        }
    }

    /**
     * Generates random A04 with new patient ID
     * 
     * @param idCounts for storing max id used
     * @return A04 message with unused patient ID and randomly generated demographics
     * @throws HL7Exception
     * @throws IOException 
     */
    public Message generateAdtAdd(Map<String, Long> idCounts) throws HL7Exception, IOException {

        Random r = new Random();

        long id;
        boolean moreIds;
        String sysCode;
        synchronized (idSync) {
            do {
                moreIds = true;
                if (idCounts.isEmpty()) {
                    return null;
                }
                ArrayList<String> availableSystems = new ArrayList<String>(idCounts.keySet());
                sysCode = availableSystems.get(r.nextInt(availableSystems.size()));
                id = idCounts.put(sysCode, idCounts.get(sysCode) + 1);
                if (id > maxIdNum && maxIdNum != -1) {
                    moreIds = false;
                    idCounts.remove(sysCode);
                    
                }

            } while (!moreIds);
        }

        ADT_A04 ret = new ADT_A04();
        ret.initQuickstart("ADT", "A04", null);

        //long time = System.currentTimeMillis();
        //Date currentDate = new Date(time);

        String SSN = null;
        if (locale.equals("us")) {
            SSN = Integer.toString(r.nextInt(799999999) + 200000000);
        }

        MSH head = ret.getMSH();
        head.getFieldSeparator().setValue("|");
        head.getSendingApplication().getNamespaceID().setValue(sendingApp);
        head.getSendingFacility().getNamespaceID().setValue(sendingFac);
        head.getReceivingApplication().getNamespaceID().setValue(receivingApp);
        head.getReceivingFacility().getNamespaceID().setValue(receivingFac);
        head.getProcessingID().getProcessingID().setValue("P");
        // String header = "MSH|^~\\&|" + mshStart + currentDate + "||ADT^A04|" + sysCode +  + "|P|2.3.1|503\r";

        EVN evn = ret.getEVN();
        evn.getEventTypeCode().setValue("A04");
        //evn.getRecordedDateTime().getTimeOfAnEvent().setValue(currentDate);
        String user = System.getProperties().getProperty("user.name");
        evn.insertOperatorID(0).getIDNumber().setValue(user);
        //String evn = "EVN|A04|" + currentDate + "|||" + user + "\r";

        PID pid = ret.getPID();
        pid.getSetIDPID().setValue("1");
        //String pid = "PID|1||"; //Line prefix
        pid.insertPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(sysCode);
        pid.getPatientIdentifierList(0).getID().setValue(Long.toString(id));
        if (nonUniqueIdList != null) {
            int i = 1;
            for (String idType : nonUniqueIdList) {
                pid.insertPatientIdentifierList(i).getAssigningAuthority().getNamespaceID().setValue(idType);
                String idValue = SSN = Integer.toString(r.nextInt(79999999) + 20000000);
                pid.getPatientIdentifierList(i).getID().setValue(idValue);
                i++;
            }
        }
        pid.insertPatientName(0).getFamilyLastName().getFamilyName().setValue(lastNames.get(r.nextInt(numln)));
        pid.getPatientName(0).getGivenName().setValue(firstNames.get(r.nextInt(numfn)));

        Calendar c = Calendar.getInstance();
        c.set(r.nextInt(112) + 1900, r.nextInt(12) + 1, r.nextInt(28) + 1);
        pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(c.getTime());

        pid.getSex().setValue(r.nextFloat() > 0.5f ? "M" : "F");

        String address = addresses.get(r.nextInt(numad));
        String[] adParts = address.split("\\^");
        XAD ad = pid.insertPatientAddress(0);
        ad.getStreetAddress().setValue(adParts[0]);
        ad.getCity().setValue(adParts[2]);
        ad.getStateOrProvince().setValue(adParts[3]);
        ad.getZipOrPostalCode().setValue(adParts[4]);
        ad.getCountry().setValue(adParts[5]);
        ad.getAddressType().setValue(adParts[6]);

        pid.insertPhoneNumberHome(0).getXtn1_9999999X99999CAnyText().setValue(String.format("(%03d)%03d-%04d", r.nextInt(800) + 200, r.nextInt(900) + 100, r.nextInt(10000)));

        if (SSN != null) {
            pid.getSSNNumberPatient().setValue(SSN);
        }

        PV1 pv1 = ret.getPV1();
        pv1.getPatientClass().setValue("E");
        pv1.getVIPIndicator().setValue("N");
        //String pv1 = "PV1||E||||||||||||||Y\r\n";

        if (messagesSent.size() >= messagesToKeep) {
            log.debug("Messages Sent array too large. Deleted saved messages array and starting over");
            messagesSent.clear();
        }
        messagesSent.add(ret);

        log.debug("Created message: {}", ret.encode().replaceAll("\\r", "\\n"));
        synchronized (countSync){
            messageCount++;
        }
        return ret;
    }

    /**
     * Generates an update message based on a randomly selected previous add.
     * 
     * @param idCounts The max id for each system code, to enforce a limit
     * @return Update message that is identical to a previous messages except for a name change
     * @throws HL7Exception
     * @throws IOException 
     */
    public Message generateAdtUpdate(Map<String, Long> idCounts) throws HL7Exception, IOException {
        Random r = new Random();
        if (messagesSent.isEmpty()) {
            return generateAdtAdd(idCounts);
        }
        ADT_A04 ret = (ADT_A04) messagesSent.get(r.nextInt(messagesSent.size()));
        ret.initQuickstart("ADT", "A08", null);

        PID pid = ret.getPID();
        pid.getPatientName(0).getGivenName().setValue(firstNames.get(r.nextInt(numfn)));

        synchronized (countSync){
            messageCount++;
        }
        return ret;
    }

    /**
     * Generates new A04 with MessageGenerator's internal id counts
     * 
     * @return
     * @throws HL7Exception
     * @throws IOException 
     */
    public Message generateAdtAdd() throws HL7Exception, IOException {
        return generateAdtAdd(idCounts);
    }
    
    /**
     * Generates new A08 with MessageGenerator's internal id counts
     * 
     * @return
     * @throws HL7Exception
     * @throws IOException 
     */
    public Message generateAdtUpdate() throws HL7Exception, IOException {
        return generateAdtUpdate(idCounts);
    }
    
    /**
     * Sets generator's locale to parameter value
     * @param locale 
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * 
     * @return Starting ID for all messages
     */
    public long getBaseIdNum() {
        return baseIdNum;
    }

    public long getMaxIdNum() {
        return maxIdNum;
    }
    
    public static long getMessageCount(){
        return messageCount;
    }
    
    public synchronized static void resetMessageCount(){
        messageCount = 0;
    }

    public final List<String> getSystems() {
        ArrayList<String> sys = new ArrayList<String>(systems);
        return sys;
    }

    private void populateArray(List l, BufferedReader in) throws IOException {
        String line = in.readLine();
        while (line != null) {
            l.add(line);
            line = in.readLine();
        }
    }
}
