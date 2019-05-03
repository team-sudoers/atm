/* 
 * File:    IAtmDataAccess.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: AtmXmlGenerator
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.dataaccess;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

public interface IAtmDataAccess {

   List<UserAccount> getAllUserAccounts();

   UserAccount findUserAccount(String userName);

   List<BankAccount> findAllBankAccounts(UserAccount account);

   BankAccount findBankAccount(long accountNumber);

   List<Transaction> getAllTransactions();

   List<Transaction> getTransactionsForUser(UserAccount user);

   boolean addUserAccount(UserAccount account);

   boolean addBankAccount(BankAccount account);

   boolean updateBankAccount(BankAccount account);

   boolean addTransaction(Transaction transaction);

   boolean deleteTransaction(Transaction transaction);

}
