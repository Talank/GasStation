package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginDialogController {

    @FXML
    private JFXButton cancel,login;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    public void initialize() {
        RequiredFieldValidator usernameValidator=new RequiredFieldValidator();
        usernameValidator.setMessage("UserName Cannot Be Empty");
        username.getValidators().add(usernameValidator);

        RequiredFieldValidator passwordValidator=new RequiredFieldValidator();
        passwordValidator.setMessage("Password Cannot Be Empty");
        password.getValidators().add(passwordValidator);
    }

    @FXML
    public void loginUser(MouseEvent e) throws IOException {

    }

    @FXML
    public void cancelLogin(MouseEvent e) throws IOException {
        Stage loginWindow=(Stage)((Node)e.getSource()).getScene().getWindow();
        loginWindow.close();
    }

}
