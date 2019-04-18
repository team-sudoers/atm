package virtualatm.service;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public interface IAtmService {
   
   boolean login(String username, String pin) throws Exception;
   void logout() throws Exception;
   
   UserAccount getLoggedInUser() throws Exception;
   BankAccount getCheckingAccount() throws Exception;
   BankAccount getSavingsAccount() throws Exception;
   List<Transaction> getAccountHistory() throws Exception;
   Transaction getLastTransaction() throws Exception;

   void withdraw(double amount, BankAccount source) throws Exception;
   void transfer(double amount, BankAccount source, BankAccount destination) throws Exception;
   void deposit(double amount, BankAccount destination) throws Exception;
   
}
