/**
 * Sample Skeleton for 'LoginPage.fxml' Controller Class
 */
package virtualatm.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginPageController extends BaseAtmController {

   @FXML // fx:id="welcomeText"
   private Label welcomeText; // Value injected by FXMLLoader

   @FXML // fx:id="userName"
   private TextField userName; // Value injected by FXMLLoader

   @FXML // fx:id="userPin"
   private TextField userPin; // Value injected by FXMLLoader
 
   @FXML
    private Button loginButton;
   
   @FXML
   void handleDeutschAction(ActionEvent event) {
      //setLanguageId();
   }

   @FXML
   void handleEnglishAction(ActionEvent event) {
      //setLanguageId();
   }

   @FXML
   void handleFrenchAction(ActionEvent event) {
      //setLanguageId();
   }

   @FXML
   void handleKoreanAction(ActionEvent event) {
      //setLanguageId();
   }

   
   @FXML
   void handleLoginAction(ActionEvent event) {
      try {
         if (validateUserInput() == false) {
            showError("Caution: Missing input-please enter all required fields.");
            return;
         }
         
         if (getAtmService().login(userName.getText(), userPin.getText()) == false) {
             
            showError("Invalid username or pin entered.");
            return;
         }
        
         super.showMainPage();
         
      } catch (Exception e) {
         super.showError(e.getMessage());
      }
   }

   @FXML
   void handleSimplifiedChineseAction(ActionEvent event) {
      //setLanguageId();
   }

   @FXML
   void handleSpanishAction(ActionEvent event) {
      //setLanguageId();
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
