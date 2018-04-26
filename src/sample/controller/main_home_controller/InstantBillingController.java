package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import javafx.scene.input.MouseEvent;

import javafx.beans.value.ObservableValue;
import sample.controller.SystemLogging;
import sample.model.databases.DatabaseHandler;
import sample.model.InstantBill;
import sample.model.SystemStatus;
import sample.library.toolbar.ToolBar;

import java.io.IOException;
import java.text.DecimalFormat;



public class InstantBillingController {

    private static boolean calcNowV =false,calcNowP=false,calcOldV=false,calcOldP=false;
    private DecimalFormat decimalFormat;
    private float rate;

    @FXML
    private JFXButton back,print;

    @FXML
    private JFXRadioButton diesel,petrol;

    @FXML
    public ToolBar toolBar;

    @FXML
    private JFXTextField customerName,volume,price,vehicleNumber;

    @FXML
    private ToggleGroup selection;

    @FXML
    public void initialize() {

        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.setTitle("Instant Billing");


        diesel.setSelected(true);
        rate=SystemStatus.getdRate();

        selection.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov,Toggle old_toggle, Toggle new_toggle)->{
            if(((JFXRadioButton)new_toggle).getText().equals("Diesel")) {
                rate=SystemStatus.getdRate();
            } else {
                rate=SystemStatus.getpRate();
            }

            if(calcOldV) {
                price.setText(decimalFormat.format(Float.parseFloat(volume.getText())*rate));
            } else if(calcOldP) {
                volume.setText(decimalFormat.format(Float.parseFloat(price.getText())/rate));
            }
        });

        decimalFormat=new DecimalFormat("#.####");

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
                if(!newValue.matches("[a-zA-Z ]*")) {//Recursion May Exists,Just a Work Around.
                    customerName.setText(oldValue);
                } else {
                    if(newValue.isEmpty()) {
                        customerName.validate();
                    } else {
                        customerName.resetValidation();
                    }
                }
        });

        vehicleNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.isEmpty())
                vehicleNumber.validate();
            else
                vehicleNumber.resetValidation();
        });

        volume.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?"))
                volume.setText(oldValue);
            else
                if(newValue.isEmpty())
                    volume.validate();
                else {
                    volume.resetValidation();
                    if(calcNowV) {
                        price.setText(decimalFormat.format(Float.parseFloat(volume.getText())*rate));
                    }
                }

        });

        volume.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldV=true;
            calcNowP=false;
            calcNowV=newPropertyValue;
        });


        price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,4})?"))
                price.setText(oldValue);
            else
                if(newValue.isEmpty())
                    price.validate();
                else {
                    price.resetValidation();
                    if(calcNowP) {
                        volume.setText(decimalFormat.format(Float.parseFloat(price.getText())/rate));
                    }
                }

        });

        price.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)->{
            calcOldP=true;
            calcOldV=false;
            calcNowP=newPropertyValue;
        });

    }

    @FXML
    public void printBill(MouseEvent e)throws IOException {
        int status=0;
        if(customerName.getText().trim().isEmpty()) {
            customerName.validate();
            status++;
        }

        if(price.getText().trim().isEmpty()) {
            price.validate();
            status++;
        }

        if(volume.getText().trim().isEmpty()) {
            volume.validate();
            status++;
        }

        if(vehicleNumber.getText().trim().isEmpty()) {
            vehicleNumber.validate();
            status++;
        }

        if(status==0) {
            InstantBill instantBill=new InstantBill(customerName.getText().trim(),price.getText(),volume.getText(),vehicleNumber.getText().trim(),diesel.isSelected());
            instantBill.printBill();
        }

    }

    @FXML
    public void insertTransaction(MouseEvent e) {
        int status=0;

        if(price.getText().trim().isEmpty()) {
            price.validate();
            status++;
        }

        if(volume.getText().trim().isEmpty()) {
            volume.validate();
            status++;
        }

        if(status==0) {
            if(DatabaseHandler.storeInstantTransaction(Float.parseFloat(price.getText()),Float.parseFloat(volume.getText()))) {
               SystemLogging.getLogger().fine("Instant Transaction Stored SuccessFully.");
            } else {
                SystemLogging.getLogger().severe("Error Storing Instant Transaction!");
            }
        }
    }
}
