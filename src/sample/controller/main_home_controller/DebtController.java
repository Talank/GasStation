package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;

import javafx.stage.Stage;
import sample.controller.main_home_controller.lists_controllers.CreditCompanyDetailsController;
import sample.controller.main_home_controller.lists_controllers.CreditCompanyListCell;
import sample.controller.main_home_controller.lists_controllers.DebtListCell;
import sample.library.toolbar.ToolBar;
import sample.model.CreditCompanyList;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;

import java.io.IOException;


/*
* This Class provides the Credit Info of all the associated companies
*
* A list of all the companies is displayed and when user clicks the items in the list
* its detailed view is presented
* */

public class DebtController {

    @FXML
    private JFXListView<DebtListModel> debtList;

    @FXML
    private ToolBar toolBar;

    private static DebtController instance;

    public DebtController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("Credit Company Lists");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        debtList.setCellFactory(lv->new CreditCompanyListCell());
        debtList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        debtList.getItems().addAll(CreditCompanyList.getDetailCompanyList());

        debtList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DebtListModel> observable, DebtListModel oldValue, DebtListModel newValue)->{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/main_home_views/credit_company_details/creditCompanyDetails.fxml"));
            try {
                Parent root=loader.load();
                sample.controller.main_home_controller.credit_company_details_controller.CreditCompanyDetailsController controller=loader.getController();
                controller.setParameters(DatabaseHandler.generateTableName(newValue.getCompanyName(),newValue.getId()));
                Stage stage= ((Stage)debtList.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }


    public static DebtController getInstance() {
        return instance;
    }

}
