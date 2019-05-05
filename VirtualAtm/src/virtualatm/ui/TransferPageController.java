/* 
 * File:    TransferPageController.java
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
 * The Transfer page controller bound to the TransferPage.fxml. Captures all user input and handles user feedback for
 * withdrawals and delegates work to the atm service instance as necessary.
 */
public class TransferPageController extends BaseAtmController {

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

   @FXML // fx:id="transferAmount"
   private TextField transferAmount; // Value injected by FXMLLoader

   @FXML // fx:id="fromAccount"
   private ComboBox<String> fromAccount; // Value injected by FXMLLoader

   @FXML // fx:id="destinationAccount"
   private ComboBox<String> destinationAccount; // Value injected by FXMLLoader
   // End FXML bound controls set in scene builder
   /////////////////////////////////////////////////////////////////////////////

   /**
    * The message to display when the transfer has completed
    */
   private String transferMessage;

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
      destinationAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
      refresh();
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
    * KeyPress event handler for the transferAmount text box used to reset the inactivity timer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleKeyPressed(KeyEvent event) {
      resetTimer();
   }

   /**
    * Transfer button click handler. Resets the timer, validation user input, and directs the atm service to perform the
    * transfer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleTransferAction(ActionEvent event) {
      try {
         resetTimer();
         if (validateUserInput() == false) {
            return;
         }

         BankAccount source = null;
         if (fromAccount.getValue().equals(getTranslatedText("checking"))) {
            source = getAtmService().getCheckingAccount();
         } else {
            source = getAtmService().getSavingsAccount();
         }

         BankAccount destination = null;
         if (destinationAccount.getValue().equals(getTranslatedText("checking"))) {
            destination = getAtmService().getCheckingAccount();
         } else {
            destination = getAtmService().getSavingsAccount();
         }

         double amount = parseWithdrawalAmount(transferAmount.getText());
         AtmServiceError error = getAtmService().transfer(amount, source, destination);
         if (error != AtmServiceError.SUCCESS) {
            refresh();
            showError(error);
         } else {
            showConfirmationTransfer(transferMessage);
            showMainPage();
         }

      } catch (Exception e) {
         showError(e.getMessage());
      }
   }

   /**
    * Private method used to validate user input into controls from this page and display the necessary translated error
    * messages.
    *
    * @return true/false based on the status of the validation
    */
   private boolean validateUserInput() {
      double value = -1;
      try {

         if (fromAccount.getValue() == null) {
            String message = getTranslatedText("SOURCE_ACCOUNT_NOT_FOUND");
            showError(message);
            return false;
         }

         if (destinationAccount.getValue() == null) {
            String message = getTranslatedText("DESTINATION_ACCOUNT_NOT_FOUND");
            showError(message);
            return false;
         }

         value = parseWithdrawalAmount(transferAmount.getText());
      } catch (Exception e) {
         value = -1;
      }

      if (value <= 0) {
         String message = getTranslatedText("INVALID_DOLLAR_AMOUNT");
         showError(message);
      }

      return value > 0;
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
    * Selection changed event handler for the destinationAccount combobox control.
    *
    * @param event The action event instance
    */
   @FXML
   void handleToAccount(ActionEvent event) {
      resetTimer();
      String accountType = destinationAccount.getValue();
      if (accountType.equals(getTranslatedText("checking"))) {
         fromAccount.setValue(getTranslatedText("savings"));
      }
      if (accountType.equals(getTranslatedText("savings"))) {
         fromAccount.setValue(getTranslatedText("checking"));
      }
   }

   /**
    * Selection changed event handler for the fromAccount combobox control.
    *
    * @param event The action event instance
    */
   @FXML
   void handleFromAccount(ActionEvent event) {
      resetTimer();
      String accountType = fromAccount.getValue();
      if (accountType.equals(getTranslatedText("checking"))) {
         destinationAccount.setValue(getTranslatedText("savings"));
      }
      if (accountType.equals(getTranslatedText("savings"))) {
         destinationAccount.setValue(getTranslatedText("checking"));
      }
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
