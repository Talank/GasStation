package sample.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.model.DatabaseHandler;
import sample.model.DebtListModel;

import java.sql.Date;


public class ChangeCompanyController {

    @FXML
    private JFXTextField companyName,creditCycle;

    @FXML
    private Label label;

    private boolean isEditable=false;

    private DebtListModel model;

    @FXML
    public void initialize() {

        label.setText("Add New Company");

        RequiredFieldValidator nameValidator=new RequiredFieldValidator();
        nameValidator.setMessage("Company Name Cannot Be Empty");
        companyName.getValidators().add(nameValidator);

        RequiredFieldValidator cycleValidator=new RequiredFieldValidator();
        cycleValidator.setMessage("Cycle Time Cannot Be Empty");
        creditCycle.getValidators().add(cycleValidator);

        companyName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z]*")) {
                companyName.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    companyName.validate();
                } else {
                    companyName.resetValidation();
                }
            }
        });

        creditCycle.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,2}")) {
                creditCycle.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    creditCycle.validate();
                } else {
                    creditCycle.resetValidation();
                }
            }
        });

    }

    @FXML
    public void saveCompany(MouseEvent e) {
        int status=0;
        if(companyName.getText().trim().isEmpty()) {
            companyName.validate();
            status++;
        }


        if(creditCycle.getText().trim().isEmpty()) {
            creditCycle.validate();
            status++;
        }

        if(status==0) {
            if(!isEditable) {
                int id=DatabaseHandler.saveCompany(companyName.getText().trim(),creditCycle.getText());
                if(id!=-1) {
                    SystemLogging.getLogger().fine("Company Has Been Added.");
                    SettingsController.getInstance().listModels.add(new DebtListModel(companyName.getText().trim(),(new Date((new java.util.Date()).getTime())).toString(),creditCycle.getText(),id));
                    ((Node)e.getSource()).getScene().getWindow().hide();
                } else {
                    SystemLogging.getLogger().severe("Error Adding Company!");
                }
            } else {
                model.setCompanyName(companyName.getText().trim());
                model.setDays(creditCycle.getText());
                if(DatabaseHandler.updateCompany(model)) {
                    SystemLogging.getLogger().fine("Company Details Has Been Changed");
                    SettingsController.getInstance().editModel(model);
                    ((Node)e.getSource()).getScene().getWindow().hide();
                } else {
                    SystemLogging.getLogger().fine("Error Changing Company Details!");
                }
            }
        }
    }

    @FXML
    public void cancel(MouseEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public void setEditData(DebtListModel model) {
        label.setText("Edit Company");
        isEditable=true;
        this.companyName.setText(model.getCompanyName());
        this.creditCycle.setText(model.getDays());
        this.model=model;
    }

}
