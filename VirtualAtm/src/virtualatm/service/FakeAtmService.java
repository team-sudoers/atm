/* 
 * File:    FakeAtmService.java
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
import java.util.logging.Level;
import java.util.logging.Logger;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.utils.Security;

/**
 * Mockup ATM service containing in memory user account, bank accounts, and transaction information.
 */
public class FakeAtmService implements IAtmService {

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
    * Current list of user accounts
    */
   private final List<UserAccount> userAccounts;

   /**
    * Current list of transactions
    */
   private final List<Transaction> transactions;

   /**
    * Current users checking account
    */
   private final BankAccount checkingAccount;

   /**
    * Current users savings account
    */
   private final BankAccount savingsAccount;

   /**
    * Currently logged in user account
    */
   private UserAccount currentUser;

   /**
    * Default constructor. Sets up a user account, checking and savings accounts, and deposits money into each account.
    */
   public FakeAtmService() {

      userAccounts = new ArrayList<>();
      transactions = new ArrayList<>();

      checkingAccount = new BankAccount();
      savingsAccount = new BankAccount();

      try {

         UserAccount userAccount = new UserAccount();
         userAccount.setId(1);
         userAccount.setFirstName("santa");
         userAccount.setLastName("claus");
         userAccount.setCellNumber("321-555-1212");
         userAccount.setEmail("sc@northpole.com");
         userAccount.setUserName("sc");
         userAccount.setPin(Security.createHash("12345"));
         userAccounts.add(userAccount);

         checkingAccount.setUserId(1);
         checkingAccount.setAccountType("checking");
         checkingAccount.setAccountNumber(11111111);
         checkingAccount.setAccountBalance(1.11);

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
      } catch (Exception ex) {
         Logger.getLogger(FakeAtmService.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * Attempts to withdraw the specified amount from a bank account
    *
    * @param amount The amount to withdraw
    * @param source The account to withdraw from
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public AtmServiceError withdraw(double amount, BankAccount source) {

      if (source.getUserId() != currentUser.getId()) {
         return AtmServiceError.ACCOUNT_NOT_OWNED;
      }

      if (source.getAccountNumber() != checkingAccount.getAccountNumber()
              && source.getAccountNumber() != savingsAccount.getAccountNumber()) {
         return AtmServiceError.ACCOUNT_NOT_OWNED;
      }

      if (amount > MAX_DAILY_WITHDRAW_LIMIT) {
         return AtmServiceError.WITHDRAWAL_AMOUNT_EXCEEDS_LIMIT;
      }

      Date date = Date.from(Instant.now().minus(Duration.ofDays(1)));
      double totalTransAmount = amount;
      for (Transaction t : transactions) {
         if ((t.getDate().after(date))
                 && (t.getBankAccountId() == source.getAccountNumber())
                 && (t.getActivityType().equalsIgnoreCase("Withdraw"))) {
            totalTransAmount += t.getAmount();
         }
      }

      if (totalTransAmount > MAX_DAILY_WITHDRAW_LIMIT) {
         return AtmServiceError.DAILY_WITHDRAWAL_LIMIT_EXCEEDED;
      }

      double currentBalance = source.getAccountBalance();
      if (currentBalance < amount) {
         return AtmServiceError.INSUFFICIENT_FUNDS;
      }

      double balance = source.getAccountBalance();
      balance -= amount;
      source.setAccountBalance(balance);

      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(source.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      transactions.add(withdrawTransaction);

      return AtmServiceError.SUCCESS;
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
   public AtmServiceError transfer(double amount, BankAccount source, BankAccount destination) {

      if (source.getUserId() != currentUser.getId()) {
         return AtmServiceError.SOURCE_BANK_ACCOUNT_NOT_OWNED;
      }

      if (destination.getUserId() != currentUser.getId()) {
         return AtmServiceError.DESTINATION_BANK_ACCOUNT_NOT_OWNED;
      }

      if (source.getAccountNumber() != checkingAccount.getAccountNumber()
              && source.getAccountNumber() != savingsAccount.getAccountNumber()) {
         return AtmServiceError.SOURCE_ACCOUNT_NOT_FOUND;
      }

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         return AtmServiceError.DESTINATION_ACCOUNT_NOT_FOUND;
      }

      double sourceBalance = source.getAccountBalance();
      if (sourceBalance < amount) {
         return AtmServiceError.INSUFFICIENT_FUNDS;
      }
      sourceBalance -= amount;
      source.setAccountBalance(sourceBalance);
      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(source.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      transactions.add(withdrawTransaction);

      double destinationBalance = destination.getAccountBalance();
      destinationBalance += amount;
      destination.setAccountBalance(destinationBalance);
      Transaction depositTransaction = new Transaction();
      depositTransaction.setActivityType("Deposit");
      depositTransaction.setAmount(amount);
      depositTransaction.setBankAccountId(destination.getAccountNumber());
      depositTransaction.setDate(new Date());
      transactions.add(depositTransaction);

      return AtmServiceError.SUCCESS;
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

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         return AtmServiceError.USER_ACCOUNT_NOT_FOUND;
      }

      double balance = destination.getAccountBalance();
      balance += amount;
      destination.setAccountBalance(balance);

      Transaction transaction = new Transaction();
      transaction.setActivityType("Deposit");
      transaction.setAmount(amount);
      transaction.setBankAccountId(destination.getAccountNumber());
      transaction.setDate(new Date());
      transactions.add(transaction);

      return AtmServiceError.SUCCESS;
   }

   /**
    * Gets the transaction history for the current user
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */   
   @Override
   public List<Transaction> getAccountHistory() {

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

   /**
    * Logs a user into the application if the provided user name and pin match an existing user account.
    *
    * @param username The user name of the account holder
    * @param pin The pin of the account holder
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public AtmServiceError login(String username, String pin) {

      UserAccount foundAccount = null;

      for (UserAccount userAccount : userAccounts) {
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

      if (checkingAccount.getUserId() != currentUser.getId()) {
         return new BankAccount();
      }

      return checkingAccount;
   }

   /**
    * Gets the savings account for the current user
    *
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public BankAccount getSavingsAccount() {

      if (savingsAccount.getUserId() != currentUser.getId()) {
         return new BankAccount();
      }

      return savingsAccount;
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
    * Gets the full bank account information for the specified account number
    *
    * @param accountId The account number of the desired
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   @Override
   public BankAccount getBankAccount(long accountId) {
      if (savingsAccount.getAccountNumber() != accountId) {
         return savingsAccount;
      }

      return checkingAccount;
   }
}
