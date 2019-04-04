/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Matt
 */
public class JavaFXApplication extends Application {
   
   @Override
   public void start(Stage stage) {
      try {
         Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

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
