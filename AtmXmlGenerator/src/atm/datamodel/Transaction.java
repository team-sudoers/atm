/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.datamodel;

import java.util.Date;

/**
 *
 * @author Matt
 */
//@XmlRootElement(name = "Transaction")
public class Transaction {
   private long bankAccountId;
   private Date date;
   private double amount;
   private String activityType;

   /**
    * @return the bankAccountId
    */
   public long getBankAccountId() {
      return bankAccountId;
   }

   /**
    * @param bankAccountId the bankAccountId to set
    */
   public void setBankAccountId(long bankAccountId) {
      this.bankAccountId = bankAccountId;
   }

   /**
    * @return the date
    */
   public Date getDate() {
      return date;
   }

   /**
    * @param date the date to set
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * @return the amount
    */
   public double getAmount() {
      return amount;
   }

   /**
    * @param amount the amount to set
    */
   public void setAmount(double amount) {
      this.amount = amount;
   }

   /**
    * @return the activityType
    */
   public String getActivityType() {
      return activityType;
   }

   /**
    * @param activityType the activityType to set
    */
   public void setActivityType(String activityType) {
      this.activityType = activityType;
   }
}
