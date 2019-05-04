/* 
 * File:    IAtmService.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.service;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 * Generic ATM service layer interface containing methods needed by the user interface layer.
 */
public interface IAtmService {

   /**
    * Logs a user into the application if the provided user name and pin match an existing user account.
    * @param username The user name of the account holder
    * @param pin The pin of the account holder
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   AtmServiceError login(String username, String pin);

   /**
    * Logs the active user out of the application
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   AtmServiceError logout();

   /**
    * Attempts to withdraw the specified amount from a bank account
    * @param amount The amount to withdraw
    * @param source The account to withdraw from 
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   AtmServiceError withdraw(double amount, BankAccount source);

   /**
    * Attempts to transfer the specified amount between accounts withdrawing from the source and depositing into the destination.
    * @param amount The amount to transfer
    * @param source The source account to withdraw from
    * @param destination The destination account to deposit into
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   AtmServiceError transfer(double amount, BankAccount source, BankAccount destination);

   /**
    * Attempts to deposit the specified amount into a destination account
    * @param amount The amount to deposit
    * @param destination The account to deposit into
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   AtmServiceError deposit(double amount, BankAccount destination);

   /**
    * Gets the currently logged in user information
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   UserAccount getLoggedInUser();

   /**
    * Gets the checking account for the current user
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   BankAccount getCheckingAccount();

   /**
    * Gets the savings account for the current user
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   BankAccount getSavingsAccount();
   
   /**
    * Gets the full bank account information for the specified account number
    * @param accountId The account number of the desired 
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   BankAccount getBankAccount(long accountId);

   /**
    * Gets the transaction history for the current user
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   List<Transaction> getAccountHistory();

   /**
    * Gets the last transaction for the current user
    * @return The appropriate AtmServiceError enumeration value depending upon whether the operation was successful.
    */
   Transaction getLastTransaction();

}
