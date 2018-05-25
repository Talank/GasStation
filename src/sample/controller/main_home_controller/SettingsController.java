package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.OutputTasks.GetSettingsTask;
import sample.controller.dialog_controller.*;
import sample.controller.main_home_controller.lists_controllers.DebtListCell;
import sample.controller.SystemLogging;
import sample.library.toolbar.ToolBar;
import sample.model.CreditCompanyList;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.DebtListModel;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.ClearExpenditureTask;
import sample.tasks.database_tasks.RemoveCompanyTask;

import java.io.IOException;


/*
*
* This Is the Setting Class
* Current Feature provides options to add,remove company
* change username and password
*
* */

public class SettingsController {

//  List to hold all the associated companies
    @FXML
    private JFXListView<DebtListModel> debtList;

    @FXML
    private Label petrolStockValue,dieselStockValue,petrolStockDate,dieselStockDate,counterStockDate,
            counterStockValue,dMeterValue,dMeterDate,pMeterValue,pMeterDate,usernameValue,usernameDate,
            dRateValue,dRateDate,pRateValue,pRateDate,billPrinter,analyticsPrinter,billPrinterDate,analyticsPrinterDate;

    @FXML
    private ToolBar toolBar;

    private GetSettingsTask task;

//  singleton reference for referring from dialogs
    private static SettingsController instance;

//  Int to store weather user wants to edit or add the company
    private int modelIndex;

    public ObservableList<DebtListModel> listModels;

    public SettingsController() {
        instance=this;
    }

    private String[][] model=null;

    @FXML
    public void initialize() {

        toolBar.setTitle("Settings");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        debtList.setCellFactory(lv->new DebtListCell());
        debtList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listModels=FXCollections.observableList(CreditCompanyList.getDetailCompanyList());
        debtList.setItems(listModels);

//      Select First item by default so that we do not have to check for list selection
        debtList.getSelectionModel().select(0);
        task=new GetSettingsTask();
        task.setOnSucceeded((WorkerStateEvent e)->{
            model=task.getValue();
            loadSettings(model);
        });
        Thread mainThread=new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();

    }

    /*
    * This is function is called when user clicks "ADD" button
    *
    * A dialog is loaded where he fills the form and the company is added
    *
    * */


    public void loadSettings(String[][] model) {

        usernameValue.setText(model[0][0]);
        usernameDate.setText(model[0][1]);

        dRateValue.setText(model[1][0]);
        dRateDate.setText(model[1][1]);

        pRateValue.setText(model[2][0]);
        pRateDate.setText(model[2][1]);

        dMeterValue.setText(model[3][0]);
        dMeterDate.setText(model[3][1]);

        pMeterValue.setText(model[4][0]);
        pMeterDate.setText(model[4][1]);

        counterStockValue.setText(model[5][0]);
        counterStockDate.setText(model[5][1]);

        dieselStockValue.setText(model[6][0]);
        dieselStockDate.setText(model[6][1]);

        petrolStockValue.setText(model[7][0]);
        petrolStockDate.setText(model[7][1]);

        billPrinter.setText(model[8][0]);
        billPrinterDate.setText(model[8][1]);

        analyticsPrinter.setText(model[9][0]);
        analyticsPrinterDate.setText(model[9][1]);
    }

    public String[][] getModel() {
        return model;
    }


    @FXML
    public void addCompany(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/changeCompany.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    /*
    * This function is called when user clicks 'DELETE' Button
    *
    * Get the currently selected company and remove from database as well as from list
    *
    * */

    @FXML
    public void deleteCompany(MouseEvent e) {
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();
        if(model!=null) {
            listModels.remove(model);
            RemoveCompanyTask task=new RemoveCompanyTask(model);
            TaskManager.executeTask(task);

            /*if(DatabaseHandler.deleteCompany(model)) {
                SystemLogging.getLogger().fine("Company Deleted SuccessFully.");
            } else {
                SystemLogging.getLogger().severe("Error Deleting Companies!");
            }*/
        }


    }

    /*
    * This function is called when use clicks 'EDIT'
    *
    * Get the current company info and pass into ChangeCompany class for editing
    * */

    @FXML
    public void editCompany(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/changeCompany.fxml"));
        Parent root=loader.load();
        ChangeCompanyController controller=loader.getController();
        modelIndex=debtList.getSelectionModel().getSelectedIndex();
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();
        controller.setEditData(model);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();

    }

    /*
     * Load the dialog to get the new change rate
     *
     * */

    @FXML
    public void changeOilRates(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/changeRateDialog.fxml"));

        Parent root=loader.load();
        ChangeRateController controller=loader.getController();
        controller.setInitialRate(dRateValue.getText(),pRateValue.getText());

        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupCounterStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/counterStockDialog.fxml"));

        Parent root=loader.load();
        CounterStockDialogController controller=loader.getController();
        controller.setInitialStock(counterStockValue.getText());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupMeterReadings(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/setupMeterReadings.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void setupPrinters(MouseEvent e) throws IOException  {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/printerDialog.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void setupOilStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/oilStockDialog.fxml"));

        Parent root=loader.load();
        OilStockDialogController controller=loader.getController();
        controller.setInitialStock(dieselStockValue.getText(),petrolStockValue.getText());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /*
    * This function is called when user clicks 'Change password'
    *
    * Just load the login and handle future details
    *
    * */

    @FXML
    public void changeUserNameAndPassword(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/changeUserParameters.fxml"));
        Parent root=loader.load();

        ChangeUserParametersController controller=loader.getController();
        controller.setUserName(usernameValue.getText());

        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void petrolPumpExpenditure(MouseEvent mouseEvent) throws IOException {
        /**
         * Petrol Pump expenditure code goes here
         */
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/addExpenditure.fxml"));
        Parent root=loader.load();

        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void clearExpenditure(MouseEvent e) {
        ClearExpenditureTask task=new ClearExpenditureTask();
        TaskManager.executeTask(task);
    }


    public void editModel(DebtListModel model) {
        listModels.remove(modelIndex);
        listModels.add(modelIndex,model);
    }




    public static SettingsController getInstance() {
        return instance;
    }

}
