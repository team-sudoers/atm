/* 
 * File:    LocalAtmService.java
 * Date:    05/03/2019
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

/**
 * An ATM service implementation that maintains state in a local data store.
 */
public class LocalAtmService implements IAtmService {

   /**
    * Constant for the maximum failed login attempts allowed before a user account is locked
    */
   private final int MAX_FAILED_LOGINS = 3;

   /**
    * Constant for the amount of time an account is locked
    */
   private final int LOCKOUT_SECONDS = 30;

   /**
    * Constant for the per user maximum daily withdraw limit
    */
   private final double MAX_DAILY_WITHDRAW_LIMIT = 600.00;

   /**
    * The object used to retrieve information from the data access layer
    */
   private final IAtmDataAccess dataAccessLayer;

   /**
    * The active user of the service
    */
   private UserAccount currentUser;

   /**
    * Default constructor
    */
   public LocalAtmService() {
      dataAccessLayer = new XmlDataAccess();
   }

   /**
    * Attempts to withdraw the specified amount from a bank account
    *
    * @param amount The amount to withdraw
    * @param source The account to withdraw from
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public AtmServiceError withdraw(final double amount, final BankAccount source) {

      if (source.getUserId() != currentUser.getId()) {
         return AtmServiceError.ACCOUNT_NOT_OWNED;
      }

      BankAccount fromAccount = dataAccessLayer.findBankAccount(source.getAccountNumber());
      if (fromAccount == null) {
         return AtmServiceError.BANK_ACCOUNT_NOT_FOUND;
      }

      if (amount > MAX_DAILY_WITHDRAW_LIMIT) {
         return AtmServiceError.WITHDRAWAL_AMOUNT_EXCEEDS_LIMIT;
      }

      Date date = Date.from(Instant.now().minus(Duration.ofDays(1)));
      double totalTransAmount = amount;
      for (Transaction t : dataAccessLayer.getTransactionsForUser(currentUser)) {
         if ((t.getDate().after(date))
                 && (t.getBankAccountId() == fromAccount.getAccountNumber())
                 && (t.getActivityType().equalsIgnoreCase("Withdraw"))) {
            totalTransAmount += t.getAmount();
         }
      }

      if (totalTransAmount > MAX_DAILY_WITHDRAW_LIMIT) {
         return AtmServiceError.DAILY_WITHDRAWAL_LIMIT_EXCEEDED;
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
      withdrawTransaction.setBankAccountId(source.getAccountNumber());
      withdrawTransaction.setDate(new Date());

      return completeTransaction(source, withdrawTransaction);
   }

   /**
    * Attempts to transfer the specified amount between accounts withdrawing from the source and depositing into the
    * destination.
    *
    * @param amount The amount to transfer
    * @param source The source account to withdraw from
    * @param destination The destination account to deposit into
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Attempts to deposit the specified amount into a destination account
    *
    * @param amount The amount to deposit
    * @param destination The account to deposit into
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Gets the transaction history for the current user
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Logs a user into the application if the provided user name and pin match an existing user account.
    *
    * @param username The user name of the account holder
    * @param pin The pin of the account holder
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Logs the active user out of the application
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public AtmServiceError logout() {
      currentUser = null;
      return AtmServiceError.SUCCESS;
   }

   /**
    * Gets the currently logged in user information
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public UserAccount getLoggedInUser() {
      return currentUser;
   }

   /**
    * Gets the checking account for the current user
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Gets the savings account for the current user
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Gets the last transaction for the current user
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Saves the transaction and updates the account balance in a single call. Attempts to save the transaction first
    * then attempts to update the account balance.
    *
    * @param ba The bank account to update
    * @param transaction The transaction to store
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
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

   /**
    * Gets the full bank account information for the specified account number
    *
    * @param accountId The account number of the desired
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public BankAccount getBankAccount(long accountId) {
      return dataAccessLayer.findBankAccount(accountId);
   }
}
