/* 
 * File:    BankAccount.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: AtmXmlGenerator
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.datamodel;

public class BankAccount {

   private long userId;
   private String accountType;
   private long accountNumber;
   private double accountBalance;

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
