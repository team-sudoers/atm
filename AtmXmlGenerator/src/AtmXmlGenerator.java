/* 
 * File:    AtmXmlGenerator.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: AtmXmlGenerator
 * Course:  UMUC CMSC 495-7982
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class AtmXmlGenerator extends Application {
   
   @Override
   public void start(Stage stage) {
      try {
         Parent root = FXMLLoader.load(getClass().getResource("virtualatm/ui/Main.fxml"));

         Scene scene = new Scene(root);

         stage.setScene(scene);
         stage.show();         
      } catch (Exception e) {
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
