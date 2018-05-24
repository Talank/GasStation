package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sample.controller.main_home_controller.SettingsController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SetupMeterReadingsTask;

import java.io.IOException;

public class SetupMeterReadingsController implements BottomBarInterface {


    @FXML
    private JFXTextField fDiesel,fPetrol;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;


    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Final Meter Readings");

        bottomBar.setButtonText("START");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Reading Cannot Be Empty");
        fDiesel.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Reading Cannot Be Empty");
        fPetrol.getValidators().add(petrolValidator);


        fDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                fDiesel.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    fDiesel.validate();
                } else {
                    fDiesel.resetValidation();
                }
            }
        });

        fPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                fPetrol.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    fPetrol.validate();
                } else {
                    fPetrol.resetValidation();
                }
            }
        });

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {
        int status=0;
        if(fPetrol.getText().isEmpty()) {
            fPetrol.validate();
            status++;
        }

        if(fDiesel.getText().isEmpty()) {
            fDiesel.validate();
            status++;
        }

        if(status==0) {

            String[][] model=SettingsController.getInstance().getModel();
            model[3][0]=fDiesel.getText();
            model[3][1]=SystemStatus.getNepaliCurrentDate();

            model[4][0]=fPetrol.getText();
            model[4][1]=SystemStatus.getNepaliCurrentDate();

            SettingsController.getInstance().loadSettings(model);

            SetupMeterReadingsTask task=new SetupMeterReadingsTask(fDiesel.getText(),fPetrol.getText());
            TaskManager.executeTask(task);

            ((Node)e.getSource()).getScene().getWindow().hide();

        }
    }
}
