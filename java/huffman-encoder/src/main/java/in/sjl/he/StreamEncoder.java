/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author samuel.levin
 */
public class StreamEncoder {
    
    private final long[] counts;
    private final File inputFile;
    private int fileLength;
    
    
    private static final int BIT_SET_BUFFER_SIZE = 10000;
    private static final int OUT_STR_BUFFER_SIZE = 1000;

    public StreamEncoder(File input){
        
        this.counts = new long[256*256];
        this.inputFile = input;
        this.fileLength = 0;
        
    }
    
    public SortedSet<TreeNode> constructCountSortedSet() throws IOException {
        InputStream in = null; 
        try {
            in = new FileInputStream(inputFile);
            return constructCountSortedSet(in);
        } finally {
            if (in != null){
                in.close();
            }
        }
        
    }
    
    public SortedSet<TreeNode> constructCountSortedSet(InputStream input) throws IOException {
        
        InputStreamReader in = null;
        
        try {
            in = new InputStreamReader(input, Charset.forName("UTF-8"));
            
            while (in.ready()){
                int c = in.read();

                counts[c]++;
            }
            
            List<TreeNode> treeList = new LinkedList<TreeNode>();
            
            for (int i = 0; i < counts.length; i++){
                
                if (counts[i] != 0){
                    treeList.add(new TreeNode(null, null, (char)i, counts[i]) );
                }
            }
            
            return new TreeSet<TreeNode>(treeList);
            
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (in != null){
                
                in.close();
            }
        }
    }
    
    public LinkedList<TreeNode> constructCountLinkedList() throws IOException {
        InputStream in = null; 
        try {
            in = new FileInputStream(inputFile);
            return constructCountLinkedList(in);
        } finally {
            if (in != null){
                in.close();
            }
        }
    }
    
    public LinkedList<TreeNode> constructCountLinkedList(InputStream input) throws IOException {
        
        InputStreamReader in = null;
        try {
            
            in = new InputStreamReader(input, Charset.forName("UTF-8"));
            while (in.ready()){

                int c = in.read();
                counts[c]++;
            }
            
            Arrays.sort(counts);
            
            LinkedList<TreeNode> treeList = new LinkedList<TreeNode>();
            for (int i = 0; counts[i] > 0 && i < counts.length; i++){
                
                treeList.add(new TreeNode(null, null, (char)i, counts[i]) );
                
            }
            
            return treeList;
            
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (in != null){
                in.close();
            }
        }
    }
    
