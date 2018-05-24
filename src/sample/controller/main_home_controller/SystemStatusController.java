package sample.controller.main_home_controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;

public class SystemStatusController {

    @FXML
    private Label systemDate;

    @FXML
    private Label transactionDate;

    @FXML
    private Label currentDieselStock;

    @FXML
    private Label currentPetrolStock;

    @FXML
    private Label initialDieselReadings;

    @FXML
    private Label initialPetrolReadings;

    @FXML
    private ToolBar toolBar;

    @FXML
    public void initialize() {

        toolBar.setTitle("System Status");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        systemDate.setText(SystemStatus.getNepaliCurrentDate());
        transactionDate.setText(SystemStatus.getNepaliTransactionDate());
        currentDieselStock.setText(String.valueOf(SystemStatus.getDieselStock())+" Litres");
        currentPetrolStock.setText(String.valueOf(SystemStatus.getPetrolStock())+" Litres");
        initialDieselReadings.setText(String.valueOf(SystemStatus.getiDiesel())+" Litres");
        initialPetrolReadings.setText(String.valueOf(SystemStatus.getiPetrol())+" Litres");
    }


}
