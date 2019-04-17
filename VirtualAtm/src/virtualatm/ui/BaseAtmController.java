/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.ui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import virtualatm.service.FakeAtmService;
import virtualatm.service.IAtmService;

/**
 *
 * @author Matt
 */
public class BaseAtmController implements Initializable {

//   private static final String DEPOSITPAGEKEY = "deposit";
//   private static final String HISTORYPAGEKEY = "history";
//   private static final String LOGINPAGEKEY = "login";
//   private static final String MAINPAGEKEY = "main";
//   private static final String TRANSFERPAGEKEY = "transfer";
//   private static final String WITHDRAWPAGEKEY = "withdraw";
   private static final String DEPOSITPAGEKEY = "DepositPage.fxml";
   private static final String HISTORYPAGEKEY = "HistoryPage.fxml";
   private static final String LOGINPAGEKEY = "LoginPage.fxml";
   private static final String MAINPAGEKEY = "MainPage.fxml";
   private static final String TRANSFERPAGEKEY = "TransferPage.fxml";
   private static final String WITHDRAWPAGEKEY = "WithdrawPage.fxml";

   public static void setStage(Stage stage) {
      primaryStage = stage;
   }

   @FXML // ResourceBundle that was given to the FXMLLoader
   private ResourceBundle resources;

   @FXML // URL location of the FXML file that was given to the FXMLLoader
   private URL location;

   private String languageId;

   private static IAtmService atmService;
   private static final Map<String, Scene> pages = new HashMap<>();
   private static Stage primaryStage;

   /**
    * Initializes the controller class.
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      // TODO
   }

   public IAtmService getAtmService() {
      if (atmService == null) {
         atmService = new FakeAtmService();
      }

      return atmService;
   }

   public void setLanguageId(String langId) {
      languageId = langId;
   }

   public String getLanguageId() {
      return languageId;
   }

   void showError(String message) {
      Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
      msgbox.setHeaderText("Add Error");
      msgbox.showAndWait();
   }

   void showMainPage() {
      showPage(MAINPAGEKEY);
   }

   void showLoginPage() {
      showPage(LOGINPAGEKEY);
   }

   void showDepositPage() {
      showPage(DEPOSITPAGEKEY);
   }

   void showHistoryPage() {
      showPage(HISTORYPAGEKEY);
   }

   void showTransferPage() {
      showPage(TRANSFERPAGEKEY);
   }

   void showWithdrawPage() {
      showPage(WITHDRAWPAGEKEY);
   }

   private void showPage(String fxmlPath) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
         
         Scene scene = new Scene(loader.load());
         primaryStage.setScene(scene);
         primaryStage.show();
      } catch (IOException ex) {
         Logger.getLogger(BaseAtmController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
