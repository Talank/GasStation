package np.com.spirrotech.application.settings;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import np.com.spirrotech.application.credit_companies.companies_list.CreditCompanyListCell;
import np.com.spirrotech.application.credit_companies.company_transactions.CreditCompanyDetailsController;
import np.com.spirrotech.application.home.AppHomeController;
import np.com.spirrotech.application.settings.dialogs.change_counter_stock.CounterStockDialogController;
import np.com.spirrotech.application.settings.dialogs.change_oil_rates.ChangeRateController;
import np.com.spirrotech.application.settings.dialogs.change_oil_stock.OilStockDialogController;
import np.com.spirrotech.application.settings.dialogs.change_user_parameter.ChangeUserParametersController;
import np.com.spirrotech.application.settings.dialogs.company_payment.PaymentDialogController;
import np.com.spirrotech.application.settings.dialogs.delete_company.DeleteConformationController;
import np.com.spirrotech.application.settings.dialogs.edit_company.EditCompanyController;


import np.com.spirrotech.library.SettingsConstants;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.library.conformation_dialog.ConformationDialogController;
import np.com.spirrotech.library.toolbar.ToolBar;
import np.com.spirrotech.dao.CreditCompanyClosingBill;
import np.com.spirrotech.dao.CreditCompanyList;
import np.com.spirrotech.dao.SystemStatus;

import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.model.ClearTableTask;
import np.com.spirrotech.model.GetSettingsTask;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;

import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.utils.tasks.Tasks;

