package sample.controller.dialog_controller;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.RefillModel;
import sample.model.TankerModel;

import sample.tasks.TaskManager;
import sample.OutputTasks.GetTankRemarkTask;

import java.io.IOException;

public class TankRemarkController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    private static TankRemarkController instance;

    @FXML
    private Label avgLossLabel;

    @FXML
    private Label remarkLabel;

    @FXML
    private Label typeLabel;

    public TankRemarkController() {
        instance=this;
    }

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Tanker Check");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public static TankRemarkController getInstance() {
        return instance;
    }

    public void setData(int vehicleNumber) {

        /*
        * Check if the tanker is present in the memory, if present load from memory otherwise read from the database
        * */

        boolean isFound = false;
        TankerModel tankerModel = null;
        for (TankerModel model: RefillModel.getTankerModels()) {
            if(model.getTankerNumber().substring(0,4).equals(String.valueOf(vehicleNumber))) {
                tankerModel = model;
                isFound = true;
                break;
            }
        }

        if (isFound) {

            //Tanker present in memory

            if(tankerModel.getTankerNumber().contains("G")) {
                remarkLabel.setText("The tanker is good!");
            } else if (tankerModel.getTankerNumber().contains("B")) {
                remarkLabel.setText("The tanker is good!");
            } else if (tankerModel.getTankerNumber().contains("R")) {
                remarkLabel.setText("Not good tanker");
            } else {
                remarkLabel.setText("Tanker Status UnKnown");
            }
            avgLossLabel.setText("Total Loss: "+ tankerModel.getAvg()*tankerModel.getCount()+  "  Count: "+tankerModel.getCount());
            typeLabel.setText("Type: "+tankerModel.getType());

        } else {

            //Read tanker from database

            GetTankRemarkTask tasks = new GetTankRemarkTask(vehicleNumber);
            tasks.setOnSucceeded((WorkerStateEvent e)->{
                TankerModel model = tasks.getValue();

                if(model!=null) {
                    float avgValue = model.getAvg();

                    if (avgValue >= 0 && avgValue <= 20) {
                        remarkLabel.setText("The tanker is good!");
                    } else if (avgValue >= 21 && avgValue <= 50) {
                        remarkLabel.setText("The tanker is good!");
                    } else if (avgValue > 50) {
                        remarkLabel.setText("Not good tanker");
                    } else {
                        remarkLabel.setText("Tanker Status UnKnown");
                    }
                    avgLossLabel.setText("Total Loss: " + avgValue * model.getCount() + "  Count: " + model.getCount());
                    typeLabel.setText("Type: " + model.getType());
                }
                else{
                    bottomBar.setErrorText("tanker not found");
                    remarkLabel.setText("Sorry! The tanker is not found");
                    typeLabel.setText("");
                    avgLossLabel.setText("");
                }
            });

            tasks.setOnFailed((WorkerStateEvent e)->{
                bottomBar.setErrorText("tanker not found");
                remarkLabel.setText("Sorry! The tanker is not found");
                typeLabel.setText("");
                avgLossLabel.setText("");
            });

            TaskManager.getInstance().executeInstantTask(tasks);

        }
    }
}
