package np.com.spirrotech.application.settings.dialogs.change_oil_stock;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.application.home.AppHomeController;
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

public class OilStockDialogController implements BottomBarInterface {

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private DialogToolBar toolBar;


    @FXML
    private JFXTextField petrolStockValue,dieselStockValue;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Setup Oil Stock");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator petrolStockValidator=new RequiredFieldValidator();
        petrolStockValidator.setMessage("Petrol Stock Cannot Be Empty");
        petrolStockValue.getValidators().add(petrolStockValidator);

        petrolStockValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                petrolStockValue.setText(oldValue);
            else
            if(newValue.isEmpty())
                petrolStockValue.validate();
            else
                petrolStockValue.resetValidation();
        });


        RequiredFieldValidator dieselStockValidator=new RequiredFieldValidator();
        dieselStockValidator.setMessage("Diesel Stock Cannot Be Empty");
        dieselStockValue.getValidators().add(dieselStockValidator);

        dieselStockValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                dieselStockValue.setText(oldValue);
            else
            if(newValue.isEmpty())
                dieselStockValue.validate();
            else
                dieselStockValue.resetValidation();
        });
    }


    @Override
    public void performAction(MouseEvent e) throws IOException {

        int status=0;

        if(petrolStockValue.getText().isEmpty()) {
            petrolStockValue.validate();
            status++;
        }

        if(dieselStockValue.getText().isEmpty()) {
            dieselStockValue.validate();
            status++;
        }

        if(status==0) {

            SettingsTask task = new SettingsTask(new String[] {dieselStockValue.getText(),petrolStockValue.getText()}, new int[] {SettingsConstants.DIESEL_STOCK, SettingsConstants.PETROL_STOCK}, SettingsConstants.CHANGE_OIL_STOCK);
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {
                    Platform.runLater(()->{
                        String[][] model=SettingsController.getInstance().getModel();

                        model[6][0]=dieselStockValue.getText();
                        model[6][1]=SystemStatus.getNepaliCurrentDate();

                        model[7][0]=petrolStockValue.getText();
                        model[7][1]=SystemStatus.getNepaliCurrentDate();

                        SystemStatus.setPetrolStock(Float.parseFloat(petrolStockValue.getText()));
                        SystemStatus.setDieselStock(Float.parseFloat(dieselStockValue.getText()));

                        SettingsController.getInstance().loadSettings(model);
                        AppHomeController.getInstance().setStock();
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

    public void setInitialStock(String dieselStock,String petrolStock) {
        dieselStockValue.setText(dieselStock);
        petrolStockValue.setText(petrolStock);
    }
}
