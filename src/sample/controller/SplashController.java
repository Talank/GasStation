package sample.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.model.DatabaseHandler;


import java.io.IOException;

public class SplashController {
    @FXML
    private JFXTextField dieselMeter,petrolMeter;


    @FXML
    public void initialize() {
        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Meter Reading Cannot Be Empty");
        dieselMeter.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Meter Reading Cannot Be Empty");
        petrolMeter.getValidators().add(petrolValidator);

        dieselMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                dieselMeter.setText(oldValue);
            else
                dieselMeter.resetValidation();
        });
        petrolMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                petrolMeter.setText(oldValue);
            else
                petrolMeter.resetValidation();
        });

    }

    @FXML
    public void startButtonClicked(MouseEvent mouseEvent) throws IOException {
        if(dieselMeter.getText().equals(""))
            dieselMeter.validate();
        else if(petrolMeter.getText().equals("")) {
            petrolMeter.validate();
            dieselMeter.resetValidation();
        } else {
            petrolMeter.resetValidation();
            if(DatabaseHandler.addInitialMeterReading(Float.parseFloat(dieselMeter.getText()),Float.parseFloat(petrolMeter.getText()))) {
                Parent homeParent=FXMLLoader.load(getClass().getResource("../view/home.fxml"));
                Scene homeScene=new Scene(homeParent);
                Stage window=(Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                window.setScene(homeScene);
                window.show();
            } else {
                System.out.println("Error Adding Initial Value");
            }

        }
    }
}
