/**
 * Sample Skeleton for 'WithdrawPage.fxml' Controller Class
 */
package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WithdrawPageController extends BaseAtmController {

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
      
//      UserAccount user = getAtmService().getLoggedInUser();
//      BankAccount ca = getAtmService().getCheckingAccount(user);
//      BankAccount sa = getAtmService().getSavingsAccount(user);
   }

   @FXML
   void handleConfirmAction(ActionEvent event) {
//      getAtmService().withdraw(account);
   }

   @FXML
   void handleWithdrawEightyAction(ActionEvent event) {

   }

   @FXML
   void handleWithdrawFortyAction(ActionEvent event) {

   }

   @FXML
   void handleWithdrawOneHundredAction(ActionEvent event) {

   }

   @FXML
   void handleWithdrawSixtyAction(ActionEvent event) {

   }

   @FXML
   void handleWithdrawTwentyAction(ActionEvent event) {

   }

   @FXML
   void handleWithdrawTwoHundredAction(ActionEvent event) {

   }

}
