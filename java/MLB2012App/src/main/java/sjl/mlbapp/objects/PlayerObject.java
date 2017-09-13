/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.objects;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author samlevin
 */
public class PlayerObject {
    
    private Map mFields;
    protected String id;
    
    private PlayerObject(){
        
    }
    
    private PlayerObject(String id){
        this.populateHash(id);
    }
    
    private PlayerObject(String first, String last){
        mFields = new HashMap<String,String>();
        mFields.put("nameFirst", first);
        mFields.put("nameLast", last);
    }
    
    public String getId(){
        return id;
    }
    
    private void populateHash(String id){
        mFields = new HashMap();
        
        
        
    }
    
}
