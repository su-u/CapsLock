package capslock;

import java.io.IOException;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author RISCassembler
 */
public class CapsLock extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
            return;
        }
        
        MainFormController controller = (MainFormController)loader.getController();
        stage.setScene(new Scene(root));
        stage.setOnShowing((event) -> controller.onLoad(event));
        stage.setTitle("CapsLock");
        stage.show();
    }
}
