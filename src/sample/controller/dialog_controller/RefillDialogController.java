package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.ProfitLossController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.model.RefillModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.RefillTask;
import sample.tasks.database_tasks.SetupOilStockTask;

import java.io.IOException;
import java.sql.Date;

public class RefillDialogController implements BottomBarInterface {

    @FXML
    private JFXTextField expected, actual;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    private int mode=0;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Diesel ReFill");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator expectedDieselValidator=new RequiredFieldValidator();
        expectedDieselValidator.setMessage("Expected Value Cannot Be Empty");
        expected.getValidators().add(expectedDieselValidator);

        RequiredFieldValidator actualDieselValidator=new RequiredFieldValidator();
        actualDieselValidator.setMessage("Actual Value Cannot Be Empty");
        actual.getValidators().add(actualDieselValidator);

        expected.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                expected.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    expected.validate();
                } else {
                    expected.resetValidation();
                }
            }
        });

        actual.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                actual.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    actual.validate();
                } else {
                    actual.resetValidation();
                }
            }
        });


    }


    public void setToolBarTitle(String title) {
        toolBar.setDialogTitle(title);
    }

    public void setMode(int mode) {
        this.mode=mode;
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        int status=0;

        if(expected.getText().isEmpty()) {
            expected.validate();
            status++;
        }

        if(actual.getText().isEmpty()) {
            actual.validate();
            status++;
        }


        if(status==0) {
            float actualValue=Float.parseFloat(actual.getText())-SystemStatus.getDieselStock();
            float loss=Float.parseFloat(expected.getText())-actualValue;

            RefillModel model=new RefillModel(Float.parseFloat(expected.getText()),actualValue,loss,DatabaseHandler.getCurrentDate());

            if(mode==0) {
                ProfitLossController.getInstance().addDieselModel(model);

                SystemStatus.setDieselStock(Float.parseFloat(actual.getText()));

                SetupOilStockTask task=new SetupOilStockTask(actual.getText(),String.valueOf(SystemStatus.getPetrolStock()));
                TaskManager.executeTask(task);

            } else {
                ProfitLossController.getInstance().addPetrolModel(model);

                SystemStatus.setPetrolStock(Float.parseFloat(actual.getText()));

                SetupOilStockTask task=new SetupOilStockTask(String.valueOf(SystemStatus.getDieselStock()),actual.getText());
                TaskManager.executeTask(task);
            }
            RefillTask task=new RefillTask(model,mode);
            TaskManager.executeTask(task);
            ((Node)e.getSource()).getScene().getWindow().hide();

            /*if(DatabaseHandler.storeRefill(model)) {
                ((Node)e.getSource()).getScene().getWindow().hide();
                SystemLogging.getLogger().fine("SuccessFully Refilled Data.");
            } else {
                SystemLogging.getLogger().severe("Error Storing Refill Data");
            }*/
        }
    }

}
