package sample.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.model.DatabaseHandler;

import java.io.IOException;

public class ChangeUserParametersController {

    @FXML
    private JFXTextField userName;

    @FXML
    private JFXPasswordField password,conformPassword;

    @FXML
    public void initialize() {
        RequiredFieldValidator userNameValidator=new RequiredFieldValidator();
        userNameValidator.setMessage("User Name Cannot Be Empty");
        userName.getValidators().add(userNameValidator);

        RequiredFieldValidator passwordValidator=new RequiredFieldValidator();
        passwordValidator.setMessage("Password Cannot Be Empty");
        password.getValidators().add(passwordValidator);

        RequiredFieldValidator conformPasswordValidator=new RequiredFieldValidator();
        conformPasswordValidator.setMessage("Password Does not Match");
        conformPassword.getValidators().add(conformPasswordValidator);

        userName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.trim().isEmpty()) {
                userName.validate();
            } else {
                userName.resetValidation();
            }
        });

        password.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.trim().isEmpty()) {
                password.validate();
            } else {
                password.resetValidation();
            }
        });


        conformPassword.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!password.getText().equals(newValue)) {
                conformPassword.validate();
            } else {
                conformPassword.resetValidation();
            }

        });


    }

    @FXML
    public void saveUserParameter(MouseEvent e) {
        int status=0;
        if(userName.getText().trim().isEmpty()) {
            userName.validate();
            status++;
        }

        if(password.getText().trim().isEmpty()) {
            password.validate();
            status++;
        }
        if(!conformPassword.getText().trim().equals(password.getText().trim())) {
            conformPassword.validate();
            status++;
        }

        if(status==0) {
            if(DatabaseHandler.setAdminParameters(userName.getText().trim(),password.getText().trim())) {
                SystemLogging.getLogger().fine("Admin Parameter Has Been Changed Successfully.");
                ((Node)e.getSource()).getScene().getWindow().hide();
            } else {
                SystemLogging.getLogger().severe("Error Changing Admin Parameters!");
            }
        }
    }

    @FXML
    public void cancel(MouseEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }


}
