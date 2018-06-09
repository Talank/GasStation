package sample.controller.dialog_controller;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.io.IOException;

public class ErrorDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label errorLabel;

    private Tasks task=null;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Error");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {

        if(task!=null) {
            TaskManager.executeTask(task);
        }
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public void setErrorMessage(String msg) {
        errorLabel.setText(msg);
    }

    public void setRetryTask(Tasks task) {
        this.task=task;
        bottomBar.setButtonText("Retry");
    }

    public void setTitle(String title) {
        toolBar.setDialogTitle(title);
    }
}
