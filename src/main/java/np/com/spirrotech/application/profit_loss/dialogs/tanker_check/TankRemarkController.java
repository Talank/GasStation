package np.com.spirrotech.application.profit_loss.dialogs.tanker_check;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.dao.RefillModel;
import np.com.spirrotech.dao.TankerModel;

import np.com.spirrotech.model.GetTankRemarkTask;
import np.com.spirrotech.utils.tasks.TaskManager;

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

    public void setData(String vehicleNumber) {

        /*
        * Check if the tanker is present in the memory, if present load from memory otherwise read from the database
        * */

        boolean isFound = false;
        TankerModel tankerModel = null;
        for (TankerModel model: RefillModel.getTankerModels()) {
            if(model.getTankerNumber().substring(0,(model.getTankerNumber().length() - 1)).equalsIgnoreCase(vehicleNumber)) {
                tankerModel = model;
                isFound = true;
                break;
            }
        }

        if (isFound) {

            //Tanker present in memory

            if(tankerModel.getTankerNumber().charAt(tankerModel.getTankerNumber().length() - 1) == 'G') {
                remarkLabel.setText("The tanker is good!");
            } else if (tankerModel.getTankerNumber().charAt(tankerModel.getTankerNumber().length() - 1) == 'B') {
                remarkLabel.setText("The tanker is good!");
            } else if (tankerModel.getTankerNumber().charAt(tankerModel.getTankerNumber().length() - 1) == 'R') {
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
                    remarkLabel.setText("Sorry! The tanker is not found");
                    typeLabel.setText("");
                    avgLossLabel.setText("");
                }
            });

            tasks.setOnFailed((WorkerStateEvent e)->{
                remarkLabel.setText("Sorry! The tanker is not found");
                typeLabel.setText("");
                avgLossLabel.setText("");
            });

            TaskManager.getInstance().executeInstantTask(tasks);

        }
    }
}
