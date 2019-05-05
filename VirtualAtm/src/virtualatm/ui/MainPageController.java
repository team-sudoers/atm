/* 
 * File:    MainPageController.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 * The Main page controller bound to the MainPage.fxml. Retrieves account information from the atm service and allows
 * the user to perform various account operations.
 */
public class MainPageController extends BaseAtmController {

   /////////////////////////////////////////////////////////////////////////////
   // Begin FXML bound controls set in scene builder
   @FXML // fx:id="topLabel"
   private Label topLabel; // Value injected by FXMLLoader

   @FXML // fx:id="checkingAmountLabel"
   private Label checkingAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="savingsAmountLabel"
   private Label savingsAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="lastTransactionDateLabel"
   private Label lastTransactionDateLabel; // Value injected by FXMLLoader
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
      try {
         super.initialize(url, rb);

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
      } catch (Exception e) {
         super.showError(e.getMessage());
      }

   }

   /**
    * Account history button click handler. Shows the account history page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleAccountHistoryAction(ActionEvent event) {
      showHistoryPage();

   }

   /**
    * Deposit button click handler. Shows the deposit page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleDepositAction(ActionEvent event) {
      showDepositPage();
   }

   /**
    * Logout button click handler. Logs the user out of the atm service and shows the login page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleLogoutAction(ActionEvent event) {
      try {
         getAtmService().logout();
         showLoginPage();
      } catch (Exception e) {
         super.showError(e.getMessage());
      }
   }

   /**
    * Transfer button click handler. Shows the transfer page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleTransferAction(ActionEvent event) {
      showTransferPage();
   }

   /**
    * Withdrawal button click handler. Shows the withdraw page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleWithdrawAction(ActionEvent event) {
      showWithdrawPage();
   }

}
