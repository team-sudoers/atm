/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.datamodel;

/**
 *
 * @author Matt
 */
public class BankAccount {
   private long userId;
   private String accountType;
   private long accountNumber;
   private double accountBalance;

   /**
    * @return the accountNumber
    */
   public long getAccountNumber() {
      return accountNumber;
   }

   /**
    * @param accountNumber the accountNumber to set
    */
   public void setAccountNumber(long accountNumber) {
      this.accountNumber = accountNumber;
   }

   /**
    * @return the accountBalance
    */
   public double getAccountBalance() {
      return accountBalance;
   }

   /**
    * @param accountBalance the accountBalance to set
    */
   public void setAccountBalance(double accountBalance) {
      this.accountBalance = accountBalance;
   }

   /**
    * @return the userId
    */
   public long getUserId() {
      return userId;
   }

   /**
    * @param userId the userId to set
    */
   public void setUserId(long userId) {
      this.userId = userId;
   }

   /**
    * @return the accountType
    */
   public String getAccountType() {
      return accountType;
   }

   /**
    * @param accountType the accountType to set
    */
   public void setAccountType(String accountType) {
      this.accountType = accountType;
   }
}
