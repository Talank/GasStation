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
import sample.model.databases.DatabaseHandler;
import sample.model.RefillModel;

import java.io.IOException;
import java.sql.Date;

public class RefillDialogController implements BottomBarInterface {

    @FXML
    private JFXTextField expected,actual;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("ReFill");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator expectedValidator=new RequiredFieldValidator();
        expectedValidator.setMessage("Expected Value Cannot Be Empty");
        expected.getValidators().add(expectedValidator);

        RequiredFieldValidator actualValidator=new RequiredFieldValidator();
        actualValidator.setMessage("Actual Value Cannot Be Empty");
        actual.getValidators().add(actualValidator);

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
            float loss=Float.parseFloat(expected.getText())-Float.parseFloat(actual.getText());
            RefillModel model=new RefillModel(expected.getText(),String.valueOf(loss),actual.getText(),new Date(new java.util.Date().getTime()));
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
