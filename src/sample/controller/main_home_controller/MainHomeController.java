package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.SystemLogging;
import sample.controller.dialog_controller.FinalReadingsController;
import sample.controller.dialog_controller.LoginDialogController;
import sample.model.CreditCompanyList;
import sample.model.databases.DatabaseHandler;
import sample.model.RateModel;

import javafx.scene.input.MouseEvent;
import sample.model.SystemStatus;

import java.io.IOException;


public class MainHomeController {


    /*
    * Main Screen For The Application
    *
    * Acts as a routing Class to route to different paths
    *
    * */
    private static MainHomeController instance;//Make Singleton Static Reference





    @FXML
    private TextField rDiesel,rPetrol;//References for the current diesel and petrol rate

    public MainHomeController() {
        instance=this;//Initialize the reference
    }


    /*
    * Get the current rates of the oil from system status table
    * Set the rates to the respective text fields
    *Also set the current rate to system status class
    * */
    @FXML
    public void initialize() {
        /*RateModel rateModel=DatabaseHandler.getCurrentRate();
        rDiesel.setText(String.valueOf(rateModel.getrDiesel()));
        rPetrol.setText(String.valueOf(rateModel.getrPetrol()));
        SystemStatus.setdRate(rateModel.getrDiesel());
        SystemStatus.setpRate(rateModel.getrPetrol());*/

        rDiesel.setText(String.valueOf(SystemStatus.getdRate()));
        rPetrol.setText(String.valueOf(SystemStatus.getpRate()));

    }



    /*
    * Load the Instant Billing View
    * */

    @FXML
    public void loadInstantBilling(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../../view/main_home_views/instantBilling.fxml");
    }


    /*
    * Load the Credit Billing View
    * Check if the Company Table is empty
    * */
    @FXML
    public void loadCreditBilling(MouseEvent e) throws IOException {
        if (CreditCompanyList.getCompanyList().size() != 0) {
            loadStage((Node)e.getSource(),"../../view/main_home_views/creditBilling.fxml");
        } else {
            System.out.println("No Company Detected.");
        }
    }

    /*
    * Load the Debt View
    * */


    @FXML
    public void loadDebt(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../../view/main_home_views/credit_company_list/debt.fxml");
    }

    /*
    * Load the Settings View
    *
    * */

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /*
    * Load the daily analytics view
    * */

    @FXML
    public void loadDailyAnalytics(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../../view/main_home_views/dailyTotalAnalytics.fxml");
    }

    /*
    * Load the profit loss view
    * */

    @FXML
    public void loadProfitLoss(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/main_home_views/profitLoss.fxml"));
        Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /*
    * Called From External Classes To change the rates
    * */


    public void showSettings(Stage stage) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/main_home_views/settings.fxml"));
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("../../css/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void setRates(String rDiesel,String rPetrol) {
        this.rDiesel.setText(rDiesel);
        this.rPetrol.setText(rPetrol);
    }

    /*
    * Get the current instance of the class
    * */

    public static MainHomeController getInstance() {
        return instance;
    }


    /*
    * Helper Function to load the stages
    *
    * */

    private void loadStage(Node node,String path) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource(path));
        Stage stage=(Stage)node.getScene().getWindow();
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getResource("../../css/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


}
