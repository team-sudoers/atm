/**
 * Sample Skeleton for 'TransferPage.fxml' Controller Class
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

public class TransferPageController extends BaseAtmController {

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

   @FXML // fx:id="sourceAcountTypeLabel"
   private Label sourceAcountTypeLabel; // Value injected by FXMLLoader

   @FXML // fx:id="selectAmountLabel"
   private Label selectAmountLabel; // Value injected by FXMLLoader

   @FXML // fx:id="fromCheckingButton"
   private Button fromCheckingButton; // Value injected by FXMLLoader

   @FXML // fx:id="fromSavingsButton"
   private Button fromSavingsButton; // Value injected by FXMLLoader

   @FXML // fx:id="transferAmount"
   private TextField transferAmount; // Value injected by FXMLLoader

   @FXML // fx:id="sourceAcountTypeLabel1"
   private Label sourceAcountTypeLabel1; // Value injected by FXMLLoader

   @FXML // fx:id="toCheckingButton"
   private Button toCheckingButton; // Value injected by FXMLLoader

   @FXML // fx:id="toSavingsButton"
   private Button toSavingsButton; // Value injected by FXMLLoader
   
   @FXML
    private ComboBox<String> fromAccount;

    @FXML
    private ComboBox<String> destinationAccount;
   
   @FXML
    private Button returnButton;

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
     fromAccount.getItems().addAll("checking", "savings");
     destinationAccount.getItems().addAll("checking", "savings");
      refresh();
   }

   @FXML
   void handleLogoutAction(ActionEvent event) {
      try {
         getAtmService().logout();
         showLoginPage();
      } catch (Exception ex) {
         Logger.getLogger(TransferPageController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @FXML
   void handleTransferAction(ActionEvent event) {
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

         BankAccount source = null;
         if (fromAccount.getValue().equals("checking")) {
            source = getAtmService().getCheckingAccount();
         } else {
            source = getAtmService().getSavingsAccount();
         }
         
         BankAccount destination = null;
         if (destinationAccount.getValue().equals("checking")) {
            destination = getAtmService().getCheckingAccount();
         } else {
            destination = getAtmService().getSavingsAccount();
         }

         double amount = parseWithdrawalAmount(transferAmount.getText());
         getAtmService().transfer(amount, source, destination);
         refresh();

      } catch (Exception e) {
         showError(e.getMessage());
      }
   }

   private boolean validateUserInput() {
      if (fromAccount.getValue() == null) {
         return false;
      }

      if (destinationAccount.getValue() == null) {
         return false;
      }

      return parseWithdrawalAmount(transferAmount.getText()) >= 0;
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
         Logger.getLogger(TransferPageController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   
    @FXML
    void handleToAccount(ActionEvent event) {
        String accountType = destinationAccount.getValue();
        if(accountType.equals("checking")){
            fromAccount.setValue("savings");
        } 
        if(accountType.equals("savings")){
            fromAccount.setValue("checking");
        }
    }
    
    @FXML
    void handleFromAccount(ActionEvent event) {
        String accountType = fromAccount.getValue();
        if(accountType.equals("checking")){
            destinationAccount.setValue("savings");
        } 
        if(accountType.equals("savings")){
            destinationAccount.setValue("checking");
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
