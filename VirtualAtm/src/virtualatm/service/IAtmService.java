/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.service;

import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 *
 * @author Matt
 */
public interface IAtmService {
   void withdraw(BankAccount account);
   void transfer(BankAccount fromAccount, BankAccount toAccount);
   void deposit(double amount, BankAccount toAccount);
   List<Transaction> getAccountHistory(UserAccount user);
   boolean login(String username, String password);
}
