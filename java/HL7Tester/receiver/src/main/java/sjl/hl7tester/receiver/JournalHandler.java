/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sjl.hl7tester.receiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.hl7tester.common.config.HL7TesterProperties;

/**
 *
 * @author samuel.levin
 */
public class JournalHandler {
    
    private static final Logger log = LoggerFactory.getLogger(JournalHandler.class);

    
    private static String journalDir;
    private static String archiveDir;
    private static File journalFile;
    private static BufferedWriter messageOut;
    private static long maxFileSize;
    
    private static final Map<String,Integer> sufMap;
    
    static {
        sufMap = new HashMap<String,Integer>();
        sufMap.put("b", 0);
        sufMap.put("k", 1);
        sufMap.put("m", 2);
        sufMap.put("g", 3);
    };
    
    private static boolean isInitialized = false;
    
    /**
     * Initializes the JournalHandler from properties
     * 
     * @throws IOException
     * @throws ReceiverException 
     */
    public static void init() throws IOException, ReceiverException {
        
        Properties props = HL7TesterProperties.getReceiverProperties();
        
        journalDir = props.getProperty("journal.basedir", "working-dir");
        journalDir = journalDir.replaceAll("[\\/]$", "");
        File f = new File(journalDir);
        if (!f.exists()){
            f.mkdirs();
        }
        archiveDir = journalDir + File.separator + "archive";
        log.debug("Archive dir set to {}", archiveDir);
        
        journalFile = new File(journalDir + File.separator + "received.txt");
        messageOut = new BufferedWriter(new FileWriter(journalFile));
        log.info("Creating journal file {}", journalFile.getAbsolutePath());
        
        String fileSize = props.getProperty("journal.max.size", "50m");
        Pattern p = Pattern.compile("(\\d+)\\s*([bBkKmMgG]?)");
        log.trace("Journal filesize string: {}", fileSize);
        log.trace("Matching journal.max.size value against following pattern: {}",p.pattern());
        Matcher m = p.matcher(fileSize);
        
        if (!m.matches()){
            throw new ReceiverException("Failed to parse journal.max.size property. Failed to set journal max size");
        } else if (m.groupCount() == 1){
            maxFileSize = Long.valueOf(m.group(1));
        } else if (m.groupCount() > 1){
            maxFileSize = Long.valueOf(m.group(1));
            String suf = m.group(2);
            if (suf != null && !suf.isEmpty()){
                maxFileSize = (long)(maxFileSize * Math.pow(1024, sufMap.get(suf.toLowerCase())) );
            }
        }
        log.trace("Journal file size set to {} bytes", maxFileSize);
        
        isInitialized = true;
    }
    
    /**
     * Archives journal file in archiveDir.
     * 
     * @param fileName name of current journal file
     * @return the absolute path to the archived file
     */
    public static String archive(){
        
        Calendar currentTime = Calendar.getInstance();
        
        DateFormat df = new SimpleDateFormat("HHmmss");
        String dateStamp = df.format(currentTime.getTime());
        log.trace("Current time stamp: {}", dateStamp);

        StringBuilder datedArchiveDir = new StringBuilder(archiveDir);
        datedArchiveDir.append(File.separator).append(currentTime.get(Calendar.YEAR))
                .append(File.separator).append(currentTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US))
                .append(File.separator).append(currentTime.get(Calendar.DAY_OF_MONTH));

        File archive = new File(datedArchiveDir.toString());
        if (!archive.exists()){
            archive.mkdirs();
            log.debug("Creating archive dir: {}", datedArchiveDir.toString());
        }

        String fileName = journalFile.getName();
        
        File archiveFile = new File( datedArchiveDir.append(File.separator) 
                .append(fileName.substring(0, fileName.lastIndexOf("."))).append("-")
                .append(dateStamp).append(fileName.substring(fileName.lastIndexOf("."), fileName.length()))
                .toString() );

        log.info("Archiving journal file to {}", archiveFile);
        journalFile.renameTo(archiveFile);
        
        isInitialized = false;
        return archiveFile.getAbsolutePath();
    }
    
    /**
     * Writes encodedMessage to journal file called "received.txt"
     * in defined journal dir.
     * 
     * @param encodedMessage Message to journal
     * @throws IOException
     * @throws ReceiverException 
     */
    public static void journal(String encodedMessage) throws IOException, ReceiverException {
        
        if (!isInitialized){
            init();
        }
        
        if (messageOut != null){
            messageOut.write(encodedMessage);
            messageOut.newLine();
            messageOut.flush();
            log.trace("Journaled message to {}", journalFile.getAbsolutePath());
        }
        
        if (journalFile.length() > maxFileSize){
            
            if (messageOut != null){
                log.info("Closing output filestream");
                messageOut.close();
            }
            
            archive();
        }
    }
    
    /**
     * Closes file writer and archives current journal file.
     * 
     * @throws IOException 
     */
    public static void close() throws IOException {
        if (messageOut != null){
            log.info("Closing output filestream");
            messageOut.close();
        }
        if (isInitialized){            
            archive();
        }
    }
    
}
