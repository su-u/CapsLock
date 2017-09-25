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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
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
    
    @FXML private HBox RootHBox;
    @FXML private ScrollPane LeftScrollPane;
    @FXML private Label NameLabel;
    @FXML private Label DescriptionLabel;
    @FXML private StackPane ViewStackPane;
    @FXML private ScrollPane LabelScroller;
    @FXML private TilePane PanelTilePane;
    @FXML private VBox RightVBox;
    

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
            final double FullScreenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            final double RightSize = FullScreenWidth / 3.0 * 2.0;
            RightVBox.setPrefWidth(RightSize);
            RightVBox.setMinWidth(RightSize);
            RightVBox.setMaxWidth(RightSize);
            final double LeftSize = FullScreenWidth - RightSize;
            LeftScrollPane.setPrefWidth(LeftSize);
            LeftScrollPane.setMinWidth(LeftSize);
            LeftScrollPane.setMaxWidth(LeftSize); 

//            PanelTilePane.setPrefWidth(LeftSize);
//            PanelTilePane.setMinWidth(LeftSize);
//            PanelTilePane.setMaxWidth(LeftSize); 
            
            PanelImageSideLength = LeftSize / 10 * 3;
            
            final double Gap = LeftSize / 20;
            
            PanelTilePane.setVgap(Gap);
            PanelTilePane.setHgap(Gap);
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
            view.setPreserveRatio(false);
            view.setFitWidth(PanelImageSideLength);
            view.setFitHeight(PanelImageSideLength);
            view.setOnMouseEntered((eve) -> {
                final ImageView TriggerView = (ImageView)eve.getSource();
                onImageFocused(TriggerView);
            });
            view.setUserData(game);
            PanelTilePane.getChildren().add(view);
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
        if(game != null)ReleasePreviousGameContents();
        
        game = (GameCertification)view.getUserData();
        NameLabel.setText(game.getName());
        DescriptionLabel.setText(game.getDescription());
        
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

        DescriptionLabel.setPadding(Insets.EMPTY);
        DescriptionLabel.autosize();
        double textwidth = DescriptionLabel.getWidth();
        
        if(LabelScroller.getWidth() < textwidth){
            DescriptionLabel.setPadding(new Insets(0, textwidth, 0, textwidth));
        }
        
        LabelScroller.setHmax(textwidth);
        Scroller.play();
    }
    
    private void ReleasePreviousGameContents(){
        ImageTimeLine.stop();
        ImageList.clear();
        try{
            GameMovieView.getMediaPlayer().stop();
        }catch(NullPointerException e){
        }
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
        System.err.println(DescriptionLabel.getLayoutX());
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
