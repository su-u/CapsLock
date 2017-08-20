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
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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
    @FXML VBox DisplayBox;
    @FXML ImageView GameSSView;
    @FXML Label TitleLabel;
    @FXML Label DiscriptionLabel;
    
    private boolean IsGameMapped = false;
    private final List<GameCertification> GameList;
    private GameCertification SelectedGame;

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
    public void initialize(URL url, ResourceBundle rb) {
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
                        SelectedGame = (GameCertification)TriggerView.getUserData();
                        GameSSView.setImage(TriggerView.getImage());
                        TitleLabel.setText(SelectedGame.getName());
                        DiscriptionLabel.setText("discride");
                        final Point2D point = view.localToScreen(view.getScene().getX(), view.getScene().getY());
                        DisplayBox.relocate(point.getX(), point.getY());
                        DisplayBox.visibleProperty().setValue(true);
                    });
                    view.setUserData(game);
                    BaseTilePane.getChildren().add(view);
                }
            }catch(Exception e){
                System.err.println(e);
            }
        }
    }
    
    @FXML
    private void onMouseExited(MouseEvent ev){DisplayBox.visibleProperty().setValue(false);}
    
    @FXML
    private void onGameClicked(MouseEvent ev){
        ProcessBuilder pb = new ProcessBuilder(SelectedGame.getExecutablePath().toString());
        pb.redirectErrorStream(true);
        try {
            Process GameProcess = pb.start();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
