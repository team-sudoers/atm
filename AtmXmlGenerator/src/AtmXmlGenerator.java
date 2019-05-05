/* 
 * File:    AtmXmlGenerator.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: AtmXmlGenerator
 * Course:  UMUC CMSC 495-7982
 */
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The class storing the entrypoint to the application
 */
public class AtmXmlGenerator extends Application {

   /**
    * Overridden JavaFX application start method used to initialize the application
    *
    * @param primaryStage The JavaFX stage used to display the application
    */
   @Override
   public void start(Stage stage) {
      try {
         Parent root = FXMLLoader.load(getClass().getResource("virtualatm/ui/Main.fxml"));

         Scene scene = new Scene(root);

         stage.setScene(scene);
         stage.show();
      } catch (IOException e) {
         System.err.println(e);
      }

   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

}
