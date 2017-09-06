package capslock;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author RISCassembler
 */

public class FlowText {
    private final Label DisplayText;
    private final TranslateTransition transition;
    public FlowText(String text){
        DisplayText = new Label(text);
        transition = new TranslateTransition(Duration.seconds(8), DisplayText);
        transition.setFromX(200);
        transition.setToX(-200);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
    }
    
    public void run(){
        transition.play();
    }
    
    public Label getNode(){
        return DisplayText;
    }
}
