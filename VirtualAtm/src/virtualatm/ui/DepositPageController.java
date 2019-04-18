/**
 * Sample Skeleton for 'DepositPage.fxml' Controller Class
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public class DepositPageController extends BaseAtmController {

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

   @FXML // fx:id="depositButton"
   private Button depositButton; // Value injected by FXMLLoader

   @FXML // fx:id="depositAcountTypeLabel"
   private Label depositAcountTypeLabel; // Value injected by FXMLLoader

   @FXML // fx:id="enterAmountLabel"
   private Label enterAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="checkingButton"
   private Button checkingButton; // Value injected by FXMLLoader

   @FXML // fx:id="SavingsButton"
   private Button SavingsButton; // Value injected by FXMLLoader

   @FXML // fx:id="depositAmount"
   private TextField depositAmount; // Value injected by FXMLLoader
   private String selectedAccountType;
  
   @FXML
    private Button returnButton;
   
   @FXML
   private ComboBox<String> fromAccount;
   

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
      fromAccount.getItems().addAll("checking", "savings");
      refresh();
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
         Logger.getLogger(DepositPageController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

     @FXML
    void handleDepositAccountType(ActionEvent event) {
         String accountType = fromAccount.getValue();
        if (accountType != null){
            selectedAccountType = accountType;
        }
    }
   
   @FXML
   void handleCheckingSelectedAction(ActionEvent event) {
      selectedAccountType = "checking";
   }

   @FXML
   void handleDepositAction(ActionEvent event) {
      try {
         if (validateUserInput() == false) {
            showError("Please enter the require information");
            return;
         }

         UserAccount user = getAtmService().getLoggedInUser();
         if (user == null) {
            showError("You must login to perform deposits");
            showLoginPage();
            return;
         }

         BankAccount ba = null;
         if (selectedAccountType.equals("checking")) {
            ba = getAtmService().getCheckingAccount();
         } else {
            ba = getAtmService().getSavingsAccount();
         }

         double amount = parseDepositAmount(depositAmount.getText());
         getAtmService().deposit(amount, ba);
         refresh();

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
         Logger.getLogger(DepositPageController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @FXML
   void handleSavingsSelectedAction(ActionEvent event) {
      selectedAccountType = "savings";
   }

   private boolean validateUserInput() {
      if (selectedAccountType.length() <= 0) {
         return false;
      }

      return parseDepositAmount(depositAmount.getText()) >= 0;
   }

   private double parseDepositAmount(String text) {

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
