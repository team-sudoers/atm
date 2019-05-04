/* 
 * File:    UserAccount.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.datamodel;

import java.sql.Timestamp;
import java.util.Date;

public class UserAccount {

   private long id;
   private String firstName;
   private String lastName;
   private String cellNumber;
   private String email;
   private String userName;
   private String pin;
   private int failedLoginCount;
   private Date lastFailedLogin;

   public UserAccount() {
      id = -1;
      firstName = "";
      lastName = "";
      cellNumber = "";
      email = "";
      userName = "";
      pin = "";
      failedLoginCount = 0;
      lastFailedLogin = new Timestamp(System.currentTimeMillis());
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

   /**
    * @return the failedLoginCount
    */
   public int getFailedLoginCount() {
      return failedLoginCount;
   }

   /**
    * @param failedLoginCount the failedLoginCount to set
    */
   public void setFailedLoginCount(int failedLoginCount) {
      this.failedLoginCount = failedLoginCount;
   }

   /**
    * @return the lastFailedLogin
    */
   public Date getLastFailedLogin() {
      return lastFailedLogin;
   }

   /**
    * @param value the failedLoginCount to set
    */
   public void setLastFailedLogin(Date value) {
      lastFailedLogin = value;
   }
}
