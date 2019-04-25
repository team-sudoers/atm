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

   private static final String XML_FILE_PATH = "datastore.xml";
   private final IAtmDataAccess dataAccessLayer;
   private UserAccount currentUser;

   public LocalAtmService() {
      dataAccessLayer = new XmlDataAccess(XML_FILE_PATH);
   }

   @Override
   public void withdraw(final double amount, final BankAccount account) throws Exception {

      if (account.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      BankAccount fromAccount = dataAccessLayer.findBankAccount(account.getAccountNumber());
      if (fromAccount == null) {
         throw new Exception("Please select a valid account");
      }

      double currentBalance = fromAccount.getAccountBalance();
      if (currentBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
      }

      double balance = fromAccount.getAccountBalance();
      balance -= amount;
      fromAccount.setAccountBalance(balance);

      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(account.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      dataAccessLayer.addTransaction(withdrawTransaction);
      dataAccessLayer.Save(true);
   }

   @Override
   public void transfer(final double amount, final BankAccount source, final BankAccount destination) throws Exception {

      if (source.getUserId() != currentUser.getId()) {
         throw new Exception("This source bank account doesn't belong to you!");
      }

      if (destination.getUserId() != currentUser.getId()) {
         throw new Exception("This destination bank account doesn't belong to you!");
      }

      BankAccount sourceAccount = dataAccessLayer.findBankAccount(source.getAccountNumber());
      if (sourceAccount == null) {
         throw new Exception("Please select a valid source account");
      }

      BankAccount destAccount = dataAccessLayer.findBankAccount(destination.getAccountNumber());
      if (destAccount == null) {
         throw new Exception("Please select a valid destination account");
      }

      double sourceBalance = sourceAccount.getAccountBalance();
      if (sourceBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
      }
      sourceBalance -= amount;
      sourceAccount.setAccountBalance(sourceBalance);
      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(sourceAccount.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      dataAccessLayer.addTransaction(withdrawTransaction);

      double destinationBalance = destination.getAccountBalance();
      destinationBalance += amount;
      destination.setAccountBalance(destinationBalance);
      Transaction depositTransaction = new Transaction();
      depositTransaction.setActivityType("Deposit");
      depositTransaction.setAmount(amount);
      depositTransaction.setBankAccountId(destination.getAccountNumber());
      depositTransaction.setDate(new Date());
      dataAccessLayer.addTransaction(depositTransaction);

      dataAccessLayer.Save(true);
   }

   @Override
   public void deposit(double amount, BankAccount destination) throws Exception {

      if (destination.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      BankAccount foundAccount = dataAccessLayer.findBankAccount(destination.getAccountNumber());
      if (foundAccount == null) {
         throw new Exception("Please select a valid account");
      }

      double balance = foundAccount.getAccountBalance();
      balance += amount;
      foundAccount.setAccountBalance(balance);

      Transaction transaction = new Transaction();
      transaction.setActivityType("Deposit");
      transaction.setAmount(amount);
      transaction.setBankAccountId(destination.getAccountNumber());
      transaction.setDate(new Date());
      dataAccessLayer.addTransaction(transaction);

      dataAccessLayer.Save(true);
   }

   @Override
   public List<Transaction> getAccountHistory() throws Exception {

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
   public boolean login(String username, String pin) throws Exception {

      if (currentUser != null) {
         throw new Exception("Please log out first!");
      }

      UserAccount foundAccount = null;
      for (UserAccount userAccount : dataAccessLayer.getAllUserAccounts()) {
         if (userAccount.getUserName().equals(username)) {
            foundAccount = userAccount;
            break;
         }
      }

      if (foundAccount == null) {
         return false;
      }

      if (foundAccount.getFailedLoginCount() >= MAX_FAILED_LOGINS) {
         long unlockTime = foundAccount.getLastFailedLogin().getTime() + LOCKOUT_SECONDS * 1000;
         if (System.currentTimeMillis() < unlockTime) {
            throw new Exception("Sorry this account is locked out");
         }
      }

      if (Security.compareHash(foundAccount.getPin(), pin) == false) {
         foundAccount.setFailedLoginCount(foundAccount.getFailedLoginCount() + 1);
         foundAccount.setLastFailedLogin(new Date());
         return false;
      }

      // successful login
      foundAccount.setFailedLoginCount(0);
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

      List<BankAccount> accounts = dataAccessLayer.findAllBankAccounts(currentUser);
      for (BankAccount account : accounts) {
         if (account.getAccountType().toLowerCase().equals("checking")) {
            return account;
         }
      }

      return new BankAccount();
   }

   @Override
   public BankAccount getSavingsAccount() throws Exception {

      List<BankAccount> accounts = dataAccessLayer.findAllBankAccounts(currentUser);
      for (BankAccount account : accounts) {
         if (account.getAccountType().toLowerCase().equals("savings")) {
            return account;
         }
      }

      return new BankAccount();

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
