package sample.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.model.RateModel;
import javafx.scene.Node;

import java.io.IOException;

public class ChangeRateController {

    @FXML
    private JFXTextField rDiesel,rPetrol;


    @FXML
    public void initialize() {
        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Rate Cannot Be Empty");
        rDiesel.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Rate Cannot Be Empty");
        rPetrol.getValidators().add(petrolValidator);

        rDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?"))
                rDiesel.setText(oldValue);
            else
                if(newValue.isEmpty())
                    rDiesel.validate();
                else
                    rDiesel.resetValidation();
        });

        rPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,3}([\\.][0-9]{0,2})?"))
                rPetrol.setText(oldValue);
            else
                if(newValue.isEmpty())
                    rPetrol.validate();
                else
                    rPetrol.resetValidation();
        });

    }

    @FXML
    public void setRate(MouseEvent e) throws IOException {
        if(rDiesel.getText().isEmpty())
            rDiesel.validate();
        if(rPetrol.getText().isEmpty())
            rPetrol.validate();
        if(!rPetrol.getText().isEmpty()&&!rDiesel.getText().isEmpty()) {
            RateModel rateModel=new RateModel(Float.parseFloat(rDiesel.getText()),Float.parseFloat(rPetrol.getText()));
            if(rateModel.storeRate())
                SystemLogging.getLogger().fine("Diesel And Petrol Rate Successfully Changed");
            else
                SystemLogging.getLogger().warning("Failed to Store Rate,Using Current Rate Temporarily!");

            MainHomeController.getInstance().setRates(rDiesel.getText(),rPetrol.getText());

            Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
            stage.close();
        }
    }

}
