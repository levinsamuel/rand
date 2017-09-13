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
public class TreeNode implements Comparable<TreeNode>{
    
    private TreeNode lower;
    private TreeNode higher;
    private Character c = null;
    private long freq;
    
    /**
     * Constructs node with no char and no links to other nodes.
     */
    public TreeNode(){
        
    }
    
    /**
     * Constructs TreeNode with links to lower and higher nodes
     * @param lower
     * @param higher 
     */
    public TreeNode(TreeNode lower, TreeNode higher){
        this.lower = lower;
        this.higher = higher;
    }
    
    /**
     * Constructs TreeNode with links to lower and higher nodes, and char
     * represented by this node's path from the root;
     * @param lower
     * @param higher
     * @param c 
     */
    public TreeNode(TreeNode lower, TreeNode higher, char c){
        this.lower = lower;
        this.higher = higher;
        this.c = c;
    }
    
    /**
     * Constructs TreeNode with links to lower and higher node, char to represent
     * path, and frequency of that char in input stream (only necessary when creating
     * encoding, not when reading encoding
     * @param lower
     * @param higher
     * @param c
     * @param freq 
     */
    public TreeNode(TreeNode lower, TreeNode higher, Character c, long freq){
        this.lower = lower;
        this.higher = higher;
        this.c = c;
        this.freq = freq;
    }
    
    public TreeNode getLower(){
        return lower;
    }
    
    public TreeNode getHigher(){
        return higher;
    }
    
    public void setLower(TreeNode lower){
        this.lower = lower;
    }
    
    public void setHigher(TreeNode higher){
        this.higher = higher;
    }
    
    public Character getChar(){
        return c;
    }
    
    public void setChar(Character c){
        this.c = c;
    }
    
    public boolean isCharNull(){
        return c == null;
    }
    
    public long getFreq(){
        return freq;
    }
    
    public void setFreq(long freq){
        this.freq = freq;
    }

    public int compareTo(TreeNode o) {
        
        if (this == o){
            return 0;
        }
        if (this.freq > o.getFreq()){
            return 1;
        } else if (this.freq < o.getFreq()){
            return -1;
        } else {
            if (c == null){
                return -1;
            } else if (o.isCharNull()){
                return 1;
            } else {
                if (c > o.getChar()){
                    return 1;
                } else if (c < o.getChar()) {
                    return -1;
                } else {
                    
                    return 1;//don't want arbitrarily equality
                }
                
            }
        }
    }
    
    
    public String toString(){
        return "Char: " + Character.valueOf(c) + "; Frequency: " + freq;
    }
}
