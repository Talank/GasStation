package sample.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.model.DatabaseHandler;
import sample.model.RefillModel;

import java.sql.Date;

public class RefillDialogController {

    @FXML
    private JFXTextField expected,actual;

    @FXML
    public void initialize() {

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

    @FXML
    public void cancel(MouseEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void saveReFill(MouseEvent e) {
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
