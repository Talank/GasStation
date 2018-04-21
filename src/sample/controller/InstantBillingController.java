package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.beans.value.ObservableValue;

import java.io.IOException;

public class InstantBillingController {

    @FXML
    private JFXButton back,print;

    @FXML
    private JFXRadioButton diesel,petrol;

    @FXML
    private JFXTextField customerName,volume,price,vehicleNumber;

    @FXML
    private ToggleGroup selection;

    @FXML
    public void initialize() {
        RequiredFieldValidator customerNameValidator=new RequiredFieldValidator();
        customerNameValidator.setMessage("Customer Name Cannot Be Empty");
        customerName.getValidators().add(customerNameValidator);

        RequiredFieldValidator volumeValidator=new RequiredFieldValidator();
        volumeValidator.setMessage("Volume Cannot Be Empty");
        volume.getValidators().add(volumeValidator);

        RequiredFieldValidator priceValidator=new RequiredFieldValidator();
        priceValidator.setMessage("Price Cannot Be Empty");
        price.getValidators().add(priceValidator);

        RequiredFieldValidator vehicleNumberValidator=new RequiredFieldValidator();
        vehicleNumberValidator.setMessage("Vehicle Number Cannot Be Empty");
        vehicleNumber.getValidators().add(vehicleNumberValidator);

        customerName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z]{3,15}[ ]{0,1}[a-zA-Z]{0,15}[ ]{0,1}[a-zA-Z]{0,15}"))
                if(oldValue!=null)
                    customerName.setText(oldValue);
            else
                if(newValue.isEmpty())
                    customerName.validate();
                else
                    customerName.resetValidation();
        });

        vehicleNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty())
                vehicleNumber.validate();
            else
                vehicleNumber.resetValidation();
        });

        volume.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                volume.setText(oldValue);
            else
                if(newValue.isEmpty())
                    volume.validate();
                else
                    volume.resetValidation();
        });

        price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                price.setText(oldValue);
            else
                if(newValue.isEmpty())
                    price.validate();
                else
                    price.resetValidation();
        });


    }



    @FXML
    public void backToHome(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../view/mainHome.fxml"));
        Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void printBill(MouseEvent e)throws IOException {
        if(customerName.getText().trim().isEmpty())
            customerName.validate();
        if(price.getText().trim().isEmpty())
            price.validate();
        if(volume.getText().trim().isEmpty())
            volume.validate();
        if(vehicleNumber.getText().trim().isEmpty())
            vehicleNumber.validate();

    }
}
