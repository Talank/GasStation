package np.com.spirrotech.application.profit_loss.dialogs.refill;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.model.RefillModel;
import np.com.spirrotech.model.TankerModel;

import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;


import java.io.IOException;

public class RefillDialogController implements BottomBarInterface {

    @FXML
    private JFXTextField expected, actual, tankerNumber;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    private int mode=0;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Diesel ReFill");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);


        /**To validate Expected Value, Ensures the field is not empty **/
        RequiredFieldValidator expectedDieselValidator=new RequiredFieldValidator();
        expectedDieselValidator.setMessage("Expected Value Cannot Be Empty");
        expected.getValidators().add(expectedDieselValidator);


        /**To validate Actual Value, Ensures the field is not empty **/
        RequiredFieldValidator actualDieselValidator=new RequiredFieldValidator();
        actualDieselValidator.setMessage("Actual Value Cannot Be Empty");
        actual.getValidators().add(actualDieselValidator);


        /**To validate Tanker Number, Ensures the field is not empty **/
        RequiredFieldValidator tankerNumberValidator=new RequiredFieldValidator();
        tankerNumberValidator.setMessage("Tanker Number Cannot Be Empty");
        tankerNumber.getValidators().add(tankerNumberValidator);

        expected.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
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
            if(!newValue.matches("[0-9]{0,10}([\\.][0-9]{0,2})?")) {
                actual.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    actual.validate();
                } else {
                    actual.resetValidation();
                }
            }
        });

        tankerNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[-a-zA-Z0-9]{0,10}")) {
                tankerNumber.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    tankerNumber.validate();
                } else {
                    tankerNumber.resetValidation();
                }
            }
        });

    }


    public void setToolBarTitle(String title) {
        toolBar.setDialogTitle(title);
    }

    public void setMode(int mode) {
        this.mode=mode;
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

        if(tankerNumber.getText().isEmpty()||(tankerNumber.getText().length()<4)) {
            tankerNumber.validate();
            status++;
        }

        if(status==0) {

            float actualValue;
            RefillModel model;
            RefillTask task;

            if(mode==0) {
                /*
                 * This is for diesel,get the diesel actual value and its losses
                 * the 'actualValue' is calculated by getting the actual reading from the device i.e 'actual.getText()' and subtracting from the current diesel stock
                 * it will give the actual value that was inserted
                 * for this to work properly, the stock should be in sync with the actual petrol pump stock
                 */

                actualValue=Float.parseFloat(actual.getText())-SystemStatus.getDieselStock();

                if((Float.parseFloat(expected.getText())-actualValue) >= 0 && actualValue > 0) {

                    //Check if diesel tanker is carrying petrol

                    //First search in memory

                    boolean isPetrol = false;
                    for (TankerModel tankerModel:RefillModel.getTankerModels()) {
                        if(tankerModel.getTankerNumber().substring(0,tankerModel.getTankerNumber().length() - 1).equalsIgnoreCase(tankerNumber.getText()) && tankerModel.getType().equals("Petrol")) {
                            isPetrol = true;
                            break;
                        }
                    }

                    if(!isPetrol) {

                        //Search in database

                        model=new RefillModel(Float.parseFloat(expected.getText()),actualValue,SystemStatus.getNepaliCurrentDate(),
                                new TankerModel(-1f,-1,"Diesel",tankerNumber.getText()));
                        task=new RefillTask(model,mode);
                        task.addTaskInterfaceListener(new TaskInterface() {
                            @Override
                            public void onComplete(Object data) {

                            }

                            @Override
                            public void onError() {
                                bottomBar.setErrorText("Petrol Tanker Provided.");
                            }
                        });
                        TaskManager.getInstance().executeTask(task);
                        ((Node)e.getSource()).getScene().getWindow().hide();
                    } else {
                        bottomBar.setErrorText("Petrol Tanker Provided.");
                    }

                } else {

                    //the actual value cannot be greater than expected value

                    bottomBar.setErrorText("-Ve Loss Encountered.");
                }

            }  else {

                /*
                 * Same logic as above
                 */
                actualValue=Float.parseFloat(actual.getText())-SystemStatus.getPetrolStock();

                if((Float.parseFloat(expected.getText())-actualValue) >= 0 && actualValue > 0) {

                    boolean isDiesel = false;
                    for (TankerModel tankerModel:RefillModel.getTankerModels()) {
                        if(tankerModel.getTankerNumber().substring(0,tankerModel.getTankerNumber().length() - 1).equalsIgnoreCase(tankerNumber.getText()) && tankerModel.getType().equals("Diesel")) {
                            isDiesel = true;
                            break;
                        }
                    }

                    if(!isDiesel) {
                        model=new RefillModel(Float.parseFloat(expected.getText()),actualValue,SystemStatus.getNepaliCurrentDate(),
                                new TankerModel(-1f,-1,"Petrol",tankerNumber.getText()));
                        task=new RefillTask(model,mode);
                        task.addTaskInterfaceListener(new TaskInterface() {
                            @Override
                            public void onComplete(Object data) {

                            }

                            @Override
                            public void onError() {
                                bottomBar.setErrorText("Diesel Tanker Provided.");
                            }
                        });
                        TaskManager.getInstance().executeTask(task);
                        ((Node)e.getSource()).getScene().getWindow().hide();
                    } else {
                        bottomBar.setErrorText("Diesel Tanker Provided.");
                    }
                } else {
                    bottomBar.setErrorText("-Ve Loss Encountered.");
                }
            }


        }
    }

}
