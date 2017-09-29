package capslock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class WarningTimer {
	private int counter=0;
	private int popupsecond=10;
	private OverLayWindow window=new OverLayWindow();
	private Timeline timer=new Timeline();

	public void Start() {
		 timer = new Timeline(new KeyFrame(Duration.millis(popupsecond*1000), new EventHandler<ActionEvent>(){
	            @Override
	            public void handle(ActionEvent event) {
	                window.Exe();
	                counter++;
	                if(counter>=2) {
	                	timer.stop();
	                }
	            }
	        }));
	        timer.setCycleCount(Timeline.INDEFINITE);
	        timer.play();
	}
	public void pre() {
		counter=0;
	}
}
