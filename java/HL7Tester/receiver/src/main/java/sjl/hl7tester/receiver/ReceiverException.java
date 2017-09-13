/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sjl.hl7tester.receiver;

/**
 *
 * @author samuel.levin
 */
public class ReceiverException extends Exception {
    
    public ReceiverException(String message){
        super(message);
    }
    
    public ReceiverException(Throwable tbl){
        super(tbl);
    }
}
