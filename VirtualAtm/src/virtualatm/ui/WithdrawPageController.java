/**
 * Sample Skeleton for 'WithdrawPage.fxml' Controller Class
 */
package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public class WithdrawPageController extends BaseAtmController {

   @FXML // fx:id="logoutButton"
   private Button logoutButton; // Value injected by FXMLLoader

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

   @FXML // fx:id="withdrawAcountTypeLabel"
   private Label withdrawAcountTypeLabel; // Value injected by FXMLLoader

   @FXML // fx:id="selectAmountLabel"
   private Label selectAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="checkingButton"
   private Button checkingButton; // Value injected by FXMLLoader

   @FXML // fx:id="SavingsButton"
   private Button SavingsButton; // Value injected by FXMLLoader

   @FXML // fx:id="otherDepositAmount"
   private TextField otherDepositAmount; // Value injected by FXMLLoader

   @FXML // fx:id="otherLabel"
   private Label otherLabel; // Value injected by FXMLLoader
   private String selectedAccountType;
   private double withdrawAmount;

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
      refresh();
   }

   @FXML
   void handleCheckingSelectedAction(ActionEvent event) {
      selectedAccountType = "checking";
   }

   @FXML
   void handleConfirmAction(ActionEvent event) {
      try {
         if (validateUserInput() == false) {
            showError("Please enter the required information");
            return;
         }

         UserAccount user = getAtmService().getLoggedInUser();
         if (user == null) {
            showError("You must login to perform withdrawals");
            showLoginPage();
            return;
         }

         BankAccount ba = null;
         if (selectedAccountType.equals("checking")) {
            ba = getAtmService().getCheckingAccount();
         } else {
            ba = getAtmService().getSavingsAccount();
         }

         double amount = withdrawAmount;
         if (amount <= 0) {
            amount = parseWithdrawalAmount(otherDepositAmount.getText());
         }

         getAtmService().withdraw(amount, ba);
         refresh();

      } catch (Exception e) {
         showError(e.getMessage());
      }
   }

   @FXML
   void handleLogoutAction(ActionEvent event) {
      getAtmService().logout();
      showLoginPage();
   }

   @FXML
   void handleSavingsSelectedAction(ActionEvent event) {
      selectedAccountType = "savings";
   }

   @FXML
   void handleWithdrawEightyAction(ActionEvent event) {
      withdrawAmount = 80.00;
   }

   @FXML
   void handleWithdrawFortyAction(ActionEvent event) {
      withdrawAmount = 40.00;
   }

   @FXML
   void handleWithdrawOneHundredAction(ActionEvent event) {
      withdrawAmount = 100.00;
   }

   @FXML
   void handleWithdrawSixtyAction(ActionEvent event) {
      withdrawAmount = 60.00;
   }

   @FXML
   void handleWithdrawTwentyAction(ActionEvent event) {
      withdrawAmount = 20.00;
   }

   @FXML
   void handleWithdrawTwoHundredAction(ActionEvent event) {
      withdrawAmount = 200.00;
   }

   private void refresh() {
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
   }

   private boolean validateUserInput() {
      if (selectedAccountType.length() <= 0) {
         return false;
      }

      double tempAmount = withdrawAmount;
      if (tempAmount <= 0) {
         tempAmount = parseWithdrawalAmount(otherDepositAmount.getText());
      }

      return tempAmount > 0;
   }

   private long parseWithdrawalAmount(String text) {

      if (text.length() <= 0) {
         return 0;
      }

      if (text.startsWith("$")) {
         text = text.substring(1);
      }

      return Long.parseLong(text);
   }

}
