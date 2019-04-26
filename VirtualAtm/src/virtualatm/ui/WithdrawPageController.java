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

public class WithdrawPageController extends BaseAtmController {

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

   private String selectedAccountType;
   private double withdrawAmount;

   @FXML
   private ComboBox<String> fromAccount;

   @FXML
   private ComboBox<String> selectAmount;

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
      fromAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
      selectAmount.getItems().addAll("20", "40", "60", "80", "100", "200");
      refresh();
   }

   @FXML
   void handleConfirmAction(ActionEvent event) {
      try {
         if (validateUserInput() == false) {
            String message = getTranslatedText("INVALID_DOLLAR_AMOUNT");
            showError(message);
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
            showMainPage();
         }

      } catch (Exception e) {
         showError(e.getMessage());
      }
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
   void handleSelectAccountChange(ActionEvent event) {
      String accountType = fromAccount.getValue();
      if (accountType != null) {
         selectedAccountType = accountType;
      }
   }

   @FXML
   void handleSelectionAmountChanged(ActionEvent event) {
      String value = selectAmount.getValue();
      if (value != null) {
         withdrawAmount = Double.valueOf(value);
      }
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

   private boolean validateUserInput() {

      double tempAmount = withdrawAmount;
      if (tempAmount <= 0) {
         tempAmount = parseWithdrawalAmount(otherDepositAmount.getText());
      }

      return tempAmount > 0;
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
