package np.com.spirrotech.application.settings.dialogs.change_user_parameter;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SettingsConstants;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.dao.AdminUser;
import np.com.spirrotech.dao.SystemStatus;

import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;

import np.com.spirrotech.model.SettingsTask;

import java.io.IOException;

public class ChangeUserParametersController implements BottomBarInterface {

    @FXML
    private JFXTextField userName;

    @FXML
    private JFXPasswordField password,conformPassword;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Change User Parameters");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

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

    @Override
    public void performAction(MouseEvent e) throws IOException {
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


            SettingsTask task = new SettingsTask(new String[] {userName.getText().trim(),(new AdminUser()).generatePasswordHash(password.getText().trim())}, new int[] {SettingsConstants.USER_NAME,SettingsConstants.PASSWORD}, SettingsConstants.CHANGE_USER_PARAMS);
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {
                    Platform.runLater(()->{
                        TaskManager.getInstance().executeTask(task);

                        String[][] model=SettingsController.getInstance().getModel();

                        model[0][0]=userName.getText();
                        model[0][1]=SystemStatus.getNepaliCurrentDate();

                        SettingsController.getInstance().loadSettings(model);
                        ((Node)e.getSource()).getScene().getWindow().hide();
                    });
                }

                @Override
                public void onError() {

                }
            });
            TaskManager.getInstance().executeTask(task);

        }
    }

    public void setUserName(String name) {
        userName.setText(name);
    }



}
