package virtualatm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.utils.Security;

public class FakeAtmService implements IAtmService {

   private final int MAX_FAILED_LOGINS = 3;
   private final int LOCKOUT_SECONDS = 30;

   private final List<UserAccount> userAccounts;
   private final List<Transaction> transactions;
   private final BankAccount checkingAccount;
   private final BankAccount savingsAccount;
   private UserAccount currentUser;

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

   @Override
   public void withdraw(double amount, BankAccount account) throws Exception {

      if (account.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      if (account.getAccountNumber() != checkingAccount.getAccountNumber()
              && account.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account.");
      }

      double currentBalance = account.getAccountBalance();
      if (currentBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
      }

      double balance = account.getAccountBalance();
      balance -= amount;
      account.setAccountBalance(balance);

      Transaction withdrawTransaction = new Transaction();
      withdrawTransaction.setActivityType("Withdraw");
      withdrawTransaction.setAmount(amount);
      withdrawTransaction.setBankAccountId(account.getAccountNumber());
      withdrawTransaction.setDate(new Date());
      transactions.add(withdrawTransaction);
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
         throw new Exception("Please select a valid account.");
      }

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account.");
      }

      double sourceBalance = source.getAccountBalance();
      if (sourceBalance < amount) {
         throw new Exception("Insufficient Funds: Please select a lesser amount.");
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

   }

   @Override
   public void deposit(double amount, BankAccount destination) throws Exception {

      if (destination.getUserId() != currentUser.getId()) {
         throw new Exception("This bank account doesn't belong to you!");
      }

      if (destination.getAccountNumber() != checkingAccount.getAccountNumber()
              && destination.getAccountNumber() != savingsAccount.getAccountNumber()) {
         throw new Exception("Please select a valid account.");
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
            throw new Exception("Sorry this account is locked out.");
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
