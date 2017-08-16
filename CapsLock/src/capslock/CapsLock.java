package capslock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author RISCassembler
 */
public class CapsLock {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader reader;
        
        try {
            reader = new BufferedReader(new FileReader("GamesInfo.json"));
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            return;
        }
        
        String jsonString;
        
        try {
            jsonString = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }
        
        System.out.println(jsonString);
         
        if(jsonString == null)return; 
        
        List<GameCertification> testlist = new ArrayList();
        
        try{
            new JSONArray(jsonString).forEach(record -> testlist.add(new GameCertification((JSONObject) record)));
        }catch(Exception e){
            return;
        }
        testlist.forEach(ele -> ((GameCertification)ele).dump());
    }
    
}
