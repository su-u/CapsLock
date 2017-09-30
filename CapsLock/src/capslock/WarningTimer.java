package capslock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

class WarningTimer {
    private int counter=0;
    private static final int popupsecond=10;
    private boolean closed=false;
    private final OverLayWindow window=new OverLayWindow();
    private Timeline timer=new Timeline();

    void Start() {
        closed=false;
         timer = new Timeline(new KeyFrame(Duration.millis(popupsecond*1000), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(closed) {
                window.Exe();
                counter++;
                if(counter>=2) {
                        timer.stop();
                }
                }
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    void pre() {
            counter=0;
            closed=true;
    }
}
