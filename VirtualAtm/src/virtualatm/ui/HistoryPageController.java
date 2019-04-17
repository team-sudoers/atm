/**
 * Sample Skeleton for 'HistoryPage.fxml' Controller Class
 */
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public class HistoryPageController extends BaseAtmController {

    @FXML // fx:id="returnButton"
    private Button returnButton; // Value injected by FXMLLoader

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

    @FXML // fx:id="accountHistoryLabel"
    private Label accountHistoryLabel; // Value injected by FXMLLoader

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

   }

   @FXML
   void handleReturnAction(ActionEvent event) {
      showMainPage();
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
      
      List<Transaction> history = getAtmService().getAccountHistory();
      
      dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
      typeColumn.setCellValueFactory(new PropertyValueFactory<>("activityType"));
      amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
      
      ObservableList<Transaction> data = FXCollections.observableArrayList(history);
      historyTableView.setItems(data);

   }
}
