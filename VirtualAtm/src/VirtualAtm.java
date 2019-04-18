import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import virtualatm.ui.BaseAtmController;

public class VirtualAtm extends Application {

   @Override
   public void start(Stage primaryStage) {

      try {
         ResourceBundle bundle = ResourceBundle.getBundle("virtualatm.ui.resources.uitext", Locale.ENGLISH);
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("virtualatm/ui/LoginPage.fxml"), bundle);
         Scene loginScene = new Scene(fxmlLoader.load());

         BaseAtmController.setStage(primaryStage);
         primaryStage.resizableProperty().setValue(Boolean.FALSE); // disable maximize
         primaryStage.setTitle("CMSC 495 Virtual ATM");
         primaryStage.setScene(loginScene);
         primaryStage.show();
      } catch (Exception ex) {
         Logger.getLogger(VirtualAtm.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      launch(args);
   }

}
