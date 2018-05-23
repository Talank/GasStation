package sample.controller.dialog_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.ProfitLossController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;
import sample.tasks.database_tasks.GetTankRemarkTask;

import java.io.IOException;
import java.sql.*;

public class TankRemarkController implements BottomBarInterface,TaskInterface<Float> {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    private static TankRemarkController instance;

    @FXML
    private Label avgLossLabel;

    @FXML
    private Label remarkLabel;

    private int vehicleNumber;

    public TankRemarkController() {
        instance=this;
    }

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Tanker Check");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
        remarkLabel.setText("Yo");



    }

    @Override
    public void onComplete(Float data) {
        //We are touching live scene ,so run it on UI thread

        Platform.runLater(()->{
            float avg=data;

            if(remarkLabel==null) {
                System.out.println("Remark label was null");
            } else {
                if (avg > 0 && avg <= 20) {
                    // this.remarkLabel.setText("The tanker is good");
                    remarkLabel.setText("The tanker is good");
                } else if (avg >= 21 && avg <= 50) {
                    //this.remarkLabel.setText("Normal tanker");
                    remarkLabel.setText("Normal tanker");
                } else if (avg > 50) {
                    //this.remarkLabel.setText("Not good tanker");
                    remarkLabel.setText("Not good tanker");

                }
            }

        });
    }

    @Override
    public void onError() {

    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public static TankRemarkController getInstance() {
        return instance;
    }

    public void setData(int vehicleNumber) {
        this.vehicleNumber=vehicleNumber;
        GetTankRemarkTask task=new GetTankRemarkTask(vehicleNumber);
        task.addTaskInterfaceListener(this);
        TaskManager.executeTask(task);
    }
}
