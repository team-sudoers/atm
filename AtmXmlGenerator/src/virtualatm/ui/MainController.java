/* 
 * File:    MainController.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: AtmXmlGenerator
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import virtualatm.dataaccess.IAtmDataAccess;
import virtualatm.dataaccess.XmlDataAccess;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.utils.Security;

/**
 * The Main page controller bound to the Main.fxml. Captures all user input and handles user feedback for creating a xml
 * file based data store.
 */
public class MainController implements Initializable {

   /////////////////////////////////////////////////////////////////////////////
   // Begin FXML bound controls set in scene builder
   @FXML
   private TextField firstName;
   @FXML
   private TextField lastName;
   @FXML
   private TextField cellNumber;
   @FXML
   private TextField email;
   @FXML
   private TextField userName;
   @FXML
   private TextField password;
   @FXML
   private TextField checkingAccountNumber;
   @FXML
   private TextField checkingAccountBalance;
   @FXML
   private TextField savingsAccountNumber;
   @FXML
   private TextField savingsAccountBalance;
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
   }

   /**
    * Create test data button click handler. Generates the xml file based on our initial project plan.
    *
    * @param event The action event instance
    */
   @FXML
   public void handleCreateTestDataAction(ActionEvent event) {
      try {

         XmlDataAccess dataAccess = new XmlDataAccess("testdata.xml");
         long counter = 0;

         createUser(++counter, "Raysean", "Jones-Dent", "301-300-3001", "rjd@umuc.edu", "rjd", "1234", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 500.00, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 500.00, dataAccess);

         createUser(++counter, "Tonye", "Martial", "301-300-3002", "tm@umuc.edu", "tm", "pa55w0rd", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 0.00, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 8000.00, dataAccess);

         createUser(++counter, "Matt", "Mitchell", "301-300-3003", "mm@umuc.edu", "mm", "mmm7615", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 0.00, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 0.00, dataAccess);

         createUser(++counter, "Kristine", "Dudley", "301-300-3004", "kd@umuc.edu", "kd", "kd6143", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 50000.00, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 1000000.00, dataAccess);

         createUser(++counter, "Choi", "Woo", "301-300-3005", "wc@umuc.edu", "wc", "cw888", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 88642.00, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 0.50, dataAccess);

         createUser(++counter, "Kim", "Justin", "301-300-3006", "jk@umuc.edu", "jk", "jdk8849", dataAccess);
         createBankAccount(counter, 10000000 + counter, "checking", 0.50, dataAccess);
         createBankAccount(counter, 20000000 + counter, "saving", 0.84, dataAccess);

      } catch (Exception ex) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * Private method to create a bank account and deposit transaction in the xml file
    *
    * @param userId The user id associated with the bank account
    * @param accountNumber The account number to assign
    * @param accountType The account type to assign
    * @param accountBalance The account balanace to assign
    * @param da The xml data access instance used to save the bank account information
    */
   private void createBankAccount(long userId, long accountNumber, String accountType, double accountBalance, XmlDataAccess da) {
      BankAccount account = new BankAccount();
      account.setAccountNumber(accountNumber);
      account.setAccountType(accountType);
      account.setUserId(userId);
      account.setAccountBalance(accountBalance);
      da.addBankAccount(account);

      if (accountBalance > 0) {
         Transaction transaction = new Transaction();
         transaction.setActivityType("Deposit");
         transaction.setAmount(accountBalance);
         transaction.setBankAccountId(accountNumber);
         transaction.setDate(new Date());
         da.addTransaction(transaction);
      }
   }

   /**
    * Private method to create a user account in the xml file
    * @param userId The id of the new user
    * @param firstName The first name of the new user
    * @param lastName The last name of the new user
    * @param cellNumber The cell number of the new user
    * @param email The email account of the new user
    * @param userName The user name of the new user
    * @param pin The pin of the new user
    * @param da The xml data access instance used to save the user account information
    * @throws Exception
    */
   private void createUser(long userId, String firstName, String lastName, String cellNumber, String email, String userName, String pin, XmlDataAccess da) throws Exception {
      UserAccount userAccount = new UserAccount();
      userAccount.setId(userId);
      userAccount.setFirstName(firstName);
      userAccount.setLastName(lastName);
      userAccount.setCellNumber(cellNumber);
      userAccount.setEmail(email);
      userAccount.setUserName(userName);
      userAccount.setPin(Security.createHash(pin));

      da.addUserAccount(userAccount);
   }

   /**
    * Create user and transaction button click handler. Adds information from the user interfaces controls to the xml file.
    *
    * @param event The action event instance
    */
   @FXML
   public void handleAddButtonAction(ActionEvent event) {
      try {

         XmlDataAccess dataAccess = new XmlDataAccess("sampledata.xml");
         dataAccess.load();

         StringBuilder error = new StringBuilder();
         if (validateUserInput(error, dataAccess) == false) {
            Alert msgbox = new Alert(Alert.AlertType.ERROR, error.toString());
            msgbox.setHeaderText("Add Error");
            msgbox.showAndWait();
            return;
         }

         long nextUserId = dataAccess.getAllUserAccounts().size() + 1;
         long nextTransactionId = dataAccess.getAllTransactions().size() + 1;

         String strFirstName = firstName.getText();
         String strLastName = lastName.getText();
         String strCellNumber = cellNumber.getText();
         String strEmail = email.getText();
         String strUserName = userName.getText();
         String strPassword = Security.createHash(password.getText());
         String strCheckingAccount = checkingAccountNumber.getText();
         String strSavingsAccount = savingsAccountNumber.getText();
         String strCheckingBalance = checkingAccountBalance.getText();
         String strSavingsBalance = savingsAccountBalance.getText();

         // setup user account
         UserAccount userAccount = new UserAccount();
         userAccount.setId(nextUserId);
         userAccount.setFirstName(strFirstName);
         userAccount.setLastName(strLastName);
         userAccount.setCellNumber(strCellNumber);
         userAccount.setEmail(strEmail);
         userAccount.setUserName(strUserName);
         userAccount.setPin(strPassword);
         dataAccess.addUserAccount(userAccount);

         // setup checking account
         double checkingBalance = Double.valueOf(strCheckingBalance);
         BankAccount checkingAccount = new BankAccount();
         checkingAccount.setAccountType("checking");
         checkingAccount.setUserId(nextUserId);
         checkingAccount.setAccountNumber(Long.parseLong(strCheckingAccount));
         checkingAccount.setAccountBalance(checkingBalance);
         dataAccess.addBankAccount(checkingAccount);

         if (checkingBalance > 0) {
            Transaction checkingTransaction = new Transaction();
            checkingTransaction.setId(nextTransactionId++);
            checkingTransaction.setActivityType("Deposit");
            checkingTransaction.setAmount(checkingAccount.getAccountBalance());
            checkingTransaction.setBankAccountId(checkingAccount.getAccountNumber());
            checkingTransaction.setDate(new Date());
            dataAccess.addTransaction(checkingTransaction);
         }

         // setup savings account
         double savingsBalance = Double.valueOf(strSavingsBalance);
         BankAccount savingsAccount = new BankAccount();
         savingsAccount.setAccountType("savings");
         savingsAccount.setUserId(nextUserId);
         savingsAccount.setAccountNumber(Long.parseLong(strSavingsAccount));
         savingsAccount.setAccountBalance(Double.valueOf(strSavingsBalance));
         dataAccess.addBankAccount(savingsAccount);

         if (savingsBalance > 0) {
            Transaction savingsTransaction = new Transaction();
            savingsTransaction.setId(nextTransactionId++);
            savingsTransaction.setActivityType("Deposit");
            savingsTransaction.setAmount(savingsBalance);
            savingsTransaction.setBankAccountId(savingsAccount.getAccountNumber());
            savingsTransaction.setDate(new Date());
            dataAccess.addTransaction(savingsTransaction);
         }

         // save the xml file
         if (dataAccess.save(false) == true) {
            Alert msgbox = new Alert(Alert.AlertType.INFORMATION, "Saved user information");
            msgbox.showAndWait();
         }

      } catch (Exception e) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
      }

   }

   /**
    * Private method use to find any errors in the user input before adding information to the xml file
    * @param error The string builder instance to store any errors found
    * @param dataAccess The data access instance used to verify conflicts don't exist with existing information
    * @return true/false based upon the result of the validation
    */
   private boolean validateUserInput(StringBuilder error, IAtmDataAccess dataAccess) {
      if (firstName.getText().isEmpty()) {
         error.append("First Name cannot be empty!");
         return false;
      }

      if (lastName.getText().isEmpty()) {
         error.append("Last Name cannot be empty!");
         return false;
      }

      if (cellNumber.getText().isEmpty()) {
         error.append("Cell number cannot be empty!");
         return false;
      }

      if (email.getText().isEmpty()) {
         error.append("Email cannot be empty!");
         return false;
      }

      if (userName.getText().isEmpty()) {
         error.append("User Name cannot be empty!");
         return false;
      }

      if (dataAccess.findUserAccount(userName.getText()) != null) {
         error.append("User Name already exists!");
         return false;
      }

      if (password.getText().isEmpty()) {
         error.append("Password cannot be empty!");
         return false;
      }

      if (checkingAccountNumber.getText().isEmpty()) {
         error.append("Checking account number cannot be empty!");
         return false;
      }

      if (dataAccess.findBankAccount(Long.parseLong(checkingAccountNumber.getText())) != null) {
         error.append("Checking account number already exists!");
         return false;
      }

      if (checkingAccountBalance.getText().isEmpty()) {
         error.append("Checking account balance cannot be empty!");
         return false;
      }

      if (savingsAccountNumber.getText().isEmpty()) {
         error.append("Savings account number cannot be empty!");
         return false;
      }

      if (dataAccess.findBankAccount(Long.parseLong(savingsAccountNumber.getText())) != null) {
         error.append("Savings account number already exists!");
         return false;
      }

      if (savingsAccountBalance.getText().isEmpty()) {
         error.append("Savings account balance cannot be empty!");
         return false;
      }

      error.append("");
      return true;
   }
}
