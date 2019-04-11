/**
 * Sample Skeleton for 'HistoryPage.fxml' Controller Class
 */

package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HistoryPageController extends BaseAtmController {

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
//      UserAccount user = getAtmService().getLoggedInUser();
      //getAtmService().getAccountHistory(user);
   }


    @FXML
    void handleLogoutAction(ActionEvent event) {
       //getAtmService().logout();
    }

    @FXML
    void handleReturnAction(ActionEvent event) {
       // return to main page
    }

}
