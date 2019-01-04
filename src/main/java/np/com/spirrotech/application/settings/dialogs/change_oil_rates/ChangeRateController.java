package np.com.spirrotech.application.settings.dialogs.change_oil_rates;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.application.home.AppHomeController;

import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SettingsConstants;

import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;

import javafx.scene.Node;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;

import np.com.spirrotech.application.settings.SettingsTask;

import java.io.IOException;

public class ChangeRateController implements BottomBarInterface {

    @FXML
    private JFXTextField rDiesel,rPetrol;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;


    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Change Rate");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Rate Cannot Be Empty");
        rDiesel.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Rate Cannot Be Empty");
        rPetrol.getValidators().add(petrolValidator);

        rDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?"))
                rDiesel.setText(oldValue);
            else
                if(newValue.isEmpty())
                    rDiesel.validate();
                else
                    rDiesel.resetValidation();
        });

        rPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?"))
                rPetrol.setText(oldValue);
            else
                if(newValue.isEmpty())
                    rPetrol.validate();
                else
                    rPetrol.resetValidation();
        });

    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        if(rDiesel.getText().isEmpty())
            rDiesel.validate();
        if(rPetrol.getText().isEmpty())
            rPetrol.validate();
        if(!rPetrol.getText().isEmpty()&&!rDiesel.getText().isEmpty()) {

            if (SystemStatus.getdRate() != Float.parseFloat(rDiesel.getText())||SystemStatus.getpRate() != Float.parseFloat(rPetrol.getText())) {
                SettingsTask task = new SettingsTask(new String[] {rDiesel.getText(), rPetrol.getText()}, new int[] {SettingsConstants.DIESEL_RATE, SettingsConstants.PETROL_RATE}, SettingsConstants.CHANGE_OIL_RATE);
                task.addTaskInterfaceListener(new TaskInterface() {
                    @Override
                    public void onComplete(Object data) {
                        Platform.runLater(()-> {
                            SystemStatus.setpRate(Float.parseFloat(rPetrol.getText()));
                            SystemStatus.setdRate(Float.parseFloat(rDiesel.getText()));

                            String[][] model=SettingsController.getInstance().getModel();

                            model[1][0]=rDiesel.getText();
                            model[1][1]=SystemStatus.getNepaliCurrentDate();

                            model[2][0]=rPetrol.getText();
                            model[2][1]=SystemStatus.getNepaliCurrentDate();
                            SettingsController.getInstance().loadSettings(model);
                            AppHomeController.getInstance().setRates();
                            ((Node)e.getSource()).getScene().getWindow().hide();
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
                TaskManager.getInstance().executeTask(task);
            } else {
                ((Node)e.getSource()).getScene().getWindow().hide();
            }

        }
    }

    public void setInitialRate(String dRate,String pRate) {
        rDiesel.setText(dRate);
        rPetrol.setText(pRate);
    }

}
