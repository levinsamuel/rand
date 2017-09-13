/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.util;

/**
 *
 * @author samuel.levin
 */
public class SenderException extends Exception {
    
    public SenderException(String message){
        super(message);
    }
    
    public SenderException(Throwable thr){
        super(thr);
    }
}
