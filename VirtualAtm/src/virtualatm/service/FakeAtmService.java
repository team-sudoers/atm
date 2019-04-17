/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public class FakeAtmService implements IAtmService {

   private final List<UserAccount> userAccounts;
   private final List<Transaction> transactions;
   private final BankAccount checkingAccount;
   private final BankAccount savingsAccount;

   private UserAccount currentUser;

   public FakeAtmService() {
      
      userAccounts = new ArrayList<>();
      transactions = new ArrayList<>();

      UserAccount userAccount = new UserAccount();
      userAccount.setId(1);
      userAccount.setFirstName("santa");
      userAccount.setLastName("claus");
      userAccount.setCellNumber("321-555-1212");
      userAccount.setEmail("sc@northpole.com");
      userAccount.setUserName("sc");
      userAccount.setPin("12345");
      userAccounts.add(userAccount);

      checkingAccount = new BankAccount();
      checkingAccount.setUserId(1);
      checkingAccount.setAccountType("checking");
      checkingAccount.setAccountNumber(11111111);
      checkingAccount.setAccountBalance(1.11);

      savingsAccount = new BankAccount();
      savingsAccount.setUserId(1);
      savingsAccount.setAccountType("savings");
      savingsAccount.setAccountNumber(22222222);
      savingsAccount.setAccountBalance(2.22);

      Transaction checkingTransaction = new Transaction();
      checkingTransaction.setActivityType("Deposit");
      checkingTransaction.setAmount(1.11);
      checkingTransaction.setBankAccountId(11111111);
      checkingTransaction.setDate(new Date());
      transactions.add(checkingTransaction);

      Transaction savingsTransaction = new Transaction();
      savingsTransaction.setActivityType("Deposit");
      savingsTransaction.setAmount(2.22);
      savingsTransaction.setBankAccountId(22222222);
      savingsTransaction.setDate(new Date());
      transactions.add(savingsTransaction);
   }

   @Override
   public void withdraw(double amount, BankAccount account) throws Exception {
      
      if (account.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      if (account.getAccountNumber() != checkingAccount.getAccountNumber()
              && account.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account");
      }

      double currentBalance = account.getAccountBalance();
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
   public void deposit(double amount, BankAccount destination) throws Exception {

      if (destination.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account");
      }

      double balance = destination.getAccountBalance();
      balance += amount;
      destination.setAccountBalance(balance);
   }

   @Override
   public List<Transaction> getAccountHistory() throws Exception {
      
      List<Transaction> retVal = new ArrayList<>();
      if (savingsAccount.getUserId() != currentUser.getId()) {
         return retVal;
      }

      if (checkingAccount.getUserId() != currentUser.getId()) {
         return retVal;
      }
      
      for (Transaction transaction : transactions) {
         if (transaction.getBankAccountId() == checkingAccount.getAccountNumber()
                 || transaction.getBankAccountId() == savingsAccount.getAccountNumber()) {
            retVal.add(transaction);
         }
      }
      
      return retVal;
   }

   @Override
   public boolean login(String username, String pin) throws Exception {
      
      UserAccount foundAccount = null;

      for (UserAccount userAccount : userAccounts) {
         if (userAccount.getUserName().equals(username) && userAccount.getPin().equals(pin)) {
            foundAccount = userAccount;
            break;
         }
      }

      if (foundAccount == null) {
         return false;
      }

      currentUser = foundAccount;
      return true;
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

      if (checkingAccount.getUserId() != currentUser.getId()) {
         return new BankAccount();
      }

      return checkingAccount;
   }

   @Override
   public BankAccount getSavingsAccount() throws Exception {

      if (savingsAccount.getUserId() != currentUser.getId()) {
         return new BankAccount();
      }

      return savingsAccount;
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

}
