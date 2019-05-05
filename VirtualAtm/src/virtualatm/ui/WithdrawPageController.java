/* 
 * File:    WithdrawPageController.java
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.service.AtmServiceError;

/**
 * The Withdraw page controller bound to the WithdrawPage.fxml. Captures all user input and handles user feedback for
 * withdrawals and delegates work to the atm service instance as necessary.
 */
public class WithdrawPageController extends BaseAtmController {

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

   @FXML // fx:id="otherDepositAmount"
   private TextField otherDepositAmount; // Value injected by FXMLLoader

   @FXML // fx:id="fromAccount"
   private ComboBox<String> fromAccount; // Value injected by FXMLLoader

   @FXML // fx:id="selectAmount"
   private ComboBox<String> selectAmount; // Value injected by FXMLLoader
   // End FXML bound controls set in scene builder
   /////////////////////////////////////////////////////////////////////////////

   /**
    * The account type selected by the user. Set by the fromAccount combobox selection changed handler.
    */
   private String selectedAccountType;

   /**
    * The withdrawal amount selected by the user. Set by the selectAmount combobox selection changed handler.
    */
   private double withdrawAmount;

   /**
    * Initializes the controller class.
    *
    * @param url The location of the FXML which initialized this controller
    * @param rb The resource bundle instance used to initialize this controller
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb);
      fromAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
      selectAmount.getItems().addAll("20", "40", "60", "80", "100", "200");
      refresh();
   }

   /**
    * Confirm button click handler. Resets the timer, validation user input, and directs the atm service to perform the
    * withdrawal.
    *
    * @param event The action event instance
    */
   @FXML
   void handleConfirmAction(ActionEvent event) {
      try {
         resetTimer();
         if (validateUserInput() == false) {
            return;
         }

         BankAccount ba = null;
         if (selectedAccountType.equals(getTranslatedText("checking"))) {
            ba = getAtmService().getCheckingAccount();
         } else {
            ba = getAtmService().getSavingsAccount();
         }

         double amount = withdrawAmount;
         if (amount <= 0) {
            amount = parseWithdrawalAmount(otherDepositAmount.getText());
         }

         AtmServiceError error = getAtmService().withdraw(amount, ba);
         if (error != AtmServiceError.SUCCESS) {
            refresh();
            showError(error);
         } else {
            showConfirmationWithdrawal(selectedAccountType);
            showMainPage();
         }

      } catch (Exception e) {
         showError(e.getMessage());
      }
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
      } catch (Exception ex) {
         super.showError(ex.getMessage());
      }
   }

   /**
    * Selection changed event handler for the fromAccount combobox control.
    *
    * @param event The action event instance
    */
   @FXML
   void handleSelectAccountChange(ActionEvent event) {
      resetTimer();
      String accountType = fromAccount.getValue();
      if (accountType != null) {
         selectedAccountType = accountType;
      }
   }

   /**
    * Selection changed event handler for the selectAmount combobox control.
    *
    * @param event The action event instance
    */
   @FXML
   void handleSelectionAmountChanged(ActionEvent event) {
      resetTimer();
      String value = selectAmount.getValue();
      if (value != null) {
         withdrawAmount = Double.valueOf(value);
      }
   }

   /**
    * KeyPress event handler for the otherDepositAmount text box used to reset the inactivity timer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleKeyPressed(KeyEvent event) {
      resetTimer();
   }

   /**
    * Private method used to refresh the controls on this page from data provided by the atm service.
    */
   private void refresh() {
      try {
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
         super.showError(ex.getMessage());
      }
   }

   /**
    * Private method used to validate user input into controls from this page and display the necessary translated error
    * messages.
    *
    * @return true/false based on the status of the validation
    */
   private boolean validateUserInput() {

      if ((selectedAccountType == null) || (selectedAccountType.length() <= 0)) {
         String message = getTranslatedText("SOURCE_ACCOUNT_NOT_FOUND");
         showError(message);
         return false;
      }

      double tempAmount = withdrawAmount;
      try {

         if (tempAmount <= 0) {
            tempAmount = parseWithdrawalAmount(otherDepositAmount.getText());
         }
      } catch (Exception e) {
         tempAmount = -1;
      }

      if (tempAmount <= 0) {
         String message = getTranslatedText("INVALID_DOLLAR_AMOUNT");
         showError(message);
      }
      return tempAmount > 0;
   }

   /**
    * Private method used to convert a string based user entered dollar amount into a double value
    *
    * @param text The string to convert
    * @return The double value
    */
   private double parseWithdrawalAmount(String text) {

      if (text.length() <= 0) {
         return 0.0;
      }

      if (text.startsWith("$")) {
         text = text.substring(1);
      }

      return Double.parseDouble(text);
   }

   /**
    * Return button click handler. Shows the main page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleReturnAction(ActionEvent event) {
      showMainPage();
   }
}
