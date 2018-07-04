package sample.controller.dialog_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.RefillModel;
import sample.model.TankerModel;
import sample.tasks.TaskInterface;

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

            if(tankerModel.getTankerNumber().contains("G")) {
                remarkLabel.setText("The tanker is good!");
            } else if (tankerModel.getTankerNumber().contains("B")) {
                remarkLabel.setText("Normal tanker");
            } else if (tankerModel.getTankerNumber().contains("R")) {
                remarkLabel.setText("Not good tanker");
            } else {
                remarkLabel.setText("Tanker Status UnKnown");
            }
            avgLossLabel.setText("Total Loss: "+ tankerModel.getAvg()*tankerModel.getCount()+  "  Count: "+tankerModel.getCount());
            typeLabel.setText("Type: "+tankerModel.getType());

        } else {
            bottomBar.setErrorText("tanker not found");
            remarkLabel.setText("Sorry! The tanker is not found");
            typeLabel.setText("");
            avgLossLabel.setText("");
        }
    }
}
