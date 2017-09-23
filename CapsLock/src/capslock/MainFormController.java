package capslock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author RISCassembler
 */
public class MainFormController implements Initializable {

    private static final double TileSizeScale = 2.5;
    private static final String DB_FILE_NAME = "GamesInfo.json";
    
    private enum State{
        None,
        ImageOnly,
        MediaOnly,
        Both_Image,
        Both_Media
    }
        
    private State DisplayState;

    private GameCertification game;
    
    private Timeline ImageTimeLine;
    private List<Image> ImageList = new ArrayList<>();
    private Iterator<Image> ImageIterator;
    private List<Media> MovieList = new ArrayList<>();
    private Iterator<Media> MovieIterator;
    private Timeline Scroller;
    
    ResizableMediaView GameMovieView = new ResizableMediaView();
    ResizableImageView GameImageView = new ResizableImageView();

    private boolean IsGameMapped = false;
    private final List<GameCertification> GameList;
    
    @FXML Label NameLabel;
    @FXML Label DiscriptionLabel;
    @FXML StackPane ViewStackPane;
    @FXML ScrollPane LabelScroller;
    @FXML TilePane LeftTilePane;
    

    public MainFormController() {
        List<GameCertification> ListBuilder = new ArrayList<>();
        
        try(final BufferedReader reader = new BufferedReader(new FileReader(DB_FILE_NAME));){
            
            final String JsonString = reader.readLine();
            new JSONArray(JsonString).forEach(record -> ListBuilder.add(new GameCertification((JSONObject) record)));
            
        } catch (FileNotFoundException ex) {
            LogHandler.instance.warning("Failed to open " + DB_FILE_NAME);
        } catch (IOException ex) {
            LogHandler.instance.warning("IOException : " + DB_FILE_NAME + " can be wrong.");
        } catch(JSONException ex){
            LogHandler.instance.warning("JSONException : " + DB_FILE_NAME + " must be wrong.");
        } catch(Exception ex){
            GameList = null;
            return;
        }
        
        GameList = Collections.unmodifiableList(ListBuilder);
        LogHandler.instance.fine(GameList.size() + "件のゲームを検出");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        ImageTimeLine = new Timeline(new KeyFrame(
        Duration.millis(2500),
        ae -> UpdateImage(ae)));
        ImageTimeLine.setCycleCount(Animation.INDEFINITE);
        
        ViewStackPane.getChildren().add(GameMovieView);
        ViewStackPane.getChildren().add(GameImageView);
        
        Scroller = new Timeline();
        Scroller.getKeyFrames().add(new KeyFrame(Duration.seconds(25),
                new KeyValue (LabelScroller.hvalueProperty(), 1000)));
        Scroller.setCycleCount(Timeline.INDEFINITE);
    }
    
    public void onLoad(WindowEvent event){
        if(IsGameMapped)return;
        
        final double PanelImageSideLength;
        {
            final double width = LeftTilePane.getWidth() / TileSizeScale;
            final double height = LeftTilePane.getHeight() / TileSizeScale;
            PanelImageSideLength = Double.min(width, height);
        }
            
        for(GameCertification game : GameList){
            final Image PanelImage;
            
            if(Files.isRegularFile(game.getPanelPath())){
                PanelImage = new Image(game.getPanelPath().toUri().toString());
            }else{
                PanelImage = GenerateCharPanel(game.getName().charAt(0));
                LogHandler.instance.warning("game's UUID : " + game.getUUID().toString() + " doesn't have panel image.");
            }
            
            final ImageView view = new ImageView(PanelImage);
            view.fitHeightProperty().setValue(PanelImageSideLength);
            view.fitWidthProperty().setValue(PanelImageSideLength);
//            view.setOnMouseEntered((eve) -> {
//                final ImageView TriggerView = (ImageView)eve.getSource();
//                GameDisplayController.onImageFocused(TriggerView);
//            });
            view.setUserData(game);
            LeftTilePane.getChildren().add(view);
        }
    }
    
    static private Image GenerateCharPanel(final char ch){
        
        final Label label = new Label(Character.toString(Character.toUpperCase(ch)));
        label.setMinSize(125, 125);
        label.setMaxSize(125, 125);
        label.setPrefSize(125, 125);
        label.setFont(Font.font(80));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        label.setBackground(new Background(new BackgroundFill(ColorSequencer.get(), CornerRadii.EMPTY, Insets.EMPTY)));
        final Scene scene = new Scene(new Group(label));
        final WritableImage img = new WritableImage(125, 125);
        scene.snapshot(img);
        return img ;
    }
    

    
    class onMovieEndClass implements Runnable{
        @Override
        public void run(){
            try{
                PlayMovie(MovieIterator.next());
            }catch(NoSuchElementException e){
                if(DisplayState == State.MediaOnly){
                    MovieIterator = MovieList.iterator();
                    PlayMovie(MovieIterator.next());
                }else{
                    DisplayState = State.Both_Image;
                    
                    ImageIterator = ImageList.iterator();
                    GameImageView.setImage(ImageIterator.next());
                    ImageTimeLine.play();
                    SwapDisplayContentType();
                }
            }
        }
    }
    
    Runnable onMovieEnd = new onMovieEndClass();

    public void onImageFocused(ImageView view){
        System.out.println("forcus");
        if(game != null){
            onMouseExited(null);
        }
        
        game = (GameCertification)view.getUserData();
        NameLabel.setText(game.getName());
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
                
                break;
                
            case 0b11:
                DisplayState = State.Both_Image;
                ImageSet();
                break;

            default:
                System.err.println("critical ; unexpected flag");
        }

        DiscriptionLabel.setPadding(Insets.EMPTY);
        DiscriptionLabel.autosize();
        double textwidth = DiscriptionLabel.getWidth();
        
        if(LabelScroller.getWidth() < textwidth){
            DiscriptionLabel.setPadding(new Insets(0, textwidth, 0, textwidth));
        }
        
        LabelScroller.setHmax(textwidth);
        Scroller.play();
    }
    
    @FXML
    private void onMouseExited(MouseEvent ev){
        System.err.println("exit");
        
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
                SwapDisplayContentType();
            }
        }
        System.err.println("timer");
        System.err.println(DiscriptionLabel.getLayoutX());
    }
    
    private void ImageSet(){
        ImageIterator = ImageList.iterator();
        GameImageView.setImage(ImageIterator.next());
        ImageTimeLine.play();

        GameMovieView.setVisible(false);
    }
    
    private void PlayMovie(Media movie){
        MediaPlayer player = new MediaPlayer(movie);
        player.setOnEndOfMedia(onMovieEnd);
        player.setAutoPlay(true);
        player.setCycleCount(1);       
        GameMovieView.setMediaPlayer(player);
    }
    
    private void SwapDisplayContentType(){
        GameImageView.setVisible(!GameImageView.isVisible());
        GameMovieView.setVisible(!GameMovieView.isVisible());
    }
}
