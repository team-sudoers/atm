/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.IAtmDataAccess;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.datamodel.XmlDataAccess;

public class LocalAtmService implements IAtmService {

   private final IAtmDataAccess dataAccess;
   private UserAccount currentUser;

   public LocalAtmService() {
      dataAccess = new XmlDataAccess("sampledata.xml");
   }

   @Override
   public boolean login(String username, String pin) throws Exception {
      try {
         UserAccount foundAccount = null;

         List<UserAccount> userAccounts = dataAccess.getAllUserAccounts();
         String hashedPin = hashPassword("SHA-256", pin);
         for (UserAccount userAccount : userAccounts) {
            if (userAccount.getUserName().equals(username) && userAccount.getPin().equals(hashedPin)) {
               foundAccount = userAccount;
               break;
            }
         }

         if (foundAccount != null) {
            currentUser = foundAccount;
            return true;
         }

      } catch (NoSuchAlgorithmException ex) {
         Logger.getLogger(LocalAtmService.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
   }

   @Override
   public void logout() throws Exception {
      currentUser = null;
   }

   @Override
   public UserAccount getLoggedInUser() throws Exception {
      return currentUser;
   }

   @Override
   public BankAccount getCheckingAccount() throws Exception {
      List<BankAccount> allAccounts = dataAccess.findAllBankAccounts(currentUser);
      for (BankAccount account : allAccounts) {
         if (account.getAccountType().equals("checking")) {
            return account;
         }
      }
      throw new Exception("Checking account not found for user");
   }

   @Override
   public BankAccount getSavingsAccount() throws Exception {
      List<BankAccount> allAccounts = dataAccess.findAllBankAccounts(currentUser);
      for (BankAccount account : allAccounts) {
         if (account.getAccountType().equals("savings")) {
            return account;
         }
      }
      throw new Exception("Savings account not found for user");
   }

   @Override
   public List<Transaction> getAccountHistory() throws Exception {
      return dataAccess.getTransactionsForUser(currentUser);
   }

   @Override
   public Transaction getLastTransaction() throws Exception {

      Transaction retVal = new Transaction();
      retVal.setDate(new Date(Long.MIN_VALUE));

      List<Transaction> history = getAccountHistory();

      for (Transaction transaction : history) {
         if (transaction.getDate().after(retVal.getDate())) {
            retVal = transaction;
         }
      }

      return retVal;
   }

   @Override
   public void withdraw(double amount, BankAccount source) throws Exception {
      if (source.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      BankAccount verifiedAccount = null;
      List<BankAccount> allAccounts = dataAccess.findAllBankAccounts(currentUser);
      for (BankAccount account : allAccounts) {
         if (source.getAccountNumber() == account.getAccountNumber()) {
            verifiedAccount = account;
            break;
         }
      }
      
      if (verifiedAccount == null) {
         throw new Exception("Failed to find bank account");
      }
      
      double currentBalance = verifiedAccount.getAccountBalance();
      if (currentBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
      }

      currentBalance -= amount;
   }

   @Override
   public void transfer(double amount, BankAccount source, BankAccount destination) throws Exception {
      if (source.getUserId() != currentUser.getId()) {
         throw new Exception("This source bank account doesn't belong to you!");
      }

      if (destination.getUserId() != currentUser.getId()) {
         throw new Exception("This destination bank account doesn't belong to you!");
      }
      
      BankAccount verifiedSourceAccount = null;
      List<BankAccount> allAccounts = dataAccess.findAllBankAccounts(currentUser);
      for (BankAccount account : allAccounts) {
         if (source.getAccountNumber() == account.getAccountNumber()) {
            verifiedSourceAccount = account;
            break;
         }
      }
      
      if (verifiedSourceAccount == null) {
         throw new Exception("Failed to find bank account");
      }

      if (source.getAccountNumber() != checkingAccount.getAccountNumber()
              && source.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account");
      }

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account");
      }

      double sourceBalance = source.getAccountBalance();
      if (sourceBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
      }
      sourceBalance -= amount;

      double destinationBalance = destination.getAccountBalance();
      destinationBalance += amount;
   }

   @Override
   public void deposit(double amount, BankAccount toAccount) throws Exception {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private String hashPassword(String hashType, String password) throws NoSuchAlgorithmException {
      MessageDigest digest = MessageDigest.getInstance(hashType);
      byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
   }
}
