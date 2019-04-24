package virtualatm.dataaccess;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public interface IAtmDataAccess {
   
   Boolean Save(Boolean force);
           
   List<UserAccount> getAllUserAccounts();
   List<BankAccount> findAllBankAccounts(UserAccount account);
   UserAccount findUserAccount(String userName);
   BankAccount findBankAccount(long accountNumber);
   
   void addUserAccount(UserAccount account);
   void addBankAccount(BankAccount account);
   void addTransaction(Transaction transaction);

   List<Transaction> getTransactionsForUser(UserAccount user);
}