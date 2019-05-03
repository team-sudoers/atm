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

public class XmlDataAccess implements IAtmDataAccess {

   private static final String XML_FILE_PATH = "datastore.xml";
   private final String filePath;
   private boolean dirty;
   private AtmData dataCache;
   
   public XmlDataAccess() {
      this(XML_FILE_PATH);
   }

   public XmlDataAccess(String xmlPath) {
      filePath = xmlPath;
      dirty = true;
      dataCache = new AtmData();
   }

   public boolean save(Boolean force) {
      return save(filePath, force);
   }

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

   public boolean load() {
      try {
         ReadFile(filePath);
         return true;
      } catch (FileNotFoundException | JAXBException ex) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, ex);
         return false;
      }
   }

   @Override
   public List<UserAccount> getAllUserAccounts() {
      try {
         ReadFile(filePath);
      } catch (FileNotFoundException | JAXBException e) {
         Logger.getLogger(XmlDataAccess.class.getName()).log(Level.SEVERE, null, e);
      }
      return dataCache.getUserAccounts();
   }

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

   @Override
   public boolean addBankAccount(BankAccount account) {
      dataCache.getBankAccounts().add(account);
      return XmlDataAccess.this.save(true);
   }

   @Override
   public boolean updateBankAccount(BankAccount account) {
      BankAccount ba = findBankAccount(account.getAccountNumber());
      ba.setAccountBalance(account.getAccountBalance());
      return XmlDataAccess.this.save(true);
   }

   @Override
   public boolean addTransaction(Transaction transaction) {
      transaction.setId(getNextTransactionId());
      dataCache.getTransactions().add(transaction);
      return XmlDataAccess.this.save(true);
   }
   
   @Override
   public boolean deleteTransaction(Transaction transaction) {
      dataCache.getTransactions().removeIf(t -> t.getId() == transaction.getId());
      return XmlDataAccess.this.save(true);
   }

   @Override
   public boolean addUserAccount(UserAccount account) {
      dataCache.getUserAccounts().add(account);
      return XmlDataAccess.this.save(true);
   }

   private synchronized void ReadFile(String path) throws JAXBException, FileNotFoundException {
      if (dirty == true) {
         JAXBContext context = JAXBContext.newInstance(AtmData.class);
         Unmarshaller um = context.createUnmarshaller();
         dataCache = (AtmData) um.unmarshal(new FileReader(path));
         dirty = false;
      }
   }

   private synchronized void WriteFile(String path, AtmData data) throws JAXBException, FileNotFoundException {
      if (dirty == true) {
         JAXBContext context = JAXBContext.newInstance(AtmData.class);
         Marshaller serializer = context.createMarshaller();
         serializer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         serializer.marshal(data, new File(path));
         dirty = false;
      }
   }

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
