/* 
 * File:    HistoryPageController.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 * The Account History page controller bound to the HistoryPage.fxml. Retrieves transaction history information from the
 * atm service and allows the user to view/sort transaction information from the past 7 days.
 */
public class HistoryPageController extends BaseAtmController {

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

   @FXML // fx:id="historyTableView"
   private TableView<TransactionHistory> historyTableView; // Value injected by FXMLLoader

   @FXML // fx:id="dateColumn"
   private TableColumn<TransactionHistory, Date> dateColumn; // Value injected by FXMLLoader

   @FXML // fx:id="typeColumn"
   private TableColumn<TransactionHistory, String> typeColumn; // Value injected by FXMLLoader

   @FXML // fx:id="amountColumn"
   private TableColumn<TransactionHistory, Double> amountColumn; // Value injected by FXMLLoader
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
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
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
    * Return button click handler. Shows the main page.
    *
    * @param event The action event instance
    */
   @FXML
   void handleReturnAction(ActionEvent event) {
      showMainPage();
   }

   /**
    * historyTableView scroll handler. Resets the timer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleScrollAction(ScrollEvent event) {
      resetTimer();
   }

   /**
    * historyTableView mouse click handler. Resets the timer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleMouseAction(MouseEvent event) {
      resetTimer();
   }

   /**
    * historyTableView column sort handler. Resets the timer.
    *
    * @param event The action event instance
    */
   @FXML
   void handleSortAction(ActionEvent event) {
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
         String pattern1 = "MM/dd/yyyy hh:mm:ss";
         SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);

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

         List<Transaction> history = getAtmService().getAccountHistory();
         List<TransactionHistory> transactionHistory = new ArrayList<TransactionHistory>();

         for (Transaction item : history) {
            TransactionHistory transactionHistory1 = new TransactionHistory();
            transactionHistory1.setTransactionDate(simpleDateFormat1.format(item.getDate()));
            transactionHistory1.setAmount(String.format("$%.02f", item.getAmount()));
            BankAccount bankAccountType = getAtmService().getBankAccount(item.getBankAccountId());
            transactionHistory1.setTransactionActivity(String.format("%s (%s)", getTranslatedText(item.getActivityType()), getTranslatedText(bankAccountType.getAccountType())));
            transactionHistory.add(transactionHistory1);
         }
         dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
         typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionActivity"));
         amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

         ObservableList<TransactionHistory> data = FXCollections.observableArrayList(transactionHistory);
         historyTableView.setItems(data);
      } catch (Exception ex) {
         super.showError(ex.getMessage());
      }

   }

   /**
    * Transaction history class used internally by this class to format transaction history data in a user friendly manner.
    */
   public class TransactionHistory {

      private String transactionDate;
      private String transactionActivity;
      private String amount;

      /**
       * @return the transactionDate
       */
      public String getTransactionDate() {
         return transactionDate;
      }

      /**
       * @param transactionDate the transactionDate to set
       */
      public void setTransactionDate(String transactionDate) {
         this.transactionDate = transactionDate;
      }

      /**
       * @return the transactionActivity
       */
      public String getTransactionActivity() {
         return transactionActivity;
      }

      /**
       * @param transactionActivity the transactionActivity to set
       */
      public void setTransactionActivity(String transactionActivity) {
         this.transactionActivity = transactionActivity;
      }

      /**
       * @return the amount
       */
      public String getAmount() {
         return amount;
      }

      /**
       * @param amount the amount to set
       */
      public void setAmount(String amount) {
         this.amount = amount;
      }

   }
}
