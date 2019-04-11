/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualatm.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import virtualatm.service.IAtmService;

/**
 *
 * @author Matt
 */
public class BaseAtmController implements Initializable {
   @FXML // ResourceBundle that was given to the FXMLLoader
   private ResourceBundle resources;

   @FXML // URL location of the FXML file that was given to the FXMLLoader
   private URL location;
   
   private IAtmService atmService;
   private Scene loginScene;
   private Scene mainScene;
   private String languageId;

   /**
    * Initializes the controller class.
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
      // TODO
   }
   
   public void setAtmService(IAtmService svc) {
      atmService = svc;
   }
   
   public IAtmService getAtmService() {
      return atmService;
   }
   
   public void setLoginScene(Scene scene) {
      loginScene = scene;
   }
   
   public void setMainScene(Scene scene) {
      mainScene = scene;
   }
   
   public Scene getLoginScene(Scene scene) {
      return loginScene;
   }
   
   public Scene getMainScene(Scene mainScene) {
      return mainScene;
   }
   
   public void setLanguageId(String langId) {
      languageId = langId;
   }
   
   public String getLanguageId() {
      return languageId;
   }
}
