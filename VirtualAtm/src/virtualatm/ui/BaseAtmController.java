/* 
 * File:    BaseAtmController.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
import virtualatm.service.AtmServiceError;
import virtualatm.service.IAtmService;
import virtualatm.service.LocalAtmService;

/**
 * Base UI controller class. Exposes common functionality needed by all user interface controllers including page
 * navigation, retrieving text translations, and accessing the atm service.
 */
public class BaseAtmController implements Initializable {

   /**
    * Constants for each page fxml file. Used to load/display pages.
    */
   private static final String DEPOSITPAGEKEY = "DepositPage.fxml";
   private static final String HISTORYPAGEKEY = "HistoryPage.fxml";
   private static final String LOGINPAGEKEY = "LoginPage.fxml";
   private static final String MAINPAGEKEY = "MainPage.fxml";
   private static final String TRANSFERPAGEKEY = "TransferPage.fxml";
   private static final String WITHDRAWPAGEKEY = "WithdrawPage.fxml";

   /**
    * Timeline object for tracking inactivity on each page
    */
   Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10000),
           ae -> handleTimer(ae)));

   /**
    * Resource bundle instance used by the application.
    */
   @FXML // ResourceBundle that was given to the FXMLLoader
   private ResourceBundle resources;

   /**
    * The location of the FXML file that was given to the FXMLLoader
    */
   @FXML
   private URL location;

   /**
    * The bindable object property used to monitor changes to the default locale
    */
   private ObjectProperty<Locale> locale;

   /**
    * The shared instance of the ATM service component
    */
   private static IAtmService atmService;

   /**
    * The primary stage of the JavaFX application
    */
   private static Stage primaryStage;

   /**
    * Initializes the controller class.
    *
    * @param url The location of the FXML which initialized this controller
    * @param rb The resource bundle instance used to initialize this controller
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {      
      location = url;
      resources = rb;
      locale = new SimpleObjectProperty<>(Locale.getDefault());
      locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
      resetTimer();
   }

   /**
    * Lazy loading accessor for the static ATM service component
    *
    * @return The shared ATM service instance
    */
   public IAtmService getAtmService() {
      if (atmService == null) {
         atmService = new LocalAtmService();
      }
      return atmService;
   }

   /**
    * Sets the Locale from the provided language id and country, loads the resources for a language, and triggers a
    * locale changed event.
    *
    * @param langId The java based language identifier desired
    * @param country The java based country identifier desired
    */
   public void setLanguageId(String langId, String country) {
      Locale value = new Locale(langId, country);
      resources = ResourceBundle.getBundle("virtualatm/ui/resources/uitext", value);
      locale.set(value);
   }

   /**
    * Retrieves the translated text from the application resources based on the provided key
    *
    * @param key The key for the desired text
    * @param args The arguments to format the text with
    * @return The translated text
    */
   public String getTranslatedText(final String key, final Object... args) {
      return MessageFormat.format(resources.getString(key), args);
   }

   /**
    * Creates a text binding that can be used by text based controls for dynamically updating the text when a locale
    * change occurs.
    *
    * @param key The key for the desired text
    * @param args The arguments to format the text with
    * @return A string binding to associate with a JavaFX control
    */
   public StringBinding createTranslatedTextBinding(final String key, Object... args) {
      return Bindings.createStringBinding(() -> getTranslatedText(key, args), locale);
   }

   /**
    * Shows a modal error messagebox using the provided string message
    *
    * @param message The message to display in the messagebox
    */
   public void showError(String message) {
      stopTimer();
      Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
      msgbox.setHeaderText(getTranslatedText("ERROR_TITLE"));
      msgbox.showAndWait();
      resetTimer();
   }

   /**
    * Shows a modal error messagebox with translated text for atm service errors ny using the error code toString method
    * as the resource key
    *
    * @param error The atm service error encountered
    */
   public void showError(AtmServiceError error) {
      stopTimer();
      String message = getTranslatedText(error.toString());
      if (message != null) {
         Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
         msgbox.setHeaderText(getTranslatedText("ERROR_TITLE"));
         msgbox.showAndWait();
      }
      resetTimer();
   }

   /**
    * Shows a non-modal error messagebox using the provided string message
    *
    * @param message The message to display in the messagebox
    */
   public void showTimerError(String message) {
      Alert msgbox = new Alert(Alert.AlertType.ERROR, message);
      msgbox.setHeaderText(getTranslatedText("ERROR_TITLE"));
      msgbox.setContentText(getTranslatedText("sessionTimeoutContent"));
      msgbox.show();
   }

   /**
    * Shows the withdrawal confirmation message box
    *
    * @param withdrawalMessage The message to display
    */
   public void showConfirmationWithdrawal(String withdrawalMessage) {
      Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
      confirmationAlert.setTitle(getTranslatedText("confirmationTitle"));
      confirmationAlert.setHeaderText(getTranslatedText("confirmationWithdrawalHeader"));
      confirmationAlert.setContentText(getTranslatedText("confirmationContent"));
      confirmationAlert.showAndWait();
   }

   /**
    * Shows the transfer confirmation message box
    *
    * @param transferMessage The message to display
    */
   public void showConfirmationTransfer(String transferMessage) {
      Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
      confirmationAlert.setTitle(getTranslatedText("confirmationTitle"));
      confirmationAlert.setHeaderText(getTranslatedText("confirmationTransferHeader"));
      confirmationAlert.setContentText(getTranslatedText("confirmationContent"));
      confirmationAlert.showAndWait();
   }

   /**
    * Shows the deposit confirmation message box
    *
    * @param depositMessage The message to display
    */
   public void showConfirmationDeposit(String depositMessage) {
      Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
      confirmationAlert.setTitle(getTranslatedText("confirmationTitle"));
      confirmationAlert.setHeaderText(getTranslatedText("confirmationDepositHeader"));
      confirmationAlert.setContentText(getTranslatedText("confirmationContent"));
      confirmationAlert.showAndWait();
   }

   /**
    * Timeline callback handler used for monitoring user inactivity
    *
    * @param event The action event instance
    */
   public void handleTimer(ActionEvent event) {
      timeline.stop();
      showLoginPage();
      showTimerError("");
      getAtmService().logout();

   }

   /**
    * Resets the user inactivity timeline
    */
   public void resetTimer() {
      timeline.stop();
      timeline.play();
   }

   /**
    * Stops the user inactivity timeline
    */
   public void stopTimer() {
      timeline.stop();
   }

   /**
    * Displays the main page
    */
   public void showMainPage() {
      showPage(MAINPAGEKEY);
   }

   /**
    * Displays the login page
    */
   public void showLoginPage() {
      showPage(LOGINPAGEKEY);
      stopTimer();
   }

   /**
    * Displays the deposit page
    */
   public void showDepositPage() {
      showPage(DEPOSITPAGEKEY);
   }

   /**
    * Displays the history page
    */
   public void showHistoryPage() {
      showPage(HISTORYPAGEKEY);
   }

   /**
    * Displays the transfer page
    */
   public void showTransferPage() {
      showPage(TRANSFERPAGEKEY);
   }

   /**
    * Displays the withdrawal page
    */
   public void showWithdrawPage() {
      showPage(WITHDRAWPAGEKEY);
   }

   /**
    * Sets the primary stage for all UI controllers
    *
    * @param stage The primary stage received from the JavaFX framework in onstart
    */
   public static void setStage(Stage stage) {
      primaryStage = stage;
   }

   /**
    * Loads and displays the provided fxml file on the primary stage
    */
   private void showPage(String fxmlPath) {
      try {
         stopTimer();
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), resources);

         Scene scene = new Scene(loader.load());
         primaryStage.setScene(scene);
         primaryStage.show();
      } catch (IOException ex) {
         Logger.getLogger(BaseAtmController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
