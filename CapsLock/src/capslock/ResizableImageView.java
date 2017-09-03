package capslock;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

/**
 *
 * @author RISCassembler
 */
public class ResizableImageView extends ImageView{
    public ResizableImageView(){
        setPreserveRatio(true);
    }
    
    @Override
    public boolean isResizable(){
        return true;
    }
    
    @Override
    public void resize(double width, double height){
        final Bounds ParentsBounds = getParent().getBoundsInLocal();
        setFitWidth(ParentsBounds.getWidth());
        setFitHeight(ParentsBounds.getHeight());
    }
}
