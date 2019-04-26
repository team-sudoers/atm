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
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.service.AtmServiceError;

public class TransferPageController extends BaseAtmController {

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

   @FXML
   private ComboBox<String> fromAccount;

   @FXML
   private ComboBox<String> destinationAccount;

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
      fromAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
      destinationAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
      refresh();
   }

   @FXML
   void handleLogoutAction(ActionEvent event) {
      try {
         getAtmService().logout();
         showLoginPage();
      } catch (Exception ex) {
         super.showError(ex.getMessage());
      }
   }

   @FXML
   void handleTransferAction(ActionEvent event) {
      try {
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
            showMainPage();
         }

      } catch (Exception e) {
         showError(e.getMessage());
      }
   }

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

   @FXML
   void handleToAccount(ActionEvent event) {
      String accountType = destinationAccount.getValue();
      if (accountType.equals(getTranslatedText("checking"))) {
         fromAccount.setValue(getTranslatedText("savings"));
      }
      if (accountType.equals(getTranslatedText("savings"))) {
         fromAccount.setValue(getTranslatedText("checking"));
      }
   }

   @FXML
   void handleFromAccount(ActionEvent event) {
      String accountType = fromAccount.getValue();
      if (accountType.equals(getTranslatedText("checking"))) {
         destinationAccount.setValue(getTranslatedText("savings"));
      }
      if (accountType.equals(getTranslatedText("savings"))) {
         destinationAccount.setValue(getTranslatedText("checking"));
      }
   }

   private double parseWithdrawalAmount(String text) {

      if (text.length() <= 0) {
         return 0.0;
      }

      if (text.startsWith("$")) {
         text = text.substring(1);
      }

      return Double.parseDouble(text);
   }

   @FXML
   void handleReturnAction(ActionEvent event) {
      showMainPage();
   }
}
