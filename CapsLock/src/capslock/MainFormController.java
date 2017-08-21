package capslock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class MainFormController implements Initializable {

    @FXML TilePane BaseTilePane;
    @FXML GameDisplayBoxController GameDisplayController;
    
    private boolean IsGameMapped = false;
    private final List<GameCertification> GameList;

    public MainFormController() {
        BufferedReader reader;
        
        try {
            reader = new BufferedReader(new FileReader("GamesInfo.json"));
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            GameList = null;
            return;
        }
        
        String jsonString;
        
        try {
            jsonString = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex);
            GameList = null;
            return;
        }
        
        System.out.println(jsonString);
         
        if(jsonString == null){
            GameList = null;
            return;
        }
        
        {
            List<GameCertification> ListBuilder = new ArrayList();
            try{
                new JSONArray(jsonString).forEach(record -> ListBuilder.add(new GameCertification((JSONObject) record)));
            }catch(JSONException e){
                GameList = null;
                return;
            }
            
            GameList = Collections.unmodifiableList(ListBuilder);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        System.out.println(GameDisplayController);
    }
    
    public void onLoad(WindowEvent event){
        if(!IsGameMapped){
            
            try{
                for(GameCertification game : GameList){
                    ImageView view = new ImageView(new Image(game.getImagesPathList().get(0).toUri().toString()));
                    view.fitHeightProperty().setValue(150);
                    view.fitWidthProperty().setValue(150);
                    view.setOnMouseEntered((eve) -> {
                        final ImageView TriggerView = (ImageView)eve.getSource();
                        GameDisplayController.onImageFocused(TriggerView);
                    });
                    view.setUserData(game);
                    BaseTilePane.getChildren().add(view);
                }
            }catch(Exception e){
                System.err.println(e);
            }
        }
    }
}
