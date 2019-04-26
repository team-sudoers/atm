package virtualatm.service;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public interface IAtmService {

   AtmServiceError login(String username, String pin);

   AtmServiceError logout();

   AtmServiceError withdraw(double amount, BankAccount source);

   AtmServiceError transfer(double amount, BankAccount source, BankAccount destination);

   AtmServiceError deposit(double amount, BankAccount destination);

   UserAccount getLoggedInUser();

   BankAccount getCheckingAccount();

   BankAccount getSavingsAccount();

   List<Transaction> getAccountHistory();

   Transaction getLastTransaction();

}
