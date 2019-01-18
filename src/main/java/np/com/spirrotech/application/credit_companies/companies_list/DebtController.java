package np.com.spirrotech.application.credit_companies.companies_list;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;


import javafx.scene.control.SelectionMode;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import np.com.spirrotech.application.home.AppHomeController;


import np.com.spirrotech.application.credit_companies.company_transactions.CreditCompanyDetailsController;

import np.com.spirrotech.library.toolbar.ToolBar;
import np.com.spirrotech.dao.CreditCompanyList;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.dao.SystemStatus;

import java.io.IOException;
import java.util.ArrayList;


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

    @FXML
    private JFXTextField searchCompany;

    private static DebtController instance;

    private ArrayList<DebtListModel> companyList;

    private ObservableList<DebtListModel> observableList;

    public DebtController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("Credit Company Lists");

        //set the listener to the back button click
        toolBar.setBackButtonInterface(() -> {
            BorderPane borderPane=FXMLLoader.load(getClass().getClassLoader().getResource(SystemStatus.getMainHomePath()));
            AppHomeController.getInstance().loadStages(borderPane);
        });

      //  toolBar.setBackPath(SystemStatus.getMainHomePath());

        debtList.setCellFactory(lv->new CreditCompanyListCell());
        debtList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        RequiredFieldValidator itemNameValidator=new RequiredFieldValidator();
        itemNameValidator.setMessage("Company name cannot be empty");
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

        debtList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DebtListModel> observable, DebtListModel oldValue, DebtListModel newValue)->{
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/credit_companies/company_transactions/creditCompanyDetails.fxml"));

            try {

                BorderPane borderPane=loader.load();

                AppHomeController.getInstance().loadStages(borderPane);
                CreditCompanyDetailsController controller=loader.getController();
                controller.setParameters(newValue.getCompanyName());

            } catch (IOException e) {
                e.printStackTrace();
            }


        });
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


    public static DebtController getInstance() {
        return instance;
    }

}
