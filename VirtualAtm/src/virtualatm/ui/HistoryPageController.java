/* 
 * File:    HistoryPageController.java
 * Date:    04/27/2019
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

public class HistoryPageController extends BaseAtmController {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
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
    void handleReturnAction(ActionEvent event) {
        showMainPage();
    }

    @FXML
    void handleScrollAction(ScrollEvent event) {
        resetTimer();
    }

    @FXML
    void handleMouseAction(MouseEvent event) {
        resetTimer();
    }
    
    @FXML
    void handleSortAction(ActionEvent event) {
        resetTimer();
    }
    
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
            
            for(Transaction item : history) {
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