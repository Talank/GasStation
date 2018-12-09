package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.OutputTasks.GetSettingsTask;
import sample.controller.dialog_controller.*;


import sample.controller.main_home_controller.lists_controllers.DebtListCell;

import sample.library.SettingsConstants;
import sample.library.SnackBarToastGenerator;
import sample.library.toolbar.ToolBar;
import sample.model.CreditCompanyClosingBill;
import sample.model.CreditCompanyList;
import sample.model.SystemStatus;

import sample.model.DebtListModel;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;

import sample.tasks.TaskManager;
import sample.tasks.Tasks;
import sample.tasks.database_tasks.*;

import java.io.IOException;
import java.sql.SQLException;


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

    private Precision precision;

    private PriceFormat priceFormat;

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

        precision = new Precision();
        priceFormat = new PriceFormat();

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
        TaskManager.getInstance().executeInstantTask(task);

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

        dRateValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[1][0]))));
        dRateDate.setText(model[1][1]);

        pRateValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[2][0]))));
        pRateDate.setText(model[2][1]);

        dMeterValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[3][0]))));
        dMeterDate.setText(model[3][1]);

        pMeterValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[4][0]))));
        pMeterDate.setText(model[4][1]);

        counterStockValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[5][0]))));
        counterStockDate.setText(model[5][1]);

        dieselStockValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[6][0]))));
        dieselStockDate.setText(model[6][1]);

        petrolStockValue.setText(priceFormat.format(precision.format(Float.parseFloat(model[7][0]))));
        petrolStockDate.setText(model[7][1]);

        billPrinter.setText(model[8][0]);
        billPrinterDate.setText(model[8][1]);

        analyticsPrinter.setText(model[9][0]);
        analyticsPrinterDate.setText(model[9][1]);
    }

    public String[][] getModel() {
        return model;
    }

    public void removeModel(DebtListModel model) {
        listModels.remove(model);
    }

    private void showSnackMessage(String msg) {
        SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator(msg,1500);
        snackBarToastGenerator.show();
    }

    @FXML
    public void addCompany(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/dialogs/changeCompany.fxml"));
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
    public void deleteCompany(MouseEvent e) throws IOException {
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();
        if(model!=null) {
            /*
            *Here, a conformation dialog is displayed saying
            * "Are you sure want to delete THIS?
             */

            //load the conformation dialog
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/deleteConformationDialog.fxml"));
            Parent root=loader.load();
            DeleteConformationController controller=loader.getController();
            controller.initializeData(model,listModels);
            Stage stage=new Stage();
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();


            //listModels.remove(model);
            //performTask(new RemoveCompanyTask(model));
        } else {
            showSnackMessage("No Company Selected!");
        }
    }

    /*
    * This function is called when use clicks 'EDIT'
    *
    * Get the current company info and pass into ChangeCompany class for editing
    * */

    @FXML
    public void editCompany(MouseEvent e) throws IOException {

        modelIndex=debtList.getSelectionModel().getSelectedIndex();
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();

        //check if the model is null, if a dialog was shown before the selection might get cleared
        if (model != null) {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/editCompany.fxml"));
            Parent root=loader.load();
            EditCompanyController controller=loader.getController();
            controller.setEditData(model);
            Stage stage=new Stage();
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            showSnackMessage("No Company Selected!");
        }



    }

    public void refreshList(DebtListModel model) {
        System.out.println("Trying To Refresh");
        Platform.runLater(()->debtList.getItems().set(debtList.getItems().indexOf(model),model));
    }

    @FXML
    public void closeCompany(MouseEvent e) {

        DebtListModel model=debtList.getSelectionModel().getSelectedItem();

        if (model != null) {
            CreditCompanyClosingBill bill=new CreditCompanyClosingBill(model);
            if (bill.printBill()) {
                showSnackMessage("Company Closed. Printing Bill!");
            } else {
                showSnackMessage("Couldn't Close Company Transaction!!");
            }
        }





    }

    @FXML
    public void payCompany(MouseEvent e) throws IOException {
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();

        if (model != null) {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/paymentDialog.fxml"));
            Parent root=loader.load();

            PaymentDialogController controller=loader.getController();
            controller.setModel(model);

            Stage stage=new Stage();
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            showSnackMessage("No Company Selected!");
        }


    }


    /*
     * Load the dialog to get the new change rate
     *
     * */

    @FXML
    public void changeOilRates(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/changeRateDialog.fxml"));

        Parent root=loader.load();
        ChangeRateController controller=loader.getController();
        controller.setInitialRate(model[1][0],model[2][0]);

        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupCounterStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/counterStockDialog.fxml"));

        Parent root=loader.load();
        CounterStockDialogController controller=loader.getController();
        controller.setInitialStock(model[5][0]);
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupMeterReadings(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/dialogs/setupMeterReadings.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void setupPrinters(MouseEvent e) throws IOException  {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/dialogs/printerDialog.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void setupOilStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/oilStockDialog.fxml"));

        Parent root=loader.load();
        OilStockDialogController controller=loader.getController();
        controller.setInitialStock(model[6][0], model[7][0]);
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
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
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/changeUserParameters.fxml"));
        Parent root=loader.load();

        ChangeUserParametersController controller=loader.getController();
        controller.setUserName(usernameValue.getText());

        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void clearDailyAnalytics(MouseEvent event) {
        performTask(new ClearTableTask(SettingsConstants.DAILY_ANALYTICS_TABLE, SettingsConstants.CLEAR_DAILY_ANALYTICS));
    }

    @FXML
    void clearDieselRefill(MouseEvent event) {
        performTask(new ClearTableTask(SettingsConstants.DIESEL_REFILL_TABLE, SettingsConstants.CLEAR_DIESEL_REFILL));
    }

    @FXML
    void clearPetrolRefill(MouseEvent event) {
        performTask(new ClearTableTask(SettingsConstants.PETROL_REFILL_TABLE, SettingsConstants.CLEAR_PETROL_REFILL));
    }

    @FXML
    public void petrolPumpExpenditure(MouseEvent e) throws IOException {
        /**
         * Petrol Pump expenditure code goes here
         */
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/addExpenditure.fxml"));
        Parent root=loader.load();

        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void clearExpenditure(MouseEvent e) {
        performTask(new ClearTableTask(SettingsConstants.EXPENDITURE_TABLE, SettingsConstants.CLEAR_EXPENDITURE));
    }


    public void editModel(DebtListModel model) {
        listModels.remove(modelIndex);
        listModels.add(modelIndex,model);
    }

    private void performTask(Tasks tasks) {
        TaskManager.getInstance().executeTask(tasks);
    }





    public static SettingsController getInstance() {
        return instance;
    }

}