import java.io.IOException;
import java.util.ArrayList;


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

    private ArrayList<DebtListModel> companyList;

    private ObservableList<DebtListModel> observableList;

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
    private JFXTextField searchCompany;

    @FXML
    public void initialize() {

        toolBar.setTitle("Settings");
        toolBar.setBackButtonInterface(() -> {
            BorderPane borderPane=FXMLLoader.load(getClass().getClassLoader().getResource(SystemStatus.getMainHomePath()));
            AppHomeController.getInstance().loadStages(borderPane);
        });

        precision = new Precision();
        priceFormat = new PriceFormat();

        debtList.setCellFactory(lv->new DebtListCell());
        debtList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listModels=FXCollections.observableList(CreditCompanyList.getDetailCompanyList());
        debtList.setItems(listModels);


//      Select First item by default so that we do not have to check for list selection
        debtList.getSelectionModel().select(0);
        task=new GetSettingsTask();

        task.addTaskInterfaceListener(new TaskInterface() {
            @Override
            public void onComplete(Object data) {
                Platform.runLater(() -> {
                    model = (String[][]) data;
                    loadSettings(model);
                });

            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void updateProgress(double current, double total) {

            }
        });

        TaskManager.getInstance().executeInstantTask(task);






        //Validate the input and initialize the debt list
        RequiredFieldValidator itemNameValidator=new RequiredFieldValidator();
        //itemNameValidator.setMessage("Company name cannot be empty");
        searchCompany.getValidators().add(itemNameValidator);


        searchCompany.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z ]*")) {
                searchCompany.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    searchCompany.validate();
                } else {
                    searchCompany.resetValidation();
                }
            }
        });

        //initialize the company list

        companyList = new ArrayList<>();

        //make shallow copy of the list, so that we can independently manipulate the list
        for (DebtListModel model : CreditCompanyList.getDetailCompanyList()) {
            companyList.add(model);
        }

        observableList = FXCollections.observableList(companyList);


        debtList.setItems(observableList);



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
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("application/settings/dialogs/add_company/changeCompany.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.setTitle("Add company");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/delete_company/deleteConformationDialog.fxml"));
            Parent root=loader.load();
            DeleteConformationController controller=loader.getController();
            controller.initializeData(model,listModels);
            controller.showBalance();
            Stage stage=new Stage();
            stage.setTitle("Delete Company");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/edit_company/editCompany.fxml"));
            Parent root=loader.load();
            EditCompanyController controller=loader.getController();
            controller.setEditData(model);
            Stage stage=new Stage();
            stage.setTitle("Edit Company");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
    public void closeCompany(MouseEvent e) throws IOException {

        //show the conformation dialog

        DebtListModel model=debtList.getSelectionModel().getSelectedItem();

        if (model != null) {

            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("library/conformation_dialog/conformationDialog.fxml"));
            Parent root=loader.load();
            ConformationDialogController controller=loader.getController();

            //hook the event listener
            controller.setConformListener((event) -> {

                //hide the dialog
                ((Node)event.getSource()).getScene().getWindow().hide();

                //print the bill
                CreditCompanyClosingBill bill=new CreditCompanyClosingBill(model);
                if (bill.printBill()) {
                    showSnackMessage("Company Closed. Printing Bill!");
                } else {
                    showSnackMessage("Printer Error: Couldn't Close Company Transaction!!");
                }
            });

            controller.setTitle("Conformation");
            controller.setConformationLabel("The company will be closed?");

            Stage stage=new Stage();
            stage.setTitle("Close Company");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } else {
            showSnackMessage("No Company Selected!");
        }

    }

    @FXML
    public void payCompany(MouseEvent e) throws IOException {
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();

        if (model != null) {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/company_payment/paymentDialog.fxml"));
            Parent root=loader.load();

            PaymentDialogController controller=loader.getController();
            controller.setModel(model);

            Stage stage=new Stage();
            stage.setTitle("Payment");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/change_oil_rates/changeRateDialog.fxml"));

        Parent root=loader.load();
        ChangeRateController controller=loader.getController();
        controller.setInitialRate(model[1][0],model[2][0]);

        Stage stage=new Stage();
        stage.setTitle("Oil Rates");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupCounterStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/change_counter_stock/counterStockDialog.fxml"));

        Parent root=loader.load();
        CounterStockDialogController controller=loader.getController();
        controller.setInitialStock(model[5][0]);
        Stage stage=new Stage();
        stage.setTitle("Counter Setup");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void setupMeterReadings(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("application/settings/dialogs/change_meter_reading/setupMeterReadings.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.setTitle("Setup Meters");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void setupPrinters(MouseEvent e) throws IOException  {
        Parent root=FXMLLoader.load(getClass().getClassLoader().getResource("application/settings/dialogs/change_printer/printerDialog.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.setTitle("Printer setup");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void setupOilStock(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/change_oil_stock/oilStockDialog.fxml"));

        Parent root=loader.load();
        OilStockDialogController controller=loader.getController();
        controller.setInitialStock(model[6][0], model[7][0]);
        Stage stage=new Stage();
        stage.setTitle("Oil Stocks");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/change_user_parameter/changeUserParameters.fxml"));
        Parent root=loader.load();

        ChangeUserParametersController controller=loader.getController();
        controller.setUserName(usernameValue.getText());

        Stage stage=new Stage();
        stage.setTitle("User Credentials");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/dialogs/add_expenditure/addExpenditure.fxml"));
        Parent root=loader.load();

        Stage stage=new Stage();
        stage.setTitle("Expenditure");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
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





    /**
     *Called in every key press
     */
    @FXML
    void findCompany(KeyEvent event) {

        if (searchCompany.getText().isEmpty()){
            searchAndAddCompany((""));
            return;
        }

        //get the current character
        if (event.getCharacter().equals("\b") && searchCompany.getText().equals("")) {//check if backspace is pressed and the search field is empty i.e. it is completely cleared, then load all the company list
            searchAndAddCompany("");
        } else if (event.getCharacter().equals("\b")) {// if only backspace is pressed then search excluding the backspace
            searchAndAddCompany(searchCompany.getText());
        } else {//if other keys are pressed then take into account the current TextField data and the current key press
            searchAndAddCompany(searchCompany.getText() + event.getCharacter());//when this event is fired, the TextField.getText() returns character excluding this key press as, it is slow to respond, so take into account current key press
        }

    }



    private void searchAndAddCompany(String text) {

        debtList.setItems(null);//clear the list

        companyList.clear();//clear the array list

        for (DebtListModel model : CreditCompanyList.getDetailCompanyList()) {//loop through all the companies
            //if (model.getCompanyName().startsWith(text)) {//text match
            if (model.getCompanyName().toLowerCase().contains(text.toLowerCase())) {//text match
                companyList.add(model);//add to the model
            } else {// no text match
                companyList.remove(model);//remove from the list
            }
        }

        debtList.setItems(observableList);//refresh the list

    }

}
