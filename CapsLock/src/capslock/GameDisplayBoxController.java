package capslock;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class GameDisplayBoxController implements Initializable {

    private GameCertification game;
    
    private Timeline ImageTimeLine;
    private List<Image> ImageList = new ArrayList();
    private Iterator<Image> ImageIterator;
    
    @FXML VBox DisplayBox;
    @FXML ImageView GameSSView;
    @FXML Label TitleLabel;
    @FXML Label DiscriptionLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ImageTimeLine = new Timeline(new KeyFrame(
        Duration.millis(2500),
        ae -> UpdateImage(ae)));
        ImageTimeLine.setCycleCount(Animation.INDEFINITE);
    }

    public void onImageFocused(ImageView view){
        game = (GameCertification)view.getUserData();
        Image SS = new Image(game.getImagesPathList().get(0).toUri().toString());
        GameSSView.setImage(SS);
        TitleLabel.setText(game.getName());
        DiscriptionLabel.setText(game.getDescription());
        InitVBoxSize();
        final Point2D point = view.localToScreen(view.getScene().getX(), view.getScene().getY());
        DisplayBox.relocate(point.getX(), point.getY());
        
        game.getImagesPathList().forEach(path -> ImageList.add(new Image(path.toUri().toString())));
        ImageIterator = ImageList.listIterator();
        DisplayBox.visibleProperty().setValue(true);
        
        ImageTimeLine.play();
    }
    
    @FXML
    private void onMouseExited(MouseEvent ev){
        DisplayBox.visibleProperty().setValue(false);
        ImageTimeLine.stop();
        ImageList.clear();
    }
    
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
    
    private void InitVBoxSize(){
        final AnchorPane ParentPane = (AnchorPane)DisplayBox.getParent();
        final double width = ParentPane.getWidth() / 2;
        final double height = ParentPane.getHeight() / 2;
        DisplayBox.setPrefSize(width, height);
    }
    
    private void UpdateImage(ActionEvent event){
        try{
            GameSSView.setImage(ImageIterator.next());
        }catch(NoSuchElementException ex){
            ImageIterator = ImageList.iterator();
            GameSSView.setImage(ImageList.get(0));
        }
        System.err.println("timer");
    }
}
