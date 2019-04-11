/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.service;

import java.util.ArrayList;
import java.util.List;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 *
 * @author Matt
 */
public class FakeAtmService implements IAtmService {

   @Override
   public void withdraw(BankAccount account) {
      System.out.println("virtualatm.service.FakeAtmService.withdraw()");
   }

   @Override
   public void transfer(BankAccount fromAccount, BankAccount toAccount) {
      System.out.println("virtualatm.service.FakeAtmService.transfer()");
   }

   @Override
   public void deposit(double amount, BankAccount toAccount) {
      System.out.println("virtualatm.service.FakeAtmService.deposit()");
   }

   @Override
   public List<Transaction> getAccountHistory(UserAccount user) {
      System.out.println("virtualatm.service.FakeAtmService.getAccountHistory()");
      return new ArrayList<>();
   }

   @Override
   public boolean login(String username, String password) {
      System.out.println("virtualatm.service.FakeAtmService.login()");
      return true;
   }

   @Override
   public void logout() {
      System.out.println("virtualatm.service.FakeAtmService.logout()");
   }

   @Override
   public UserAccount getLoggedInUser() {
      System.out.println("virtualatm.service.FakeAtmService.getLoggedInUser()");
      return new UserAccount();
   }

   @Override
   public BankAccount getCheckingAccount(UserAccount user) {
      System.out.println("virtualatm.service.FakeAtmService.getCheckingAccount()");
      return new BankAccount();
   }

   @Override
   public BankAccount getSavingsAccount(UserAccount user) {
      System.out.println("virtualatm.service.FakeAtmService.getSavingsAccount()");
      return new BankAccount();
   }

}
