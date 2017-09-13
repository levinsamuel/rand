/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sjl.mlbapp.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sjl.mlbapp.gui.MainPage;
import sjl.mlbapp.objects.PlayerName;

/**
 *
 * @author samlevin
 */
public class DataManager {
    
    Connection con;
    Logger log = LoggerFactory.getLogger(DataManager.class);
    
    
    public DataManager(){
        this.con = MainPage.con;
        
    }
    
    public DataManager(Connection con){
        this.con = con;
        
    }
    
    public HashMap getStatsByPlayerName(String first, String last) throws SQLException {
        try {
            HashMap ret = new HashMap<String,String>();
            Statement stmt = con.createStatement();
            
            String queryString = "select playerID from master where nameFirst = \"" + first + "\" and nameLast = \"" + last + "\"";
            
            System.out.println("Query String: " + queryString);
            ResultSet rs = stmt.executeQuery(queryString);
            rs.next();
            String playerID = rs.getString("playerID");
            rs = stmt.executeQuery("select pos from fielding where playerID = '" + playerID + "'");
            while (rs.next()){
                if (rs.getString("POS").equals("P")){
                    rs = stmt.executeQuery("select sum(W) wins, sum(IPouts) outs, sum(ER) earnedruns, sum(SO) ks " +
                            "from pitching where playerID = '" + playerID + "'");
                    rs.next();
                    ret.put("Position","Pitcher");
                    ret.put("Wins",Integer.toString(rs.getInt("wins")));
                    int outs = rs.getInt("outs");
                    int er = rs.getInt("earnedruns");
                    String IP = Integer.toString(outs/3) + "." + Integer.toString(outs%3);
                    String ERA = String.format("%04.2f" , (double)er*27/(double)outs );
                    ret.put("IP",IP);
                    ret.put("ERA",ERA);
                    ret.put("Strikeouts",Integer.toString(rs.getInt("ks")));
                    
                    return ret;
                }
            }
            
            rs = stmt.executeQuery("select sum(AB) abs, sum(H) hits, sum(HR) homers, sum(RBI) ribbies from batting where playerID = '" + playerID + "'");
            rs.next();
            ret.put("Position","Hitter");
            int hits = rs.getInt("hits");
            int abs = rs.getInt("abs");
            ret.put("At Bats",Integer.toString(abs));
            String BA = String.format("%.3f" , (double)hits/(double)abs );
            ret.put("Batting Average",BA);
            ret.put("Home Runs", Integer.toString(rs.getInt("homers")));
            ret.put("Runs Batted In", Integer.toString(rs.getInt("ribbies")));
            
            return ret;
            
            
        } catch (SQLException ex) {
            log.error(null, ex);
            throw ex;
        }
    }
    
    
    public ArrayList<PlayerName> findPlayers(String name) {
        
        String[] nameTokens = name.split("\\s");
        String queryString = null;
        
        if (nameTokens == null || nameTokens.length < 1){
            return null;
        } else if (nameTokens.length == 1){
            queryString = String.format("select nameFirst, nameLast, birthYear, playerID from master where nameFirst = '%1$s' or nameLast = '%1$s'", nameTokens[0]);
        } else if (nameTokens.length >= 2){
            queryString = String.format("select nameFirst, nameLast, birthYear, playerID from master where (nameFirst = '%1$s' and nameLast = '%2$s')"
                    + " or (nameFirst = '%2$s' and nameLast = '%1$s')", nameTokens[0], nameTokens[1]);
        }
        
        try {
            Statement stmt = con.createStatement();
            stmt.setMaxRows(0);
            stmt.executeQuery(queryString);
            ResultSet rs;
            
            ArrayList<PlayerName> ret = new ArrayList<PlayerName>();
            do {
                rs = stmt.getResultSet();
                
                while (rs.next()){
                    PlayerName pn = new PlayerName();
                    pn.id = rs.getString("playerId");
                    pn.nameFirst = rs.getString("nameFirst");
                    pn.nameLast = rs.getString("nameLast");
                    pn.birthYear = rs.getInt("birthYear");
                    ret.add(pn);
                }
            
            } while (stmt.getMoreResults());
        
            return ret;
                    
        } catch (SQLException ex){
            log.error( "SQL error", ex);
            return null;
        }
        
        
        
    }
}
