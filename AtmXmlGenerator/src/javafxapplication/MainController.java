/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication;

import atm.datamodel.BankAccount;
import atm.datamodel.Transaction;
import atm.datamodel.UserAccount;
import atm.datamodel.XmlDataAccess;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Matt
 */
public class MainController implements Initializable {

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
   private TextField ssn;
   @FXML
   private TextField checkingAccountNumber;
   @FXML
   private TextField checkingAccountBalance;
   @FXML
   private TextField savingsAccountNumber;
   @FXML
   private TextField savingsAccountBalance;

   XmlDataAccess dataAccess;

   /**
    * Initializes the controller class.
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      // TODO
      dataAccess = new XmlDataAccess("sampledata.xml");

   }

   @FXML
   public void handleAddButtonAction(ActionEvent event) {

      StringBuilder error = new StringBuilder();
      if (validateUserInput(error) == false) {
         Alert msgbox = new Alert(Alert.AlertType.ERROR, error.toString());
         msgbox.setHeaderText("Add Error");
         msgbox.showAndWait();
         return;
      }

      try {

         dataAccess.Load();
         long nextUserId = dataAccess.getAllUserAccounts().size() + 1;

         String strFirstName = firstName.getText();
         String strLastName = lastName.getText();
         String strCellNumber = cellNumber.getText();
         String strEmail = email.getText();
         String strUserName = userName.getText();
         String strPassword = hashPassword("SHA-256", password.getText());
         String strSocialSecurityNumber = ssn.getText();
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
         userAccount.setPassword(strPassword);
         userAccount.setSocialSecurityNumber(strSocialSecurityNumber);
         dataAccess.addUserAccount(userAccount);

         // setup checking account
         double checkingBalance = Double.valueOf(strCheckingBalance);
         BankAccount checkingAccount = new BankAccount();
         checkingAccount.setUserId(nextUserId);
         checkingAccount.setAccountNumber(Long.parseLong(strCheckingAccount));
         checkingAccount.setAccountBalance(checkingBalance);
         dataAccess.addBankAccount(checkingAccount);
         
         if (checkingBalance > 0) {
            Transaction checkingTransaction = new Transaction();
            checkingTransaction.setActivityType("Deposit");
            checkingTransaction.setAmount(checkingAccount.getAccountBalance());
            checkingTransaction.setBankAccountId(checkingAccount.getAccountNumber());
            checkingTransaction.setDate(new Date());
            dataAccess.addTransaction(checkingTransaction);
         }

         // setup savings account
         double savingsBalance = Double.valueOf(strSavingsBalance);
         BankAccount savingsAccount = new BankAccount();
         savingsAccount.setUserId(nextUserId);
         savingsAccount.setAccountNumber(Long.parseLong(strSavingsAccount));
         savingsAccount.setAccountBalance(Double.valueOf(strSavingsBalance));
         dataAccess.addBankAccount(savingsAccount);
         
         if (savingsBalance > 0) {
            Transaction savingsTransaction = new Transaction();
            savingsTransaction.setActivityType("Deposit");
            savingsTransaction.setAmount(savingsBalance);
            savingsTransaction.setBankAccountId(savingsAccount.getAccountNumber());
            savingsTransaction.setDate(new Date());   
            dataAccess.addTransaction(savingsTransaction);
         }

         // save the xml file
         if (dataAccess.Save(false) == true) {
            Alert msgbox = new Alert(Alert.AlertType.INFORMATION, "Saved user information");
            msgbox.showAndWait();
         }

      } catch (Exception e) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
      }

   }
   
   private String hashPassword(String hashType, String password) throws NoSuchAlgorithmException {
      MessageDigest digest = MessageDigest.getInstance(hashType);
      byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
   }
   
   private boolean validateUserInput(StringBuilder error) {
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

      if (ssn.getText().isEmpty()) {
         error.append("Last four of SSN cannot be empty!");
         return false;
      }

      if (checkingAccountNumber.getText().isEmpty()) {
         error.append("Checking account number cannot be empty!");
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

      if (savingsAccountBalance.getText().isEmpty()) {
         error.append("Savings account balance cannot be empty!");
         return false;
      }

      error.append("");
      return true;
   }
}
