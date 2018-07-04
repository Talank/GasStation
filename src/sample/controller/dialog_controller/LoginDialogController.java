package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.OutputTasks.GetAdminUser;
import sample.controller.AppHomeController;
import sample.controller.main_home_controller.MainHomeController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.AdminUser;
import sample.tasks.TaskManager;

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

    private Stage stage;

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
        int status=0;

        if(username.getText().trim().isEmpty()) {
            username.validate();
            status++;
        }

        if(password.getText().isEmpty()) {
            password.validate();
            status++;
        }


        if(status==0) {
            AdminUser user=new AdminUser();
            user.initializeAdmin(username.getText().trim(),password.getText().trim());
            GetAdminUser task=new GetAdminUser();
            task.setOnSucceeded((WorkerStateEvent e1)->{
                try {
                    if(user.validate(task.getValue())) {
                        switch (this.status) {
                            case 1:

                                BorderPane borderPane=FXMLLoader.load(getClass().getResource("../../view/main_home_views/settings.fxml"));
                                AppHomeController.getInstance().loadStages(borderPane);

//                                MainHomeController.getInstance().showSettings(stage);
                                break;
                        }
                        Stage loginWindow=(Stage)((Node)e.getSource()).getScene().getWindow();
                        loginWindow.close();

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
                } catch(IOException e2) {
                    e2.printStackTrace();
                }

            });

            TaskManager.executeInstantTask(task);

        }
    }

    public void setStage(Stage stage) {
        this.stage=stage;
    }

    public void setStatus(int status) {
        this.status=status;
    }

}
