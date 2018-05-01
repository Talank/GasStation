package sample.controller.dialog_controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;

import java.io.IOException;


public class ClosingCalculationController implements BottomBarInterface {

    @FXML
    private Label pComputed,pProvided,dComputed,dProvided,remarks;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Closing Results");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
    }


    public void setData(float data[]) {
        dComputed.setText(data[0]+" L");
        dProvided.setText(data[1]+" L");
        pComputed.setText(data[2]+" L");
        pProvided.setText(data[3]+" L");
    }

    public void setRemarks(String remarks) {
        this.remarks.setText(remarks);
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        (((Node)e.getSource()).getScene().getWindow()).hide();
    }
}
