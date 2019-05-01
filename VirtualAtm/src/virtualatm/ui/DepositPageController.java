/* 
 * File:    DepositPageController.java
 * Date:    04/27/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
package virtualatm.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import virtualatm.datamodel.BankAccount;
import virtualatm.datamodel.Transaction;
import virtualatm.datamodel.UserAccount;
import virtualatm.service.AtmServiceError;

public class DepositPageController extends BaseAtmController {

    @FXML
    private Label topLabel;//didn't remove because it's linked to the date. 

    @FXML // fx:id="checkingAmountLabel"
    private Label checkingAmountLabel; // Value injected by FXMLLoader

    @FXML // fx:id="savingsAmountLabel"
    private Label savingsAmountLabel; // Value injected by FXMLLoader

    @FXML // fx:id="lastTransactionDateLabel"
    private Label lastTransactionDateLabel; // Value injected by FXMLLoader

    @FXML // fx:id="depositAmount"
    private TextField depositAmount; // Value injected by FXMLLoader

    private String selectedAccountType;

    @FXML
    private ComboBox<String> fromAccount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        fromAccount.getItems().addAll(getTranslatedText("checking"), getTranslatedText("savings"));
        refresh();
    }

    private void refresh() {
        try {
            String pattern = "MM/dd/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            topLabel.setText(String.format("%s", simpleDateFormat.format(new Date())));

            UserAccount user = getAtmService().getLoggedInUser();
            BankAccount ca = getAtmService().getCheckingAccount();
            BankAccount sa = getAtmService().getSavingsAccount();
            Transaction lastTransaction = getAtmService().getLastTransaction();

            if (ca != null) {
                checkingAmountLabel.setText(String.format("$%.2f", ca.getAccountBalance()));
            }

            if (sa != null) {
                savingsAmountLabel.setText(String.format("$%.2f", sa.getAccountBalance()));
            }

            if (lastTransaction != null) {
                lastTransactionDateLabel.setText(String.format("%s", simpleDateFormat.format(lastTransaction.getDate())));
            }
        } catch (Exception e) {
            super.showError(e.getMessage());
        }
    }

    @FXML
    void handleDepositAccountType(ActionEvent event) {
        resetTimer();
        String accountType = fromAccount.getValue();
        if (accountType != null) {
            selectedAccountType = accountType;
        }
    }

    @FXML
    void handleDepositAction(ActionEvent event) {

        try {

            resetTimer();
            if (validateUserInput() == false) {
                return;
            }

            BankAccount ba = null;
            if (selectedAccountType.equals(getTranslatedText("checking"))) {
                ba = getAtmService().getCheckingAccount();
            } else {
                ba = getAtmService().getSavingsAccount();
            }

            double amount = parseDepositAmount(depositAmount.getText());
            AtmServiceError error = getAtmService().deposit(amount, ba);
            if (error != AtmServiceError.SUCCESS) {
                refresh();
                showError(error);
            } else {
                showMainPage();
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    void handleLogoutAction(ActionEvent event) {
        try {
            resetTimer();
            getAtmService().logout();
            showLoginPage();
        } catch (Exception e) {
            super.showError(e.getMessage());
        }
    }

    private boolean validateUserInput() {
        double value = -1;
        try {

            if ((selectedAccountType == null) || (selectedAccountType.length() <= 0)) {
                String message = getTranslatedText("DESTINATION_ACCOUNT_NOT_FOUND");
                showError(message);
                return false;
            }

            value = parseDepositAmount(depositAmount.getText());

        } catch (Exception e) {
            value = -1;
        }

        if (value <= 0) {
            String message = getTranslatedText("INVALID_DOLLAR_AMOUNT");
            showError(message);
        }

        return value > 0;
    }

    private double parseDepositAmount(String text) {

        if (text.length() <= 0) {
            return 0.0;
        }

        if (text.startsWith("$")) {
            text = text.substring(1);
        }

        return Double.parseDouble(text);
    }

    @FXML
    void handleReturnAction(ActionEvent event) {
        showMainPage();
    }
}
