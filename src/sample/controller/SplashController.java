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
import sample.model.SystemStatus;

import java.util.logging.FileHandler;
import java.util.logging.Logger;


import java.io.IOException;

public class SplashController {
    @FXML
    private JFXTextField dieselMeter,petrolMeter,counterMeter;


    @FXML
    public void initialize() {

        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Meter Reading Cannot Be Empty");
        dieselMeter.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Meter Reading Cannot Be Empty");
        petrolMeter.getValidators().add(petrolValidator);

        RequiredFieldValidator counterValidator=new RequiredFieldValidator();
        counterValidator.setAccessibleText("Counter Reading Cannot Be Empty");
        counterMeter.getValidators().add(counterValidator);

        dieselMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                dieselMeter.setText(oldValue);
            else
                if(newValue.isEmpty())
                    dieselMeter.validate();
                else
                    dieselMeter.resetValidation();
        });

        petrolMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                petrolMeter.setText(oldValue);
            else
                if(newValue.isEmpty())
                    petrolMeter.validate();
                else
                    petrolMeter.resetValidation();
        });

        counterMeter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                counterMeter.setText(oldValue);
            else
            if(newValue.isEmpty())
                counterMeter.validate();
            else
                counterMeter.resetValidation();
        });

    }

    @FXML
    public void startButtonClicked(MouseEvent mouseEvent) throws IOException {
        int status=0;

        if(dieselMeter.getText().isEmpty()) {
            dieselMeter.validate();
            status++;
        }

        if(petrolMeter.getText().isEmpty()) {
            petrolMeter.validate();
            status++;
        }

        if(counterMeter.getText().isEmpty()) {
            counterMeter.validate();
            status++;
        }

        if(status==0) {
            float iDiesel=Float.parseFloat(dieselMeter.getText());
            float iPetrol=Float.parseFloat(petrolMeter.getText());
            float iCounter=Float.parseFloat(counterMeter.getText());
            if(DatabaseHandler.addInitialMeterReading(iDiesel,iPetrol,iCounter)) {
                SystemStatus.setiDiesel(iDiesel);
                SystemStatus.setiPetrol(iPetrol);
                SystemStatus.setiCounter(iCounter);
                Parent homeParent=FXMLLoader.load(getClass().getResource("../view/mainHome.fxml"));
                Scene homeScene=new Scene(homeParent);
                Stage window=(Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                window.setScene(homeScene);
                window.show();
            } else {
                SystemLogging.getLogger().severe("Error Storing Initial Values,System might misbehave!");
            }
        }

    }
}
