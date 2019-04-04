/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.datamodel;

import java.util.List;

/**
 *
 * @author Matt
 */
public interface IAtmDataAccess {
   List<UserAccount> getAllUserAccounts();
   List<BankAccount> findAllBankAccountsForUser(UserAccount account);
   UserAccount findUserAccount(String userName);
   
   void addUserAccount(UserAccount account);
   void addBankAccount(BankAccount account);
   void addTransaction(Transaction transaction);
}
