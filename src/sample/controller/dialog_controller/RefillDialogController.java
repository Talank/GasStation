package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.ProfitLossController;
import sample.controller.SystemLogging;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.RefillModel;

import java.io.IOException;
import java.sql.Date;

public class RefillDialogController implements BottomBarInterface {

    @FXML
    private JFXTextField expectedDiesel,actualDiesel,expectedPetrol,actualPetrol;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("ReFill");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator expectedDieselValidator=new RequiredFieldValidator();
        expectedDieselValidator.setMessage("Expected Value Cannot Be Empty");
        expectedDiesel.getValidators().add(expectedDieselValidator);

        RequiredFieldValidator actualDieselValidator=new RequiredFieldValidator();
        actualDieselValidator.setMessage("Actual Value Cannot Be Empty");
        actualDiesel.getValidators().add(actualDieselValidator);

        expectedDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                expectedDiesel.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    expectedDiesel.validate();
                } else {
                    expectedDiesel.resetValidation();
                }
            }
        });

        actualDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                actualDiesel.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    actualDiesel.validate();
                } else {
                    actualDiesel.resetValidation();
                }
            }
        });

        RequiredFieldValidator expectedPetrolValidator=new RequiredFieldValidator();
        expectedPetrolValidator.setMessage("Expected Value Cannot Be Empty");
        expectedPetrol.getValidators().add(expectedPetrolValidator);

        RequiredFieldValidator actualPetrolValidator=new RequiredFieldValidator();
        actualPetrolValidator.setMessage("Actual Value Cannot Be Empty");
        actualPetrol.getValidators().add(actualPetrolValidator);

        expectedPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                expectedPetrol.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    expectedPetrol.validate();
                } else {
                    expectedPetrol.resetValidation();
                }
            }
        });

        actualPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?")) {
                actualPetrol.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    actualPetrol.validate();
                } else {
                    actualPetrol.resetValidation();
                }
            }
        });

    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        int status=0;

        if(expectedDiesel.getText().isEmpty()) {
            expectedDiesel.validate();
            status++;
        }

        if(actualDiesel.getText().isEmpty()) {
            actualDiesel.validate();
            status++;
        }

        if(expectedPetrol.getText().isEmpty()) {
            expectedPetrol.validate();
            status++;
        }

        if(actualPetrol.getText().isEmpty()) {
            actualPetrol.validate();
            status++;
        }

        if(status==0) {
            float dieselLoss=Float.parseFloat(expectedDiesel.getText())-(SystemStatus.getDieselStock()-Float.parseFloat(actualDiesel.getText()));
            float petrolLoss=Float.parseFloat(expectedPetrol.getText())-(SystemStatus.getPetrolStock()-Float.parseFloat(actualPetrol.getText()));
            RefillModel model=new RefillModel(Float.parseFloat(expectedDiesel.getText()),Float.parseFloat(actualDiesel.getText()),Float.parseFloat(expectedPetrol.getText()),Float.parseFloat(actualPetrol.getText()),dieselLoss,petrolLoss,new Date((new java.util.Date().getTime())));
            ProfitLossController.getInstance().addModel(model);
            if(DatabaseHandler.storeRefill(model)) {
                ((Node)e.getSource()).getScene().getWindow().hide();
                SystemLogging.getLogger().fine("SuccessFully Refilled Data.");
            } else {
                SystemLogging.getLogger().severe("Error Storing Refill Data");
            }
        }
    }

}
