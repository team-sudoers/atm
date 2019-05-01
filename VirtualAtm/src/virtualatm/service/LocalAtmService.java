/* 
 * File:    LocalAtmService.java
 * Date:    04/27/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import virtualatm.dataaccess.IAtmDataAccess;
import virtualatm.dataaccess.XmlDataAccess;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.utils.Security;

public class LocalAtmService implements IAtmService {

   private final int MAX_FAILED_LOGINS = 3;
   private final int LOCKOUT_SECONDS = 30;

   private final IAtmDataAccess dataAccessLayer;
   private UserAccount currentUser;

   public LocalAtmService() {
      dataAccessLayer = new XmlDataAccess();
   }

   @Override
   public AtmServiceError withdraw(final double amount, final BankAccount account) {

      if (account.getUserId() != currentUser.getId()) {
         return AtmServiceError.ACCOUNT_NOT_OWNED;
      }

      BankAccount fromAccount = dataAccessLayer.findBankAccount(account.getAccountNumber());
      if (fromAccount == null) {
         return AtmServiceError.BANK_ACCOUNT_NOT_FOUND;
      }

      double currentBalance = fromAccount.getAccountBalance();
      if (currentBalance < amount) {
         return AtmServiceError.INSUFFICIENT_FUNDS;
      }

      double balance = fromAccount.getAccountBalance();
      balance -= amount;
      fromAccount.setAccountBalance(balance);

      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(account.getAccountNumber());
      withdrawTransaction.setDate(new Date());

      return completeTransaction(account, withdrawTransaction);
   }

   @Override
   public AtmServiceError transfer(final double amount, final BankAccount source, final BankAccount destination) {

      if (source.getUserId() != currentUser.getId()) {
         return AtmServiceError.SOURCE_BANK_ACCOUNT_NOT_OWNED;
      }

      if (destination.getUserId() != currentUser.getId()) {
         return AtmServiceError.DESTINATION_BANK_ACCOUNT_NOT_OWNED;
      }

      BankAccount sourceAccount = dataAccessLayer.findBankAccount(source.getAccountNumber());
      if (sourceAccount == null) {
         return AtmServiceError.SOURCE_ACCOUNT_NOT_FOUND;
      }

      BankAccount destAccount = dataAccessLayer.findBankAccount(destination.getAccountNumber());
      if (destAccount == null) {
         return AtmServiceError.DESTINATION_ACCOUNT_NOT_FOUND;
      }

      double sourceBalance = sourceAccount.getAccountBalance();
      if (sourceBalance < amount) {
         return AtmServiceError.INSUFFICIENT_FUNDS;
      }
      sourceBalance -= amount;
      sourceAccount.setAccountBalance(sourceBalance);
      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(sourceAccount.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      if (completeTransaction(sourceAccount, withdrawTransaction) != AtmServiceError.SUCCESS) {
         return AtmServiceError.FAILURE_SAVING_TRANSACTION;
      }

      double destinationBalance = destination.getAccountBalance();
      destinationBalance += amount;
      destination.setAccountBalance(destinationBalance);
      Transaction depositTransaction = new Transaction();
      depositTransaction.setActivityType("Deposit");
      depositTransaction.setAmount(amount);
      depositTransaction.setBankAccountId(destination.getAccountNumber());
      depositTransaction.setDate(new Date());
      
      return completeTransaction(destAccount, depositTransaction);
   }

   @Override
   public AtmServiceError deposit(double amount, BankAccount destination) {

      if (destination.getUserId() != currentUser.getId()) {
         return AtmServiceError.ACCOUNT_NOT_OWNED;
      }

      BankAccount foundAccount = dataAccessLayer.findBankAccount(destination.getAccountNumber());
      if (foundAccount == null) {
         return AtmServiceError.USER_ACCOUNT_NOT_FOUND;
      }

      double balance = foundAccount.getAccountBalance();
      balance += amount;
      foundAccount.setAccountBalance(balance);

      Transaction transaction = new Transaction();
      transaction.setActivityType("Deposit");
      transaction.setAmount(amount);
      transaction.setBankAccountId(destination.getAccountNumber());
      transaction.setDate(new Date());
      
      return completeTransaction(foundAccount, transaction);
   }

   @Override
   public List<Transaction> getAccountHistory() {

      // we have a hard coded 7 days of history right now
      List<Transaction> retVal = new ArrayList<>();
      Date date = Date.from(Instant.now().minus(Duration.ofDays(7)));

      for (Transaction transaction : dataAccessLayer.getTransactionsForUser(currentUser)) {
         if (transaction.getDate().after(date)) {
            retVal.add(transaction);
         }
      }

      return retVal;
   }

   @Override
   public AtmServiceError login(String username, String pin) {

      if (currentUser != null) {
         return AtmServiceError.LOGOUT_REMINDER;
      }

      UserAccount foundAccount = null;
      for (UserAccount userAccount : dataAccessLayer.getAllUserAccounts()) {
         if (userAccount.getUserName().equals(username)) {
            foundAccount = userAccount;
            break;
         }
      }

      if (foundAccount == null) {
         return AtmServiceError.USER_ACCOUNT_NOT_FOUND;
      }

      if (foundAccount.getFailedLoginCount() >= MAX_FAILED_LOGINS) {
         long unlockTime = foundAccount.getLastFailedLogin().getTime() + LOCKOUT_SECONDS * 1000;
         if (System.currentTimeMillis() < unlockTime) {
            return AtmServiceError.USER_ACCOUNT_LOCKED;
         }
      }

      try {
         if (Security.compareHash(foundAccount.getPin(), pin) == false) {
            foundAccount.setFailedLoginCount(foundAccount.getFailedLoginCount() + 1);
            foundAccount.setLastFailedLogin(new Date());
            return AtmServiceError.INVALID_USER_CREDENTIALS;
         }
      } catch (Exception ex) {
         return AtmServiceError.INVALID_USER_CREDENTIALS;
      }

      // successful login
      foundAccount.setFailedLoginCount(0);
      currentUser = foundAccount;
      return AtmServiceError.SUCCESS;
   }

   @Override
   public AtmServiceError logout() {
      currentUser = null;
      return AtmServiceError.SUCCESS;
   }

   @Override
   public UserAccount getLoggedInUser() {
      return currentUser;
   }

   @Override
   public BankAccount getCheckingAccount() {

      List<BankAccount> accounts = dataAccessLayer.findAllBankAccounts(currentUser);
      for (BankAccount account : accounts) {
         if (account.getAccountType().toLowerCase().equals("checking")) {
            return account;
         }
      }

      return new BankAccount();
   }

   @Override
   public BankAccount getSavingsAccount() {

      List<BankAccount> accounts = dataAccessLayer.findAllBankAccounts(currentUser);
      for (BankAccount account : accounts) {
         if (account.getAccountType().toLowerCase().equals("savings")) {
            return account;
         }
      }

      return new BankAccount();

   }

   @Override
   public Transaction getLastTransaction() {

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
   
      
   private AtmServiceError completeTransaction(BankAccount ba, Transaction transaction) {
      if (dataAccessLayer.addTransaction(transaction) == false) {
         return AtmServiceError.FAILURE_SAVING_TRANSACTION;
      }
      
      if (dataAccessLayer.updateBankAccount(ba) == false) {
         dataAccessLayer.deleteTransaction(transaction);
         return AtmServiceError.FAILURE_SAVING_TRANSACTION;
      }
      
      return AtmServiceError.SUCCESS;
   }

    @Override
    public BankAccount getBankAccount(long countId) {
        return dataAccessLayer.findBankAccount(countId);
    }
}
