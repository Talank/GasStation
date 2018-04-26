package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.AdminUser;

import java.io.IOException;

public class LoginDialogController implements BottomBarInterface {


    @FXML
    private JFXTextField username;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private JFXPasswordField password;

    @FXML
    private DialogBottomBar bottomBar;

    private int status=0;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Login");

        bottomBar.setButtonText("LOGIN");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator usernameValidator=new RequiredFieldValidator();
        usernameValidator.setMessage("UserName Cannot Be Empty");
        username.getValidators().add(usernameValidator);

        RequiredFieldValidator passwordValidator=new RequiredFieldValidator();
        passwordValidator.setMessage("Password Cannot Be Empty");
        password.getValidators().add(passwordValidator);

        username.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty())
                username.validate();
            else
                username.resetValidation();
        });

        password.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty())
                password.validate();
            else
                password.resetValidation();
        });


    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        if(username.getText().isEmpty())
            username.validate();

        if(password.getText().isEmpty())
            password.validate();

        if(!password.getText().isEmpty()&&!username.getText().isEmpty()) {
            AdminUser user=new AdminUser();
            user.initializeAdmin(username.getText().trim(),password.getText().trim());
            if(user.validate()) {
                Stage loginWindow=(Stage)((Node)e.getSource()).getScene().getWindow();
                loginWindow.close();
                Parent root;
                switch (status) {
                    case 0:
                        root =FXMLLoader.load(getClass().getResource("../../view/dialogs/changeRateDialog.fxml"));
                        break;
                    case 1:
                        root =FXMLLoader.load(getClass().getResource("../../view/dialogs/changeUserParameters.fxml"));
                        break;
                    default:
                        root =FXMLLoader.load(getClass().getResource("../../view/dialogs/changeRateDialog.fxml"));
                        break;
                }

                Stage stage=new Stage();
                Scene scene=new Scene(root);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.show();
            } else {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/errorDialog.fxml"));
                Parent root=loader.load();
                ErrorDialogController controller=loader.getController();
                controller.setTitle("Login Error");
                controller.setErrorMessage("Invalid Username and Password Provided.Please Try Again Later.");
                Stage stage=new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            }
        }
    }

    public void setStatus(int status) {
        this.status=status;
    }

}
