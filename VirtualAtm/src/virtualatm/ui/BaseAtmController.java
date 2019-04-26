/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.ui;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import virtualatm.service.AtmServiceError;
import virtualatm.service.IAtmService;
import virtualatm.service.LocalAtmService;

/**
 *
 * @author Matt
 */
public class BaseAtmController implements Initializable {

   private static final String DEPOSITPAGEKEY = "DepositPage.fxml";
   private static final String HISTORYPAGEKEY = "HistoryPage.fxml";
   private static final String LOGINPAGEKEY = "LoginPage.fxml";
   private static final String MAINPAGEKEY = "MainPage.fxml";
   private static final String TRANSFERPAGEKEY = "TransferPage.fxml";
   private static final String WITHDRAWPAGEKEY = "WithdrawPage.fxml";

   @FXML // ResourceBundle that was given to the FXMLLoader
   private ResourceBundle resources;

   @FXML // URL location of the FXML file that was given to the FXMLLoader
   private URL location;

   private ObjectProperty<Locale> locale;

   private static IAtmService atmService;

   private static Stage primaryStage;

   /**
    * Initializes the controller class.
    *
    * @param url
    * @param rb
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      location = url;
      resources = rb;
      locale = new SimpleObjectProperty<>(Locale.getDefault());
      locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
   }

   public IAtmService getAtmService() {
      if (atmService == null) {
         atmService = new LocalAtmService();
      }
      return atmService;
   }

   public void setLanguageId(String langId, String country) {
      Locale value = new Locale(langId, country);
      resources = ResourceBundle.getBundle("virtualatm/ui/resources/uitext", value);
      locale.set(value);
   }

   public String getTranslatedText(final String key, final Object... args) {
      return MessageFormat.format(resources.getString(key), args);
   }

   public StringBinding createTranslatedTextBinding(final String key, Object... args) {
      return Bindings.createStringBinding(() -> getTranslatedText(key, args), locale);
   }

   public void showError(String message) {
      Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
      msgbox.setHeaderText(getTranslatedText("ERROR_TITLE"));
      msgbox.showAndWait();
   }

   public void showError(AtmServiceError error) {
      String message = getTranslatedText(error.toString());
      if (message != null) {
         Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
         msgbox.setHeaderText(getTranslatedText("ERROR_TITLE"));
         msgbox.showAndWait();
      }
   }

   public boolean askYesNoQuestion(String title, String question) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
      alert.setTitle(title);
      alert.setHeaderText(null);
      Optional<ButtonType> result = alert.showAndWait();
      return result.get() == ButtonType.YES;
   }

   public void showMainPage() {
      showPage(MAINPAGEKEY);
   }

   public void showLoginPage() {
      showPage(LOGINPAGEKEY);
   }

   public void showDepositPage() {
      showPage(DEPOSITPAGEKEY);
   }

   public void showHistoryPage() {
      showPage(HISTORYPAGEKEY);
   }

   public void showTransferPage() {
      showPage(TRANSFERPAGEKEY);
   }

   public void showWithdrawPage() {
      showPage(WITHDRAWPAGEKEY);
   }

   public static void setStage(Stage stage) {
      primaryStage = stage;
   }

   private void showPage(String fxmlPath) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), resources);

         Scene scene = new Scene(loader.load());
         primaryStage.setScene(scene);
         primaryStage.show();
      } catch (IOException ex) {
         Logger.getLogger(BaseAtmController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
