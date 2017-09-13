/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.hl7tester.sender.util;

/**
 *
 * @author samuel.levin
 */
public class TCPAddress {

    private String host;
    private int port;

    private TCPAddress() {
    }

    public TCPAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHostName(){
        return host;
    }
    
    public int getPort(){
        return port;
    }
}
