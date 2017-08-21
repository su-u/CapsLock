package capslock;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class GameDisplayBoxController implements Initializable {

    private GameCertification game;
    
    @FXML VBox DisplayBox;
    @FXML ImageView GameSSView;
    @FXML Label TitleLabel;
    @FXML Label DiscriptionLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void onImageFocused(ImageView view){
        game = (GameCertification)view.getUserData();
        GameSSView.setImage(view.getImage());
        TitleLabel.setText(game.getName());
        DiscriptionLabel.setText("discride");
        final Point2D point = view.localToScreen(view.getScene().getX(), view.getScene().getY());
        DisplayBox.relocate(point.getX(), point.getY());
        DisplayBox.visibleProperty().setValue(true);
    }
    
    @FXML
    private void onMouseExited(MouseEvent ev){DisplayBox.visibleProperty().setValue(false);}
    
    @FXML
    private void onGameClicked(MouseEvent ev){
        ProcessBuilder pb = new ProcessBuilder(game.getExecutablePath().toString());
        pb.redirectErrorStream(true);
        try {
            Process GameProcess = pb.start();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
