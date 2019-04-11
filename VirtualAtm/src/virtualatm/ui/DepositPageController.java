/**
 * Sample Skeleton for 'DepositPage.fxml' Controller Class
 */
package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DepositPageController extends BaseAtmController {

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
//      UserAccount user = getAtmService().getLoggedInUser();
//      BankAccount ca = getAtmService().getCheckingAccount(user);
//      BankAccount sa = getAtmService().getSavingsAccount(user);
   }

   @FXML
   void handleConfirmAction(ActionEvent event) {
      //getAtmService().deposit(0, toAccount);
   }

}
