/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.objects;

/**
 *
 * @author samlevin
 */
public class PlayerName {
    
    public String nameFirst;
    public String nameLast;
    public String id;
    public int birthYear;
    
    @Override
    public String toString(){
        return nameFirst + " " + nameLast + " (Birth Year: " + birthYear + ")";
    }
    
}
