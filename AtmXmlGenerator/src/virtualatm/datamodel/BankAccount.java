/* 
 * File:    BankAccount.java
 * Date:    04/27/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.datamodel;

/**
 * Simple class to encapsulate BankAccount information used by the application.  Only contains get/set methods for attributes.
 */
public class BankAccount {

   /**
    * The id of the user account associated with this bank account
    */
   private long userId;
   
   /**
    * The account type ("checking" or "savings")
    */
   private String accountType;
   
   /**
    * The account number of this bank account
    */
   private long accountNumber;
   
   /**
    * The current balance of this bank account
    */
   private double accountBalance;

   /**
    * Default constructor
    */
   public BankAccount() {
      userId = -1;
      accountType = "";
      accountNumber = -1;
      accountBalance = 0.0;
   }

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
