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
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class GameDisplayBoxController implements Initializable {
    
    private enum State{
        None,
        ImageOnly,
        MediaOnly,
        Both_Image,
        Both_Media
    }

    private GameCertification game;
    
    private Timeline ImageTimeLine;
    private List<Image> ImageList = new ArrayList();
    private Iterator<Image> ImageIterator;
    private List<Media> MovieList = new ArrayList();
    private Iterator<Media> MovieIterator;
    
    private State DisplayState;
    
    private FlowText flow;
    
    private Timeline Scroller;
    
    private double width;
    private double height;
    
    class onMovieEndClass implements Runnable{
        @Override
        public void run(){
            try{
                PlayMovie(MovieIterator.next());
            }catch(NoSuchElementException e){
                if(DisplayState == State.MediaOnly){
                    MovieIterator = MovieList.iterator();
                    PlayMovie(MovieIterator.next());

                    GameImageView.setVisible(false);
                    GameMovieView.setVisible(true);
                }else{
                    DisplayState = State.Both_Image;
                    
                    ImageIterator = ImageList.iterator();
                    GameImageView.setImage(ImageIterator.next());
                    ImageTimeLine.play();
                    GameImageView.setVisible(true);
                    GameMovieView.setVisible(false);
                }
            }
        }
    }
    
    Runnable onMovieEnd = new onMovieEndClass();
    
    @FXML VBox DisplayBox;
    @FXML Label TitleLabel;
    @FXML Label DiscriptionLabel;
    @FXML StackPane ViewStackPane;
    @FXML ScrollPane ClipBox;
    
    ResizableMediaView GameMovieView = new ResizableMediaView();
    ResizableImageView GameImageView = new ResizableImageView();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("init");
        
        ImageTimeLine = new Timeline(new KeyFrame(
        Duration.millis(2500),
        ae -> UpdateImage(ae)));
        ImageTimeLine.setCycleCount(Animation.INDEFINITE);
        
        ViewStackPane.getChildren().add(GameMovieView);
        ViewStackPane.getChildren().add(GameImageView);
        
        Scroller = new Timeline();
        Scroller.getKeyFrames().add(new KeyFrame(Duration.seconds(25),
                new KeyValue (ClipBox.hvalueProperty(), 1000)));
        Scroller.setCycleCount(Timeline.INDEFINITE);
    }

    public void onImageFocused(ImageView view){
        System.out.println("forcus");
        if(game != null){
            onMouseExited(null);
        }
        
        game = (GameCertification)view.getUserData();
        TitleLabel.setText(game.getName());
        
        //flow = new FlowText(game.getDescription());
        
        //DisplayBox.getChildren().add(flow.getNode());
        DiscriptionLabel.setText(game.getDescription());

        
        byte Flags = 0;
        
        game.getImagesPathList().forEach(path -> ImageList.add(new Image(path.toUri().toString())));
        game.getMoviePathList().forEach(path -> MovieList.add(new Media(path.toUri().toString())));
        
        if(!ImageList.isEmpty())Flags = 0b1;
        if(!MovieList.isEmpty())Flags += 0b10; 
        
        switch(Flags){
            case 0:
                DisplayState = State.None;
                break;
            case 0b1:
                DisplayState = State.ImageOnly;
                ImageSet();
                break;
            case 0b10:
                DisplayState = State.MediaOnly;
                MovieIterator = MovieList.iterator();
                PlayMovie(MovieIterator.next());

                GameImageView.setVisible(false);
                GameMovieView.setVisible(true);
                
                break;
                
            case 0b11:
                DisplayState = State.Both_Image;
                ImageSet();
                break;

            default:
                System.err.println("critical ; unexpected flag");
        }
        
         DisplayBox.visibleProperty().setValue(true);
        
        InitVBoxSize();
        final Point2D point = view.localToScreen(view.getScene().getX(), view.getScene().getY());
        DisplayBox.relocate(point.getX(), point.getY());
        
        //flow.run();
        DiscriptionLabel.setPadding(Insets.EMPTY);
        DiscriptionLabel.autosize();
        double textwidth = DiscriptionLabel.getWidth();
        
        if(ClipBox.getWidth() < textwidth){
            DiscriptionLabel.setPadding(new Insets(0, textwidth, 0, textwidth));
        }
        
        ClipBox.setHmax(textwidth);
        Scroller.play();
    }
    
    @FXML
    private void onMouseExited(MouseEvent ev){
        System.err.println("exit");
        
        DisplayBox.visibleProperty().setValue(false);
        ImageTimeLine.stop();
        ImageList.clear();
        try{
            GameMovieView.getMediaPlayer().stop();
        }catch(NullPointerException e){
        }
        GameMovieView.setMediaPlayer(null);
        MovieList.clear();
        game = null;
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
        width = ParentPane.getWidth() / 2;
        height = ParentPane.getHeight() / 2;
        DisplayBox.setPrefSize(width, height);
        DisplayBox.setMaxSize(width, height);
    }
    
    private void UpdateImage(ActionEvent event){
        try{
            GameImageView.setImage(ImageIterator.next());
        }catch(NoSuchElementException ex){
            if(DisplayState == State.ImageOnly){
                ImageIterator = ImageList.iterator();
                GameImageView.setImage(ImageIterator.next());
            }else{
                ImageTimeLine.stop();
                DisplayState = State.Both_Media;
                MovieIterator = MovieList.iterator();
                PlayMovie(MovieIterator.next());
                GameImageView.setVisible(false);
                GameMovieView.setVisible(true);
            }
        }
        System.err.println("timer");
        System.err.println(DiscriptionLabel.getLayoutX());
    }
    
    private void ImageSet(){
        ImageIterator = ImageList.iterator();
        GameImageView.setImage(ImageIterator.next());
        ImageTimeLine.play();
        
        GameImageView.setVisible(true);
        GameMovieView.setVisible(false);
    }
    
    private void PlayMovie(Media movie){
        MediaPlayer player = new MediaPlayer(movie);
        player.setOnEndOfMedia(onMovieEnd);
        player.setAutoPlay(true);
        player.setCycleCount(1);       
        GameMovieView.setMediaPlayer(player);
    }
    
    private void SwapDisplayType(){
        if(GameImageView.isDisabled())GameImageView.setDisable(false);
        if(GameMovieView.isDisabled())GameMovieView.setDisable(false);
    }
}
