/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.datamodel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Matt
 */
@XmlRootElement(name = "AtmData")
public class AtmData {
   
   private List<UserAccount> userAccountsList;
   private List<BankAccount> bankAccountsList;
   private List<Transaction> transactionsList;

   public AtmData() {
      userAccountsList = new ArrayList<>();
      bankAccountsList = new ArrayList<>();
      transactionsList = new ArrayList<>();
   }
   /**
    * @return the userAccountsList
    */
   @XmlElementWrapper(name = "UserAccountList")
   @XmlElement(name = "UserAccount")
   public List<UserAccount> getUserAccounts() {
      return userAccountsList;
   }

   /**
    * @param userAccounts the userAccountsList to set
    */
   public void setUserAccounts(List<UserAccount> userAccounts) {
      this.userAccountsList = userAccounts;
   }

   /**
    * @return the bankAccountsList
    */
   @XmlElementWrapper(name = "BankAccountList")
   @XmlElement(name = "BankAccount")
   public List<BankAccount> getBankAccounts() {
      return bankAccountsList;
   }

   /**
    * @param bankAccounts the bankAccountsList to set
    */
   public void setBankAccounts(List<BankAccount> bankAccounts) {
      this.bankAccountsList = bankAccounts;
   }

   /**
    * @return the transactionsList
    */
   @XmlElementWrapper(name = "TransactionList")
   @XmlElement(name = "Transaction")
   public List<Transaction> getTransactions() {
      return transactionsList;
   }

   /**
    * @param transactions the transactionsList to set
    */
   public void setTransactions(List<Transaction> transactions) {
      this.transactionsList = transactions;
   }
           
}
