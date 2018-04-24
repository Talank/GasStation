package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.DailyAnalytics;
import sample.library.ToolBar;
import sample.model.DailyAnalyticsModel;
import sample.model.DatabaseHandler;
import sample.model.SystemStatus;


public class DailyAnalyticsController {

    @FXML
    private Label pInitial,pFinal,dInitial,dFinal,cInitial,cFinal,cNet,pNet,dNet;

    @FXML
    private ToolBar toolBar;



    @FXML
    public void initialize() {

        toolBar.setTitle("Daily Analytics");
        toolBar.setBackPath("../view/mainHome.fxml");

        pInitial.setText(String.valueOf(SystemStatus.getiPetrol())+" L");
        pFinal.setText(String.valueOf(SystemStatus.getfPetrol())+" L");
        pNet.setText("Rs."+String.valueOf((SystemStatus.getpRate()*(SystemStatus.getfPetrol()-SystemStatus.getiPetrol())))+"/-");

        dInitial.setText(String.valueOf(SystemStatus.getiDiesel())+" L");
        dFinal.setText(String.valueOf(SystemStatus.getfDiesel())+" L");
        dNet.setText("Rs."+String.valueOf((SystemStatus.getdRate()*(SystemStatus.getfDiesel()-SystemStatus.getiDiesel())))+"/-");

        cInitial.setText("Rs."+String.valueOf(SystemStatus.getiCounter()));

        float price=DatabaseHandler.getTotalInstantPrice();
        if(!(price==0||price==-1)) {
            cFinal.setText("Rs."+String.valueOf(price)+"/-");
            cNet.setText("Rs."+String.valueOf(price)+"/-");
        }
    }


}
