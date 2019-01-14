package np.com.spirrotech.application.settings.dialogs.change_meter_reading;

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
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.model.SettingsTask;

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

        bottomBar.setButtonText("SAVE");
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

            SettingsTask task = new SettingsTask(new String[] {fDiesel.getText(),fPetrol.getText()}, new int[] {SettingsConstants.DIESEL_METER_READING, SettingsConstants.PETROL_METER_READING}, SettingsConstants.CHANGE_METER_READING);
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {
                    Platform.runLater(()->{
                        String[][] model=SettingsController.getInstance().getModel();
                        model[3][0]=fDiesel.getText();
                        model[3][1]=SystemStatus.getNepaliCurrentDate();

                        model[4][0]=fPetrol.getText();
                        model[4][1]=SystemStatus.getNepaliCurrentDate();

                        SettingsController.getInstance().loadSettings(model);
                        ((Node)e.getSource()).getScene().getWindow().hide();
                    });
                }

                @Override
                public void onError(String msg) {

                }

                @Override
                public void updateProgress(double current, double total) {

                }
            });
            TaskManager.getInstance().executeTask(task);


        }
    }
}
