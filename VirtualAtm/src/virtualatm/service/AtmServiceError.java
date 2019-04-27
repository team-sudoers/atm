/* 
 * File:    AtmServiceError.java
 * Date:    04/27/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.service;

public enum AtmServiceError {
   SUCCESS,
   ACCOUNT_NOT_OWNED,
   USER_ACCOUNT_NOT_FOUND,
   BANK_ACCOUNT_NOT_FOUND,
   INSUFFICIENT_FUNDS,
   SOURCE_ACCOUNT_NOT_FOUND,
   DESTINATION_ACCOUNT_NOT_FOUND,
   SOURCE_BANK_ACCOUNT_NOT_OWNED,
   DESTINATION_BANK_ACCOUNT_NOT_OWNED,
   USER_ACCOUNT_LOCKED,
   INVALID_USER_CREDENTIALS,
   LOGOUT_REMINDER,
   FAILURE_SAVING_TRANSACTION,
}
