package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.SettingsController;
import sample.library.SettingsConstants;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SettingsTask;
import sample.tasks.database_tasks.SetupCounterStockTask;

import java.io.IOException;

public class CounterStockDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private JFXTextField counterStock;


    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Setup Counter Stock");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator counterStockValidator=new RequiredFieldValidator();
        counterStockValidator.setMessage("Counter Stock Cannot Be Empty");
        counterStock.getValidators().add(counterStockValidator);


        counterStock.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                counterStock.setText(oldValue);
            else
            if(newValue.isEmpty())
                counterStock.validate();
            else
                counterStock.resetValidation();
        });
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        if(counterStock.getText().isEmpty()) {
            counterStock.validate();
        } else {

            SettingsTask task = new SettingsTask(new String[] {counterStock.getText()}, new int[] {SettingsConstants.COUNTER_STOCK}, SettingsConstants.CHANGE_COUNTER_STOCK);
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {
                    Platform.runLater(()->{

                        String[][] model=SettingsController.getInstance().getModel();
                        model[5][0]=counterStock.getText();
                        model[5][1]=SystemStatus.getNepaliCurrentDate();

                        SettingsController.getInstance().loadSettings(model);
                        ((Node)e.getSource()).getScene().getWindow().hide();
                    });
                }

                @Override
                public void onError() {

                }
            });
            TaskManager.getInstance().executeTask(task);

           /*
            SetupCounterStockTask task=new SetupCounterStockTask(counterStock.getText());
            TaskManager.getInstance().executeTask(task);
            ((Node)e.getSource()).getScene().getWindow().hide();

            String[][] model=SettingsController.getInstance().getModel();
            model[5][0]=counterStock.getText();
            model[5][1]=SystemStatus.getNepaliCurrentDate();

            SettingsController.getInstance().loadSettings(model);*/

        }
    }

    public void setInitialStock(String stock) {
        counterStock.setText(stock);
    }
}
