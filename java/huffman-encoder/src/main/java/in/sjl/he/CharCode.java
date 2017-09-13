/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

/**
 *
 * @author samuel.levin
 */
public class CharCode {
    
    public final char ch;
    public final int code;
    public final int length;
    
    /**
     * Constructor containing three necessary arguments for CharCode.
     * @param ch character being encoded
     * @param code byte value of bit string character code
     * @param length length of bit string; may not be a full 8 bits.
     */
    public CharCode(char ch, int code, int length){
        this.ch = ch;
        this.code = code;
        this.length = length;
    }
    
    @Override
    public String toString(){
        
        StringBuilder ret = new StringBuilder("");
        
        int bit = 1;
        for (int i = 0; i < this.length; i++){
            
            if ((bit & code) != 0){
                ret.insert(0, '1');
            } else {
                ret.insert(0, '0');
            }
            bit = bit << 1;
        }
        
        ret.append(":").append(ch);
        
        return ret.toString();
    }
    
}
