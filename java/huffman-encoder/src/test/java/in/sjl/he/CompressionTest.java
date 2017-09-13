/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.SortedSet;
import org.junit.Test;
import org.junit.Assert;



/**
 *
 * @author samuel.levin
 */
public class CompressionTest {
    
    private final TreeNode tn;
    private final SortedSet<TreeNode> ss;
    private final StreamEncoder se;
    private final CharCode[] encoding;
    
    private static final String SAMPLE_IN_FILE = "src" + File.separator + "test" + File.separator + "sample" + File.separator + "sample.txt";
    private static final String ENCODED_OUTPUT = "target" + File.separator + "test-encoding" + File.separator + "sample-encoding.hen";
            
    public CompressionTest() throws FileNotFoundException, IOException, Exception {
        
        se = new StreamEncoder(new File(SAMPLE_IN_FILE));
        CompressionWriter cw = new CompressionWriter();
        
        ss = se.constructCountSortedSet();
        tn = ListCompiler.compileTreeNodeSet(ss);
        encoding = CharacterEncoder.createCharacterEncoding(tn);
        
        //cw.writeCompression(null, ir);
    }
    
    @Test
    public void checkForNulls() throws Exception {
        
        Assert.assertNotEquals(ss.size(), 0);
                
        Assert.assertNotNull(tn);
        
        //printTreeNode(tn);
        
    }
    
    @Test
    public void encodeToFile() throws Exception{
        
        File outFile = new File(ENCODED_OUTPUT);
        
        if (!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            BitSet bs = se.encodeStream(encoding, out);
            //printBitSet(bs);
        } finally {
            if (out != null){
                out.close();
            }
        }
    }
    
    //@Test
    public void printEncoding(){
        int i = 0;
        for (CharCode encoding1 : encoding) {
            if (encoding1 != null){
                
                System.out.println("Char: " + (char)i + "; BitString: " + encoding1);
            }
            i++;
        }
    }
    
    @Test
    public void decodeToSdtOut() throws FileNotFoundException, Exception {
        
        File encodedFile = new File(ENCODED_OUTPUT);
        
        Assert.assertTrue(encodedFile.exists());
        Assert.assertTrue(encodedFile.canRead());
        
        InputStream in = null;
        
        try {
            in = new FileInputStream(encodedFile);
        
            se.decodeStream(System.out, in);
            System.out.println("\n");
            //printBitSet(bs);
        } finally {
            if (in != null){
                in.close();
            }
        }
    }
    
    private void printTreeNode(TreeNode tn){
        if (tn.getHigher() != null){
            printTreeNode(tn.getHigher());
        }
        if (!tn.isCharNull()){
            System.out.println(tn);
        }
        if (tn.getLower() != null){
            printTreeNode(tn.getLower());
        }
    }
    
    private void printBitSet(BitSet bs){
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < bs.length(); i++){
            if (bs.get(i)){
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        
        System.out.println(sb.toString());
    }
}
