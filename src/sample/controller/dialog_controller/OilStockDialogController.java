package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.SettingsController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SetupOilStockTask;

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

        bottomBar.setButtonText("SETUP");
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
            SetupOilStockTask task=new SetupOilStockTask(dieselStockValue.getText(),petrolStockValue.getText());
            TaskManager.executeTask(task);

            String[][] model=SettingsController.getInstance().getModel();

            model[6][0]=dieselStockValue.getText();
            model[6][1]=SystemStatus.getCurrentDate();

            model[7][0]=petrolStockValue.getText();
            model[7][1]=SystemStatus.getCurrentDate();


            SettingsController.getInstance().loadSettings(model);
            ((Node)e.getSource()).getScene().getWindow().hide();


            /*if(DatabaseHandler.setupOilStock(dieselStockValue.getText(),petrolStockValue.getText())) {
                ((Node)e.getSource()).getScene().getWindow().hide();
            } else {
                System.out.println("Error Updating Oil Stock");
            }*/
        }



    }

    public void setInitialStock(String dieselStock,String petrolStock) {
        dieselStockValue.setText(dieselStock);
        petrolStockValue.setText(petrolStock);
    }
}
