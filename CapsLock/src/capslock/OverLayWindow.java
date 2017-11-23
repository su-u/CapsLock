package capslock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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
    private static final int CLOSE_SECONDS=15;
    private String warnmesse;


    void Exe(int i) {
        if(i==1) {
            warnmesse="プレイ開始から5分\n経過しました";
        }else if(i==2) {
            warnmesse="プレイ開始から10分\n経過しました";
        }
        final Stage primaryStage = new Stage(StageStyle.TRANSPARENT);
        final StackPane root = new StackPane();

        final Scene scene = new Scene(root, 300, 120);
        scene.setFill(null);

        final Label label = new Label(warnmesse);
        label.setFont(new Font("Arial", 30));
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(label);
        borderPane.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(0,0,0,0.3);");

        root.getChildren().add(borderPane);

        final Rectangle2D d = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setX(d.getWidth()-300);
        primaryStage.setY(d.getHeight()-300);

        primaryStage.show();

        final Timeline timer = new Timeline(new KeyFrame(Duration.seconds(CLOSE_SECONDS), (ActionEvent event) -> primaryStage.close()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
}
