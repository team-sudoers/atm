/* 
 * File:    XmlDataAccess.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.dataaccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;

/**
 * XML based implementation of the IAtmDataAccess interface. Used to persist ATM data model objects to/from an XML file.
 */
public class XmlDataAccess implements IAtmDataAccess {

   /**
    * Default file path
    */
   private static final String XML_FILE_PATH = "datastore.xml";

   /**
    * Current file path
    */
   private final String filePath;

   /**
    * Flag indicating whether changes are waiting to be written to the file
    */
   private boolean dirty;

   /**
    * In-memory cache of the XML data file
    */
   private AtmData dataCache;

   /**
    * Default constructor. Initializes the class using the default xml file path.
    */
   public XmlDataAccess() {
      this(XML_FILE_PATH);
   }

   /**
    * Overridden constructor. Allows the caller to specify an xml file path.
    *
    * @param xmlPath The path of the xml file
    */
   public XmlDataAccess(String xmlPath) {
      filePath = xmlPath;
      dirty = true;
      dataCache = new AtmData();
   }

   /**
    * Saves the in-memory data cache to disk.
    *
    * @param force Forces the file to be re-written even if there are no changes to the file
    * @return true/false value depending on whether the file was written
    */
   public boolean save(Boolean force) {
      return save(filePath, force);
   }

