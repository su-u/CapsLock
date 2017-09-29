package capslock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class OverLayWindow {
	private int closesecond=5;
	static private int mynumber=0;
	private String warnmesse;

	public void Exe() {
		mynumber++;
		if(mynumber==1) {
			warnmesse="Warning1!";
		}else if(mynumber==2) {
			warnmesse="Warning2!";
		}
	    Stage primaryStage = new Stage(StageStyle.TRANSPARENT);
	    StackPane root = new StackPane();

	    Scene scene = new Scene(root, 300, 120);
	    scene.setFill(null);

	    Label label = new Label(warnmesse);
	    label.setFont(new Font("Arial", 30));
	    BorderPane borderPane = new BorderPane();
	    borderPane.setCenter(label);
	    borderPane.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(0,0,0,0.3);");

	    root.getChildren().add(borderPane);


	    Rectangle2D d = Screen.getPrimary().getVisualBounds();
	    primaryStage.setScene(scene);
	    primaryStage.setAlwaysOnTop(true);
	    primaryStage.setX(d.getWidth()-300);
	    primaryStage.setY(d.getHeight()-300);

	    primaryStage.show();

	    Timeline timer = new Timeline(new KeyFrame(Duration.millis(closesecond*1000), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
            	primaryStage.close();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
	}
}
