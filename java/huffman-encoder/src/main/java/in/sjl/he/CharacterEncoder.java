/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author samuel.levin
 */
public class CharacterEncoder {
    
    private static final Logger log = Logger.getLogger(CharacterEncoder.class.getName());
    
    /**
     * Given encoding tree, return an array of CharCode
     * Array index is equivalent to encoded character's integer code
     * So, effectively, the array is a map between a character and its encoding.
     * @param tn Encoding tree constructed from applying Huffman algorithm to frequency analysis of input stream.
     * @return Array that maps a character's integer code to a CharCode object.
     */
    public static CharCode[] createCharacterEncoding (TreeNode tn){
        
        CharCode[] ret = new CharCode[256*256];
        
        assignBitEncoding(tn, ret, 0, 0);
        
        
        return ret;
    }
    
    /**
     * Given an array that maps a character value to a CharCode, returns a Tree
     * ideal for decoding an incoming bit string.
     * @param encoding Array that maps a character to its encoding
     * @return TreeNode representing the bit paths read from the encoding.
     * @throws java.lang.Exception
     */
    public static TreeNode createDecoderTree(CharCode[] encoding) throws Exception{
        
        TreeNode root = new TreeNode();
        
        for (int i = 0; i < encoding.length; i++){
            
            CharCode code = encoding[i];
            if (code != null){
                
                log.log(Level.FINE, "Creating decoder path for code: {0}", code);
                StringBuilder sb = new StringBuilder("");
                
                TreeNode it = root;
                for (int j = 1 << (code.length - 1); j > 0; j = j >> 1){
                    
                    if ((j & code.code) != 0){
                        
                        if (it.getHigher() == null){
                            
                            it.setHigher(new TreeNode());
                        }
                        
                        it = it.getHigher();
                        sb.append("1");
                        log.finest("One step higher");
                    } else {
                        
                        if (it.getLower() == null){
                            it.setLower(new TreeNode());
                        }
                        it = it.getLower();
                        log.finest("One step lower");
                        sb.append("0");
                    }
                }
                log.log(Level.FINE, "Added path for encoding: {0}", sb.toString());
                
                if (it.isCharNull()){
                    it.setChar(code.ch);
                } else {
                    throw new Exception("Already encoded a character for path reprented by " + code);
                }
            }
        }
        
        return root;
    }
    
    /**
     * Recursive algorithm for finding the bit path to each element
     * @param tn Complete tree of all characters with optimum bit path
     * @param charArray array of all characters and their associated codes to which to add codes
     * @param path stores the path traveled to each character
     * @param depth number of recursions 
     */
    private static void assignBitEncoding(TreeNode tn, CharCode[] charArray, int path, int depth){
        
        if (!tn.isCharNull()){
            
            charArray[tn.getChar().charValue()] = new CharCode(tn.getChar(), path, depth);
        } else {
            
            //path in a "string" of bits. new bits are added to the end, others are shifted left
            //1 left shift equivalent to adding a 0 in last bit.
            assignBitEncoding(tn.getLower(), charArray, path << 1, depth + 1);
            //l left shift + 1 equivalent to adding 1 in last bit.
            //E.g. (10110 << 1) + 1 = 101101 
            assignBitEncoding(tn.getHigher(), charArray, (path << 1) + 1, depth + 1);
        }
        
    }
    
    
}
