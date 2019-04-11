/**
 * Sample Skeleton for 'MainPage.fxml' Controller Class
 */
package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainPageController extends BaseAtmController {

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
//      UserAccount user = getAtmService().getLoggedInUser();
//      BankAccount ca = getAtmService().getCheckingAccount(user);
//      BankAccount sa = getAtmService().getSavingsAccount(user);
//      List<Transaction> history = getAtmService().getAccountHistory(user);
   }

   
   @FXML
   void handleAccountHistoryAction(ActionEvent event) {
      // show account history page
   }

   @FXML
   void handleDepositAction(ActionEvent event) {
      // show deposit page
   }

   @FXML
   void handleLogoutAction(ActionEvent event) {
      // show login page
   }

   @FXML
   void handleTransferAction(ActionEvent event) {
      // show transfer page
   }

   @FXML
   void handleWithdrawAction(ActionEvent event) {
      // show withdraw page
   }

}
