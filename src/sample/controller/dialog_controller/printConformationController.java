package sample.controller.dialog_controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;

import java.io.IOException;

public class printConformationController implements BottomBarInterface {
    private static printConformationController instance;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label remarkLabel;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Print Conformation");
        //bottomBar.setButtonText("PRINT AGAIN");
    }

    public printConformationController() {
        instance=this;
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }
}