    public BitSet encodeStream(CharCode[] encoding, OutputStream output) throws IOException, Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(inputFile);
            return encodeStream(encoding, output, in);
        } finally {
            if (in != null){
                
                in.close();
            }
        }
        
    }
    
    public BitSet encodeStream(CharCode[] encoding, OutputStream out, InputStream input) throws IOException, Exception {
        
        InputStreamReader in = null;
        BitSet ret = null;
        
        writeCharEncoding(encoding, out);
        
        try {
            in = new InputStreamReader(input, Charset.forName("UTF-8"));
            //out.write(fileLength);
            
            BitSet bs = new BitSet(BIT_SET_BUFFER_SIZE);
            int index = 0;
            while (in.ready()){

                int c = in.read();
                
                CharCode code = encoding[c];
                
                if (code != null){
                    //System.out.println("Reading CharCode: "+code + "; total length is " + (length = length + code.length));
                    
                    int bit = 1 << (code.length - 1);
                    for (int i = 0 ; i < code.length; i++){
                        
                        if (index == BIT_SET_BUFFER_SIZE){
                            out.write(toByteArray(bs, index));
                            if (ret == null){
                                ret = bs;
                            }
                            bs = new BitSet(BIT_SET_BUFFER_SIZE);
                            index = 0;
                            
                        }
                        
                        if ((code.code & bit) != 0 ){
                            bs.set(index);
                        }
                        bit = bit >> 1;
                        //index now points to the next space, or the length,
                        //not the last place written.
                        index++;
                    }
                                        
                } else {
                    throw new Exception("No character code for found character: " + (char)c + ", character index: " + index);
                }
            }
            
            out.write(toByteArray(bs, index));
            
            //Write tail-byte to tell how many bits the last byte is.
            out.write((index+1)%8);
            
            if (ret == null){
                ret = bs.get(0, index);
            }
            return ret;
            
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (in != null){
                in.close();
            }
            //out.close();
        }

    }
    
    
    public void decodeStream(PrintStream out, InputStream in) throws IOException, Exception {
        
        BitSet ret = null;
        try {
                        
//            int length = 0;
//            length = in.read();
//            if (length == -1){
//                return;
//            }
            
            BitSet bs = new BitSet(BIT_SET_BUFFER_SIZE);
            int index = 0;
            
            LinkedList<Integer> buffer = new LinkedList<Integer>();
            buffer.add(in.read());
            buffer.add(in.read());
            if (buffer.peekLast().intValue() > 8){
                //Length of bit code is two byes
                //Read in next 2 bytes instead of one.
                buffer.add(in.read());
            }
            buffer.add(in.read());
            
            TreeNode decoder;
            CharCode[] encoding = new CharCode[256*256];
            
            //Look for null character to terminate encoding
            while (buffer.peekFirst() != 0 && buffer.peekLast() != -1){
                
                char c = (char)buffer.pollFirst().intValue();
                int length = buffer.pollFirst().intValue();
                int code = buffer.pollFirst().intValue();
                Integer extraByte = buffer.pollFirst();
                if (extraByte != null){
                    code = code*256 + extraByte.intValue();
                }
                
                encoding[c] = new CharCode(c, code, length);
                
                buffer.add(in.read());
                buffer.add(in.read());
                if (buffer.peekLast().intValue() > 8 && buffer.peekFirst() != 0){
                    //Length of bit code is two byes
                    //Read in next 2 bytes instead of one.
                    buffer.add(in.read());
                }
                buffer.add(in.read());
            }
            
            decoder = CharacterEncoder.createDecoderTree(encoding);
            
            int nullByte = buffer.pollFirst();
            assert nullByte == 0;
            
            buffer.add(in.read());

            while (buffer.peekFirst() != -1 && !buffer.isEmpty()){
                
                assert buffer.size() == 3;
                
                int currentByte = buffer.pollFirst();
                
                int byteLength = 8;
                //Check if EOF has been read.
                if (buffer.peekLast() == -1){
                    //If so, remove EOF char
                    buffer.pollLast();
                    //And take last byte to equal the number of bits to read from the last byte.
                    byteLength = buffer.pollLast();
                }
                for (int i = 1 << 7; i > 0 && byteLength > 0; i = i >> 1){
                    
                    if (index == BIT_SET_BUFFER_SIZE){
                        index = writeDecodedBitsToChars(decoder, out, bs, index);
                    }
                    
                    
                    if ((i & currentByte) != 0){
                        bs.set(index);
                    }
                    index++;
                    byteLength--;
                }
                buffer.add(in.read());
            }
            
            writeDecodedBitsToChars(decoder, out, bs.get(0, index), index);
            
            
        } catch (IOException ex) {
            throw ex;
        } finally {
            
        }

    }
    
    private int writeDecodedBitsToChars(TreeNode decoder, PrintStream out, BitSet bs, int length) throws Exception {
        
        BitSet written = (BitSet)bs.clone();
        StringBuilder sb = new StringBuilder(OUT_STR_BUFFER_SIZE);
        
        TreeNode it = decoder;
        
        int lastIndex = 0;
        
        for (int i = 0; i < length; i++){
            
            if (!it.isCharNull()){
                if (sb.length() == sb.capacity()){
                    out.print(sb.toString());
                    sb = new StringBuilder(OUT_STR_BUFFER_SIZE);
                }
                sb.append(it.getChar());
                it = decoder;
                lastIndex = i;
            }
            
            if (bs.get(i)){
                it = it.getHigher();
            } else {
                it = it.getLower();
            }

            if (it == null){
                throw new Exception("Unknown character code path at index " + i + ".\nPrinting current contents of string buffer: [" + sb.toString() + "]");
            }
            
        }
        
        //Print remaining StringBuilder
        out.print(sb.toString());
        //Copy remaining untransalted bits to beginning of new bitset
        int start = 0;
        if (lastIndex < length - 1){
            BitSet remainder = bs.get(lastIndex + 1, length);
            bs.clear();
            bs.or(remainder);
            start = length - (lastIndex + 1);
        } else {
            bs.clear();
        }
        
        return start;
        
    }
    
    /**
     * Writes char code pattern to output stream.
     * General pattern is repeating 3 bytes: char, byte code, length of byte code in bits.
     * Length is required because bit strings associated to char may have leading zeroes
     * and may not require an entire byte to encode.
     * Pattern is terminated with a null char '\u0000'
     * @param encoding Array of all character codes, as determined by frequency analysis
     * @param out stream to which to write 
     * @throws IOException if output stream throws IOException
     */
    private void writeCharEncoding(CharCode[] encoding, OutputStream out) throws IOException{
        
        for (int i = 0; i < encoding.length; i++){
            
            CharCode code = encoding[i];
            if (code != null){
                
                out.write((int)code.ch);
                out.write(code.length);
                //TODO: support for bitstrings longer than two bytes in size.
                if (code.length > 8){
                    //If bit string takes more than 8 bits to reprsent, write bit string
                    //as two bytes.
                    out.write(code.code / 256);
                }
                out.write(code.code);
            }
            
        }
        out.write(0);
    }
    
    private byte[] toByteArray(BitSet bs, int length){
        
        byte[] ret = new byte[(length+7)/8];
        
        int b = 0;
        for (int i = 0; i < length + 7; i++){
            
            b = b << 1;
            if (bs.get(i)){
                b++;
            }
            
            if (i % 8 == 7){
                ret[i/8] = (byte)b;
                b = 0;
            }
        }
        
        return ret;
    }
    
}
