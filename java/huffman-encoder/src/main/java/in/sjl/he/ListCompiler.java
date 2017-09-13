/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package in.sjl.he;

import java.util.SortedSet;

/**
 *
 * @author samuel.levin
 */
public class ListCompiler {
    
    public ListCompiler(){
        
    }
    
    public static TreeNode compileTreeNodeSet(SortedSet<TreeNode> treeList) throws Exception {
        
        while (treeList.size() > 1){
            
            TreeNode first = treeList.first();
            treeList.remove(first);
            TreeNode second = treeList.first();
            treeList.remove(second);
            
            TreeNode combo = new TreeNode(first, second, null, first.getFreq() + second.getFreq());
            
            treeList.add(combo);
            
        }
        
        
        return treeList.first();
    }
    
}
