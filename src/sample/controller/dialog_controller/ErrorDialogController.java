package sample.controller.dialog_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.GetErrorMsgTask;

import java.io.IOException;

public class ErrorDialogController implements BottomBarInterface,TaskInterface<String> {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label errorLabel;

    private static ErrorDialogController instance;
    private String msg;

    public ErrorDialogController(){
        instance = this;
    }

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Error");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public void setErrorMessage(String msg) {
        this.msg=msg;
        GetErrorMsgTask errorMsgTask=new GetErrorMsgTask(msg);
        errorMsgTask.addTaskInterfaceListener(this);
        TaskManager.executeTask(errorMsgTask);

    }

    public void setTitle(String title) {
        toolBar.setDialogTitle(title);
    }

    public static ErrorDialogController getInstance(){
        return instance;
    }

    @Override
    public void onComplete(String data) {
        Platform.runLater(()->{
        if(data!=null){
                errorLabel.setText(data);

        }
        });
    }

    @Override
    public void onError() {

    }
}
