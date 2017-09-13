/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;

/**
 *
 * @author samuel.levin
 */
public class CompressionWriter {
    
    /**
     * No file to write compression stream.
     * Value = 2.
     */
    public static final int ERROR_NO_OUTPUT_FILE = 2;
    /**
     * No file from which to read text to be compressed.
     * Value = 3.
     */
    public static final int ERROR_NO_INPUT_FILE = 3;
    
    
    public CompressionWriter(){
        
    }
    
    private void writeCompression(File outFile, StreamEncoder ir) throws IOException, Exception {
        
        SortedSet<TreeNode> treeList = ir.constructCountSortedSet();
                    
        TreeNode frequencyNode = ListCompiler.compileTreeNodeSet(treeList);
        
        CharCode[] encoding = CharacterEncoder.createCharacterEncoding(frequencyNode);
                
        FileOutputStream out = new FileOutputStream(outFile);
        ir.encodeStream(encoding, out);
        
        ir.decodeStream(System.out, new FileInputStream(outFile));
        
    }
    
    /**
     * Returns 2 if there is no output file given
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception{
        
        if (args.length == 0){
            System.err.print("Output file required");
            
        } else {
        
            File outFile = new File(args[0]);

          //  if (outFile.exists()){
            //    System.err.print("Output file exists, will not overwrite");
                
            //} else {

                StreamEncoder ir = null;
                if (args.length == 1){ //Read from stdin
                    //ir = new StreamEncoder(System.in);
                    //Not yet supported
                    System.exit(ERROR_NO_INPUT_FILE);
                } else { //Read from file provided in 2nd arg
                    File inFile = new File(args[1]);

                    if (!inFile.exists() || !inFile.canRead()){
                        System.err.print("Input file provided does not exist or cannot be read");
                    }
                    ir = new StreamEncoder(inFile);
                    
                }
                
                CompressionWriter compWriter = new CompressionWriter();
                
                compWriter.writeCompression(outFile, ir);
                
                return;
            //}
        }
        
        System.exit(ERROR_NO_OUTPUT_FILE);
    }
    
}
