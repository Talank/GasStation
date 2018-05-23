package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.SystemLogging;
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

   // public JFXTextField tankerNumber;
    @FXML
    private JFXTextField expected, actual, tankerNumber;

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


        /**To validate Expected Value, Ensures the field is not empty **/
        RequiredFieldValidator expectedDieselValidator=new RequiredFieldValidator();
        expectedDieselValidator.setMessage("Expected Value Cannot Be Empty");
        expected.getValidators().add(expectedDieselValidator);


        /**To validate Actual Value, Ensures the field is not empty **/
        RequiredFieldValidator actualDieselValidator=new RequiredFieldValidator();
        actualDieselValidator.setMessage("Actual Value Cannot Be Empty");
        actual.getValidators().add(actualDieselValidator);


        /**To validate Tanker Number, Ensures the field is not empty **/
        RequiredFieldValidator tankerNumberValidator=new RequiredFieldValidator();
        tankerNumberValidator.setMessage("Tanker Number Cannot Be Empty");
        tankerNumber.getValidators().add(tankerNumberValidator);

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

        tankerNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                tankerNumber.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    tankerNumber.validate();
                } else {
                    tankerNumber.resetValidation();
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

        if(tankerNumber.getText().isEmpty()) {
            tankerNumber.validate();
            status++;
        }

        if(status==0) {

            float actualValue,loss;
            RefillModel model;

            if(mode==0) {
                /**
                 * This is for diesel,get the diesel actual value and its losses
                 */

                actualValue=Float.parseFloat(actual.getText())-SystemStatus.getDieselStock();
                loss=Float.parseFloat(expected.getText())-actualValue;

                model=new RefillModel(Float.parseFloat(expected.getText()),actualValue,loss,SystemStatus.getCurrentDate(),tankerNumber.getText());
                ProfitLossController.getInstance().addDieselModel(model);
            } else {

                /**
                 * This is for petrol,get the petrol actual value and its losses
                 */
                actualValue=Float.parseFloat(actual.getText())-SystemStatus.getPetrolStock();
                loss=Float.parseFloat(expected.getText())-actualValue;

                model=new RefillModel(Float.parseFloat(expected.getText()),actualValue,loss,SystemStatus.getCurrentDate(),tankerNumber.getText());
                ProfitLossController.getInstance().addPetrolModel(model);
            }

            RefillTask task=new RefillTask(model,mode);
            TaskManager.executeTask(task);
            ((Node)e.getSource()).getScene().getWindow().hide();

        }
    }

}
