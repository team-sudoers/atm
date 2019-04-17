/**
 * Sample Skeleton for 'MainPage.fxml' Controller Class
 */
package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public class MainPageController extends BaseAtmController {

   @FXML // fx:id="topLabel"
   private Label topLabel; // Value injected by FXMLLoader

   @FXML // fx:id="accountBalancesLabel"
   private Label accountBalancesLabel; // Value injected by FXMLLoader

   @FXML // fx:id="checkingLabel"
   private Label checkingLabel; // Value injected by FXMLLoader

   @FXML // fx:id="checkingAmountLabel"
   private Label checkingAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="savingsLabel"
   private Label savingsLabel; // Value injected by FXMLLoader

   @FXML // fx:id="savingsAmountLabel"
   private Label savingsAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="lastTransactionLabel"
   private Label lastTransactionLabel; // Value injected by FXMLLoader

   @FXML // fx:id="lastTransactionDateLabel"
   private Label lastTransactionDateLabel; // Value injected by FXMLLoader

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      try {
         super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
         
         String pattern = "MM/dd/yyyy";
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
         topLabel.setText(String.format("%s", simpleDateFormat.format(new Date())));
         
         UserAccount user = getAtmService().getLoggedInUser();
         BankAccount ca = getAtmService().getCheckingAccount();
         BankAccount sa = getAtmService().getSavingsAccount();
         Transaction lastTransaction = getAtmService().getLastTransaction();
         
         if (ca != null) {
            checkingAmountLabel.setText(String.format("$%.2f", ca.getAccountBalance()));
         }
         
         if (sa != null) {
            savingsAmountLabel.setText(String.format("$%.2f", sa.getAccountBalance()));
         }
         
         if (lastTransaction != null) {
            lastTransactionDateLabel.setText(String.format("%s", simpleDateFormat.format(lastTransaction.getDate())));
         }
      } catch (Exception ex) {
         Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   @FXML
   void handleAccountHistoryAction(ActionEvent event) {
      showHistoryPage();
   }

   @FXML
   void handleDepositAction(ActionEvent event) {
      showDepositPage();
   }

   @FXML
   void handleLogoutAction(ActionEvent event) {
      try {
         getAtmService().logout();
         showLoginPage();
      } catch (Exception ex) {
         Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @FXML
   void handleTransferAction(ActionEvent event) {
      showTransferPage();
   }

   @FXML
   void handleWithdrawAction(ActionEvent event) {
      showWithdrawPage();
   }

}
