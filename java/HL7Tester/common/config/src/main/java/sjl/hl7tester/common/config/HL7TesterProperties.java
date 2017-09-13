/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sjl.hl7tester.common.config;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author samuel.levin
 */
public abstract class HL7TesterProperties {
    
    private static final Logger log = LoggerFactory.getLogger(HL7TesterProperties.class);
    
    private static Properties props;
    
    protected HL7TesterProperties(){
    }
    
    public static Properties getSenderProperties(){
        return getProperties("sender.properties");
    }
    
    public static Properties getReceiverProperties(){
        return getProperties("receiver.properties");
    }
    
    private static Properties getProperties(String propFileName){
        
        String sep = File.separator;
        
        String configFilePath = String.format("etc%1$sconfigp%1$s%2$s", sep, propFileName);
            
        props = new Properties();
        File configFile = new File(configFilePath);
        if (!configFile.exists()){
            //try to find in the source directory if running from netbeans
            configFile = new File(String.format("src%1$smain%1$sconfig%1$s%2$s", sep, propFileName));
        }
        try {
            props.load(new FileReader(configFile));
        } catch (FileNotFoundException ex) {
            log.error("File Not Found " + configFile.getAbsolutePath(), ex);
        } catch (IOException ex) {
            log.error("IOException",ex);
        }
        
        log.debug("Reading from config file {}", configFile.getAbsolutePath());
        
        return props;
    }
    
    public static Properties getPropertiesFromPath(String path){
        String configFilePath = path;
            
        props = new Properties();
        try {
            props.load(new FileReader(new File(configFilePath)));
        } catch (FileNotFoundException ex) {
            log.error("File Not Found " + configFilePath,ex);
        } catch (IOException ex) {
            log.error("IOException",ex);
        }
        
        log.info("Reading from config file {}", configFilePath);
        
        return props;
    }
    
}
