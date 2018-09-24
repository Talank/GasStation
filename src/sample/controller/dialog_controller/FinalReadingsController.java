package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import sample.controller.AppHomeController;



import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;

import sample.model.SystemStatus;

import java.io.IOException;


public class FinalReadingsController implements BottomBarInterface {

    @FXML
    private JFXTextField fDiesel,fPetrol;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Final Meter Readings");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

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

    @Override
    public void performAction(MouseEvent e) throws IOException {
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

            //Check if the finalMeterReading - initialMeterReading is < 0, if it is, then there is error

            if((Float.parseFloat(fDiesel.getText()) - SystemStatus.getiDiesel()) < 0 || (Float.parseFloat(fPetrol.getText()) - SystemStatus.getiPetrol()) < 0) {
                bottomBar.setErrorText("Negative Meter Readings Encountered");
            } else {
                SystemStatus.setfDiesel(Float.parseFloat(fDiesel.getText()));
                SystemStatus.setfPetrol(Float.parseFloat(fPetrol.getText()));

                BorderPane borderPane=FXMLLoader.load(getClass().getClassLoader().getResource("sample/view/main_home_views/dailyAnalytics.fxml"));
                AppHomeController.getInstance().loadStages(borderPane);

                ((Node)e.getSource()).getScene().getWindow().hide();
            }

        }
    }




}
