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

public class LoginPageController extends BaseAtmController {

   @FXML // fx:id="welcomeText"
   private Label welcomeText; // Value injected by FXMLLoader

   @FXML // fx:id="userName"
   private TextField userName; // Value injected by FXMLLoader

   @FXML // fx:id="userPin"
   private PasswordField userPin; // Value injected by FXMLLoader

   @FXML // fx:id="loginButton"
   private Button loginButton; // Value injected by FXMLLoader

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb);

      welcomeText.textProperty().bind(createTranslatedTextBinding("welcomeLabelText"));
      userName.promptTextProperty().bind(createTranslatedTextBinding("userNameText"));
      userPin.promptTextProperty().bind(createTranslatedTextBinding("userPinText"));
      loginButton.textProperty().bind(createTranslatedTextBinding("loginButtonText"));
   }

   @FXML
   void handleDeutschAction(ActionEvent event) {
      setLanguageId("de", "DE");
   }

   @FXML
   void handleEnglishAction(ActionEvent event) {
      setLanguageId("en", "EN");
   }

   @FXML
   void handleFrenchAction(ActionEvent event) {
      setLanguageId("fr", "FR");
   }

   @FXML
   void handleKoreanAction(ActionEvent event) {
      setLanguageId("ko", "KO");
   }

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

   @FXML
   void handleSimplifiedChineseAction(ActionEvent event) {
      setLanguageId("zh", "CN");
   }

   @FXML
   void handleSpanishAction(ActionEvent event) {
      setLanguageId("es", "ES");
   }

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
