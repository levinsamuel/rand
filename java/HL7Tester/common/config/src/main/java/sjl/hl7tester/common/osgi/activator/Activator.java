/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sjl.hl7tester.common.osgi.activator;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/**
 *
 * @author samuel.levin
 */
public class Activator implements BundleActivator {
    
    private static final Logger mLogger = Logger.getLogger(Activator.class.getName());
    
    private InitialContext ctx;

    public Activator() throws NamingException{
        mLogger.info("Constructing Configuration Activator");
    }
    
    public void start(BundleContext bc) throws Exception {
        mLogger.info("Starting config bundle.");
    }

    public void stop(BundleContext bc) throws Exception {
        mLogger.info("Stopping config bundle.");
    }
    
}
