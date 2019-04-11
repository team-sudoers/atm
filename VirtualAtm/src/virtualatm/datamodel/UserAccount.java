/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matt
 */
//@XmlRootElement(name = "UserAccount")
public class UserAccount {
   private long id;
   private String firstName;
   private String lastName;
   private String cellNumber;
   private String email;
   private String userName;
   private String pin;
   private String checkingAccountNumber;
   private String checkingAccountBalance;
   private String savingsAccountNumber;
   private String savingsAccountBalance;
   private List<Transaction> transactionHistory;
   
   public UserAccount() {
      transactionHistory = new ArrayList<>();
   }
   /**
    * @return the firstName
    */
   public String getFirstName() {
      return firstName;
   }

   /**
    * @param firstName the firstName to set
    */
   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   /**
    * @return the lastName
    */
   public String getLastName() {
      return lastName;
   }

   /**
    * @param lastName the lastName to set
    */
   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   /**
    * @return the cellNumber
    */
   public String getCellNumber() {
      return cellNumber;
   }

   /**
    * @param cellNumber the cellNumber to set
    */
   public void setCellNumber(String cellNumber) {
      this.cellNumber = cellNumber;
   }

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email the email to set
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return the userName
    */
   public String getUserName() {
      return userName;
   }

   /**
    * @param userName the userName to set
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * @return the pin
    */
   public String getPin() {
      return pin;
   }

   /**
    * @param pin the pin to set
    */
   public void setPin(String pin) {
      this.pin = pin;
   }

   /**
    * @return the checkingAccountNumber
    */
   public String getCheckingAccountNumber() {
      return checkingAccountNumber;
   }

   /**
    * @param checkingAccountNumber the checkingAccountNumber to set
    */
   public void setCheckingAccountNumber(String checkingAccountNumber) {
      this.checkingAccountNumber = checkingAccountNumber;
   }

   /**
    * @return the checkingAccountBalance
    */
   public String getCheckingAccountBalance() {
      return checkingAccountBalance;
   }

   /**
    * @param checkingAccountBalance the checkingAccountBalance to set
    */
   public void setCheckingAccountBalance(String checkingAccountBalance) {
      this.checkingAccountBalance = checkingAccountBalance;
   }

   /**
    * @return the savingsAccountNumber
    */
   public String getSavingsAccountNumber() {
      return savingsAccountNumber;
   }

   /**
    * @param savingsAccountNumber the savingsAccountNumber to set
    */
   public void setSavingsAccountNumber(String savingsAccountNumber) {
      this.savingsAccountNumber = savingsAccountNumber;
   }

   /**
    * @return the savingsAccountBalance
    */
   public String getSavingsAccountBalance() {
      return savingsAccountBalance;
   }

   /**
    * @param savingsAccountBalance the savingsAccountBalance to set
    */
   public void setSavingsAccountBalance(String savingsAccountBalance) {
      this.savingsAccountBalance = savingsAccountBalance;
   }

   /**
    * @return the transactionHistory
    */
   public List<Transaction> getTransactionHistory() {
      return transactionHistory;
   }

   /**
    * @param transactionHistory the transactionHistory to set
    */
   public void setTransactionHistory(List<Transaction> transactionHistory) {
      this.transactionHistory = transactionHistory;
   }

   /**
    * @return the id
    */
   public long getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(long id) {
      this.id = id;
   }
}
