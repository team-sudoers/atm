/* 
 * File:    IAtmDataAccess.java
 * Date:    04/27/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.dataaccess;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 * Generic ATM Data access layer interface abstraction.  Contains create, read, update, and delete (CRUD) methods for 
 * data model objects from any type of data storage mechanism.
 */
public interface IAtmDataAccess {

   /**
    * Retrieves a list of all user accounts
    * @return the list of user accounts
    */
   List<UserAccount> getAllUserAccounts();

   /**
    * Finds a specific user account based upon the provided user name
    * @param userName The user name of the user account to find
    * @return The user account
    */
   UserAccount findUserAccount(String userName);

   /**
    * Retrieves a list of all bank accounts associated with a particular user
    * @param account The user account used to find bank accounts
    * @return The list of bank accounts associated with the specified user
    */
   List<BankAccount> findAllBankAccounts(UserAccount account);

   /**
    * Finds a specific bank account based upon the provided account number
    * @param accountNumber The account number of the bank account
    * @return The bank account found
    */
   BankAccount findBankAccount(long accountNumber);

   /**
    * Retrieves a list of all transactions
    * @return The list of transactions
    */
   List<Transaction> getAllTransactions();

   /**
    * Retrieves a list of all transaction for a particular user
    * @param user The user account associated with a transactions
    * @return The list of transactions associated with the user
    */
   List<Transaction> getTransactionsForUser(UserAccount user);

   /**
    * Adds a user account to the data store
    * @param account The information for the new user account
    * @return true/false depending on whether the user account was stored successfully
    */
   boolean addUserAccount(UserAccount account);

   /**
    * Adds a bank account to the data store
    * @param account The information for the new bank account
    * @return true/false depending on whether the bank account was stored successfully
    */
   boolean addBankAccount(BankAccount account);

   /**
    * Updates the information of an existing bank account
    * @param account The new information for the bank account
    * @return true/false depending on whether the updated information was stored successfully
    */
   boolean updateBankAccount(BankAccount account);

   /**
    * Adds a transaction to the data store
    * @param transaction The transaction information to store
    * @return true/false depending on whether the transaction was stored successfully
    */
   boolean addTransaction(Transaction transaction);

   /**
    * Deletes a transaction to the data store
    * @param transaction The transaction to delete
    * @return true/false depending on whether the transaction was deleted successfully
    */
   boolean deleteTransaction(Transaction transaction);

}
