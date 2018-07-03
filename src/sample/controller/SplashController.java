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
import sample.model.databases.DatabaseHandler;
import sample.model.SystemStatus;


import java.io.IOException;

/**
 * @deprecated
 */

public class SplashController {
    /*
    * This Screen Accepts The Initial Values For The Diesel Meter Readings
    * Petrol Meter Readings and Counter Meter Readings
    *
    * */



    @FXML
    private JFXTextField dieselMeter,petrolMeter,counterMeter;


    @FXML
    public void initialize() {

        /*
        * Initialize The Validator so that we can actively Monitor the User Input
        *
        * */

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
            /*
            * Only Accept Decimal Numbers
            * */
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?"))
                dieselMeter.setText(oldValue);
            else
                if(newValue.isEmpty())
                    dieselMeter.validate();//So Error Message
                else
                    dieselMeter.resetValidation();//Hide Error Message
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

        if(dieselMeter.getText().isEmpty()) {//Check if the field is empty
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

      /*  if(status==0) { //All the fields are filled
            float iDiesel=Float.parseFloat(dieselMeter.getText());
            float iPetrol=Float.parseFloat(petrolMeter.getText());
            float iCounter=Float.parseFloat(counterMeter.getText());
            if(DatabaseHandler.addInitialMeterReading(iDiesel,iPetrol,iCounter)) {//Store The initial Meter Readings
                SystemStatus.setiDiesel(iDiesel);//Set the System Status Variables
                SystemStatus.setiPetrol(iPetrol);
                SystemStatus.setiCounter(iCounter);
                Parent homeParent=FXMLLoader.load(getClass().getResource("../view/main_home_views/mainHome.fxml"));//Load Home Screen
                Scene homeScene=new Scene(homeParent);
                Stage window=(Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                window.setScene(homeScene);
                window.show();
            } else {
                SystemLogging.getLogger().severe("Error Storing Initial Values,System might misbehave!");
            }
        }*/

    }
}
