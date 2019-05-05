/* 
 * File:    LoginPageController.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import virtualatm.service.AtmServiceError;

/**
 * The Login page controller bound to the LoginPage.fxml. Retrieves credentials from the user and uses the atm service
 * to attempt login.
 */
public class LoginPageController extends BaseAtmController {

   /////////////////////////////////////////////////////////////////////////////
   // Begin FXML bound controls set in scene builder
   @FXML // fx:id="welcomeText"
   private Label welcomeText; // Value injected by FXMLLoader

   @FXML // fx:id="userName"
   private TextField userName; // Value injected by FXMLLoader

   @FXML // fx:id="userPin"
   private PasswordField userPin; // Value injected by FXMLLoader

   @FXML // fx:id="loginButton"
   private Button loginButton; // Value injected by FXMLLoader
   // End FXML bound controls set in scene builder
   /////////////////////////////////////////////////////////////////////////////

   /**
    * Initializes the controller class.
    *
    * @param url The location of the FXML which initialized this controller
    * @param rb The resource bundle instance used to initialize this controller
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb);

      stopTimer();
      welcomeText.textProperty().bind(createTranslatedTextBinding("welcomeLabelText"));
      userName.promptTextProperty().bind(createTranslatedTextBinding("userNameText"));
      userPin.promptTextProperty().bind(createTranslatedTextBinding("userPinText"));
      loginButton.textProperty().bind(createTranslatedTextBinding("loginButtonText"));
   }

   /**
    * German button click handler. Switches the application default locale to german.
    *
    * @param event The action event instance
    */
   @FXML
   void handleDeutschAction(ActionEvent event) {
      setLanguageId("de", "DE");
   }

   /**
    * English button click handler. Switches the application default locale to english.
    *
    * @param event The action event instance
    */
   @FXML
   void handleEnglishAction(ActionEvent event) {
      setLanguageId("en", "EN");
   }

   /**
    * French button click handler. Switches the application default locale to french.
    *
    * @param event The action event instance
    */
   @FXML
   void handleFrenchAction(ActionEvent event) {
      setLanguageId("fr", "FR");
   }

   /**
    * Korean button click handler. Switches the application default locale to korean.
    *
    * @param event The action event instance
    */
   @FXML
   void handleKoreanAction(ActionEvent event) {
      setLanguageId("ko", "KO");
   }

   /**
    * Login button click handler. Uses the atm service to login the user from the provided credentials.
    *
    * @param event The action event instance
    */
   @FXML
   void handleLoginAction(ActionEvent event) {
      try {
         if (validateUserInput() == false) {
            String message = getTranslatedText("MISSING_INPUT");
            showError(message);
            return;
         }

         AtmServiceError error = getAtmService().login(userName.getText(), userPin.getText());
         if (error != AtmServiceError.SUCCESS) {
            showError(error);
         } else {
            super.showMainPage();
         }

      } catch (Exception e) {
         super.showError(e.getMessage());
      }
   }

   /**
    * Simplified chinese button click handler. Switches the application default locale to simplified chinese.
    *
    * @param event The action event instance
    */
   @FXML
   void handleSimplifiedChineseAction(ActionEvent event) {
      setLanguageId("zh", "CN");
   }

   /**
    * Spanish button click handler. Switches the application default locale to spanish.
    *
    * @param event The action event instance
    */
   @FXML
   void handleSpanishAction(ActionEvent event) {
      setLanguageId("es", "ES");
   }

   /**
    * Verifies the user has entered a user name and pin
    *
    * @return true/false based upon whether the user has enter the required information
    */
   private boolean validateUserInput() {
      if (userName.getText().length() <= 0) {
         return false;
      }

      if (userPin.getText().length() <= 0) {
         return false;
      }

      return true;
   }

}
