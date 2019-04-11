/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import virtualatm.service.FakeAtmService;
import virtualatm.service.IAtmService;
import virtualatm.ui.LoginPageController;
import virtualatm.ui.MainPageController;

/**
 *
 * @author Matt
 */
public class VirtualAtm extends Application {

   @Override
   public void start(Stage primaryStage) {

      try {

         IAtmService svc = new FakeAtmService();
         
         FXMLLoader mainPageLoader = new FXMLLoader(getClass().getResource("virtualatm/ui/MainPage.fxml"));
         Parent mainPage = mainPageLoader.load();
         Scene mainScene = new Scene(mainPage);

         FXMLLoader loginPaneLoader = new FXMLLoader(getClass().getResource("virtualatm/ui/LoginPage.fxml"));
         Parent loginPage = loginPaneLoader.load();
         Scene loginScene = new Scene(loginPage);

         LoginPageController loginPageController = (LoginPageController) loginPaneLoader.getController();
         loginPageController.setMainScene(mainScene);
         loginPageController.setAtmService(svc);

         // injecting first scene into the controller of the second scene
         MainPageController mainPageController = (MainPageController) mainPageLoader.getController();
         mainPageController.setLoginScene(loginScene);
         mainPageController.setAtmService(svc);
         
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
