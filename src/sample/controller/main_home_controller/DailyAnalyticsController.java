package sample.controller.main_home_controller;


import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.model.transaction_model.DailyTotalAnalyticModel;

import sample.tasks.TaskManager;
import sample.OutputTasks.GetDailyTotalAnalyticsModelTask;
import sample.tasks.database_tasks.TodayClosingTask;


/*
* This class provides a details of total transaction of the day
*
* */

public class DailyAnalyticsController {

    @FXML
    private Label pInitial,pFinal,dInitial,dFinal,cInitial,cFinal,pNet,dNet,netOil,netExtra;

    @FXML
    private ToolBar toolBar;

    private Precision precision;
    private PriceFormat priceFormat;


    @FXML
    public void initialize() {

        toolBar.setTitle("Today Analytics");//title
        toolBar.setBackPath("sample/view/main_home_views/dailyTotalAnalytics.fxml");

        precision = new Precision();
        priceFormat = new PriceFormat();

    }

    public void setModel(DailyTotalAnalyticModel model) {

        pInitial.setText(priceFormat.format(precision.format(Float.parseFloat(model.getIPetrolVol())))+" L");

        pFinal.setText(priceFormat.format(precision.format(Float.parseFloat(model.getCPetrolVol())))+" L");
        pNet.setText("Rs. "+priceFormat.format(precision.format((SystemStatus.getfDiesel()-SystemStatus.getiPetrol())*SystemStatus.getpRate()))+"/-");

        dInitial.setText(priceFormat.format(precision.format(Float.parseFloat(model.getIDieselVol())))+" L");
        dFinal.setText(priceFormat.format(precision.format(Float.parseFloat(model.getCDieselVol())))+" L");
        dNet.setText("Rs. "+priceFormat.format(precision.format((SystemStatus.getfPetrol()-SystemStatus.getiDiesel())*SystemStatus.getdRate()))+"/-");

        cInitial.setText("Rs./-");
        cFinal.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getCExtra())))+"/-");

        netOil.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getOilIncome())))+"/-");
        netExtra.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getCExtra())))+"/-");

        TodayClosingTask tasks=new TodayClosingTask(model);
        TaskManager.getInstance().executeTask(tasks);

    }


}
