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
import sample.model.SystemStatus;
import sun.misc.FpUtils;

import java.io.IOException;
import java.sql.Statement;

public class FinalReadingsController {

    @FXML
    private JFXTextField fDiesel,fPetrol;

    private Stage stage;


    @FXML
    public void initialize() {

        RequiredFieldValidator dieselValidator=new RequiredFieldValidator();
        dieselValidator.setMessage("Diesel Reading Cannot Be Empty");
        fDiesel.getValidators().add(dieselValidator);

        RequiredFieldValidator petrolValidator=new RequiredFieldValidator();
        petrolValidator.setMessage("Petrol Reading Cannot Be Empty");
        fPetrol.getValidators().add(petrolValidator);


        fDiesel.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                fDiesel.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    fDiesel.validate();
                } else {
                    fDiesel.resetValidation();
                }
            }
        });

        fPetrol.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                fPetrol.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    fPetrol.validate();
                } else {
                    fPetrol.resetValidation();
                }
            }
        });

    }

    @FXML
    public void cancel(MouseEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void loadDailyAnalytics(MouseEvent e) throws IOException {
        int status=0;
        if(fPetrol.getText().isEmpty()) {
            fPetrol.validate();
            status++;
        }

        if(fDiesel.getText().isEmpty()) {
            fDiesel.validate();
            status++;
        }
        if(status==0) {
            SystemStatus.setfDiesel(Float.parseFloat(fDiesel.getText()));
            SystemStatus.setfPetrol(Float.parseFloat(fPetrol.getText()));
            ((Node)e.getSource()).getScene().getWindow().hide();
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/dailyAnalytics.fxml"));
            Parent root=loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    public void setStage(Stage stage) {
        this.stage=stage;
    }



}
