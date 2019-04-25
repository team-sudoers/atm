package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
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
   private TableView<Transaction> historyTableView; // Value injected by FXMLLoader

   @FXML // fx:id="dateColumn"
   private TableColumn<Transaction, Date> dateColumn; // Value injected by FXMLLoader

   @FXML // fx:id="typeColumn"
   private TableColumn<Transaction, String> typeColumn; // Value injected by FXMLLoader

   @FXML // fx:id="amountColumn"
   private TableColumn<Transaction, Double> amountColumn; // Value injected by FXMLLoader

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

         List<Transaction> history = getAtmService().getAccountHistory();

         dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
         typeColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));
         amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

         ObservableList<Transaction> data = FXCollections.observableArrayList(history);
         historyTableView.setItems(data);
      } catch (Exception ex) {
         super.showError(ex.getMessage());
      }

   }
}
