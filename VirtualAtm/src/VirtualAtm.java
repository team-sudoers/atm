/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import virtualatm.ui.BaseAtmController;

/**
 *
 * @author Matt
 */
public class VirtualAtm extends Application {

   @Override
   public void start(Stage primaryStage) {

      try {

         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("virtualatm/ui/LoginPage.fxml"));
         Scene loginScene = new Scene(fxmlLoader.load());

         BaseAtmController.setStage(primaryStage);
         
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