   /**
    * Saves the in-memory data cache to the specified path on the disk.
    *
    * @param path The path to write the file
    * @param force Forces the file to be re-written even if there are no changes to the file
    * @return true/false value depending on whether the file was written
    */
   public boolean save(String path, Boolean force) {
      try {
         if (force == true) {
            dirty = true;
         }

         WriteFile(path, dataCache);
         return true;
      } catch (FileNotFoundException | JAXBException ex) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, ex);
         return false;
      }
   }

   /**
    * Reads the xml file from disk and stores the data in the in-memory data cache.
    *
    * @return true/false value depending on whether the file was read
    */
   public boolean load() {
      try {
         ReadFile(filePath);
         return true;
      } catch (FileNotFoundException | JAXBException ex) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, ex);
         return false;
      }
   }

   /**
    * Retrieves a list of all user accounts
    *
    * @return the list of user accounts
    */
   @Override
   public List<UserAccount> getAllUserAccounts() {
      try {
         ReadFile(filePath);
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }
      return dataCache.getUserAccounts();
   }

   /**
    * Retrieves a list of all bank accounts associated with a particular user
    *
    * @param account The user account used to find bank accounts
    * @return The list of bank accounts associated with the specified user
    */
   @Override
   public List<BankAccount> findAllBankAccounts(UserAccount account) {
      List<BankAccount> bankAccounts = new ArrayList<>();
      try {
         ReadFile(filePath);

         for (BankAccount bankAccount : dataCache.getBankAccounts()) {
            if (bankAccount.getUserId() == account.getId()) {
               bankAccounts.add(bankAccount);
            }
         }
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }

      return bankAccounts;
   }

   /**
    * Finds a specific user account based upon the provided user name
    *
    * @param userName The user name of the user account to find
    * @return The user account
    */
   @Override
   public UserAccount findUserAccount(String userName) {
      UserAccount retValue = null;

      try {
         ReadFile(filePath);

         for (UserAccount userAccount : dataCache.getUserAccounts()) {
            if (userAccount.getUserName() == null ? userName == null : userAccount.getUserName().equals(userName)) {
               retValue = userAccount;
               break;
            }
         }
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }

      return retValue;
   }

   /**
    * Adds a bank account to the data store
    *
    * @param account The information for the new bank account
    * @return true/false depending on whether the bank account was stored successfully
    */
   @Override
   public boolean addBankAccount(BankAccount account) {
      dataCache.getBankAccounts().add(account);
      return XmlDataAccess.this.save(true);
   }

   /**
    * Updates the information of an existing bank account
    *
    * @param account The new information for the bank account
    * @return true/false depending on whether the updated information was stored successfully
    */
   @Override
   public boolean updateBankAccount(BankAccount account) {
      BankAccount ba = findBankAccount(account.getAccountNumber());
      ba.setAccountBalance(account.getAccountBalance());
      return XmlDataAccess.this.save(true);
   }

   /**
    * Adds a transaction to the data store
    *
    * @param transaction The transaction information to store
    * @return true/false depending on whether the transaction was stored successfully
    */
   @Override
   public boolean addTransaction(Transaction transaction) {
      transaction.setId(getNextTransactionId());
      dataCache.getTransactions().add(transaction);
      return XmlDataAccess.this.save(true);
   }

   /**
    * Deletes a transaction to the data store
    *
    * @param transaction The transaction to delete
    * @return true/false depending on whether the transaction was deleted successfully
    */
   @Override
   public boolean deleteTransaction(Transaction transaction) {
      dataCache.getTransactions().removeIf(t -> t.getId() == transaction.getId());
      return XmlDataAccess.this.save(true);
   }

   /**
    * Adds a user account to the data store
    *
    * @param account The information for the new user account
    * @return true/false depending on whether the user account was stored successfully
    */
   @Override
   public boolean addUserAccount(UserAccount account) {
      dataCache.getUserAccounts().add(account);
      return XmlDataAccess.this.save(true);
   }

   /**
    * Private method that uses the JAX parser to deserialize the xml file content into an AtmData instance
    * @param path The path of the XML file
    * @throws JAXBException The exception thrown if the JAX parser encounters any issues deserializing the file
    * @throws FileNotFoundException The exception thrown if the file wasn't found
    */
   private synchronized void ReadFile(String path) throws JAXBException, FileNotFoundException {
      if (dirty == true) {
         JAXBContext context = JAXBContext.newInstance(AtmData.class);
         Unmarshaller um = context.createUnmarshaller();
         dataCache = (AtmData) um.unmarshal(new FileReader(path));
         dirty = false;
      }
   }

      /**
    * Private method that uses the JAX parser to serialize the xml file content from an AtmData instance
    * @param path The path of the XML file
    * @throws JAXBException The exception thrown if the JAX parser encounters any issues serializing the data
    * @throws FileNotFoundException The exception thrown if the file wasn't found
    */
   private synchronized void WriteFile(String path, AtmData data) throws JAXBException, FileNotFoundException {
      if (dirty == true) {
         JAXBContext context = JAXBContext.newInstance(AtmData.class);
         Marshaller serializer = context.createMarshaller();
         serializer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         serializer.marshal(data, new File(path));
         dirty = false;
      }
   }

   /**
    * Retrieves a list of all transaction for a particular user
    *
    * @param user The user account associated with a transactions
    * @return The list of transactions associated with the user
    */
   @Override
   public List<Transaction> getTransactionsForUser(UserAccount user) {
      List<Transaction> transactions = new ArrayList<>();
      List<Long> userBankAccountNumbers = new ArrayList<>();

      findAllBankAccounts(user).forEach((account) -> userBankAccountNumbers.add(account.getAccountNumber()));

      try {

         ReadFile(filePath);
         for (Transaction transaction : dataCache.getTransactions()) {
            if (userBankAccountNumbers.contains(transaction.getBankAccountId())) {
               transactions.add(transaction);
            }
         }
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }

      return transactions;
   }

   /**
    * Retrieves a list of all transactions
    *
    * @return The list of transactions
    */
   @Override
   public List<Transaction> getAllTransactions() {
      List<Transaction> transactions = new ArrayList<>();

      try {

         ReadFile(filePath);

         transactions = dataCache.getTransactions();

      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }

      return transactions;
   }

   /**
    * Finds a specific bank account based upon the provided account number
    *
    * @param accountNumber The account number of the bank account
    * @return The bank account found
    */
   @Override
   public BankAccount findBankAccount(long accountNumber) {

      BankAccount retVal = null;
      try {
         ReadFile(filePath);

         for (BankAccount bankAccount : dataCache.getBankAccounts()) {
            if (bankAccount.getAccountNumber() == accountNumber) {
               retVal = bankAccount;
               break;
            }
         }
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }

      return retVal;
   }

   /**
    * Utility method used to increment the transaction id for a new transaction.  Simulates an auto-increment column.
    * @return 
    */
   private long getNextTransactionId() {
      long retVal = 0;
      List<Transaction> transactions = getAllTransactions();
      for (Transaction transaction : transactions) {
         if (transaction.getId() >= retVal) {
            retVal = transaction.getId() + 1;
         }
      }
      return retVal;
   }
}
