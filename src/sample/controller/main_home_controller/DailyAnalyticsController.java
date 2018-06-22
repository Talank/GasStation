package sample.controller.main_home_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.GetDailyTotalAnalyticsModelTask;
import sample.tasks.database_tasks.SetupMeterReadingsTask;
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

    private DailyTotalAnalyticModel model;

    public static DailyAnalyticsController instance;
    public DailyAnalyticsController() {
        instance=this;
    }


    @FXML
    public void initialize() {

        toolBar.setTitle("Today Analytics");//title
        toolBar.setBackPath("../../view/main_home_views/dailyTotalAnalytics.fxml");

        GetDailyTotalAnalyticsModelTask task=new GetDailyTotalAnalyticsModelTask();
        task.addTaskInterfaceListener(new TaskInterface() {
            @Override
            public void onComplete(Object data) {
                Platform.runLater(()->{
                    setModel((DailyTotalAnalyticModel)data);
                    System.out.println("Model is set.");
                });
            }

            @Override
            public void onError() {
                System.out.println("Error");
            }
        });

        TaskManager.executeTask(task);

    }

    public void setModel(DailyTotalAnalyticModel model) {
        this.model=model;
        pInitial.setText(new PriceFormat(model.getIPetrolVol()).getResult()+" L");

        pFinal.setText(new PriceFormat(model.getCPetrolVol()).getResult()+" L");
        pNet.setText("Rs."+new PriceFormat((SystemStatus.getfDiesel()-SystemStatus.getiPetrol())*SystemStatus.getpRate()+"").getResult()+"/-");

        dInitial.setText(new PriceFormat(model.getIDieselVol()).getResult()+" L");
        dFinal.setText(new PriceFormat(model.getCDieselVol()).getResult()+" L");
        dNet.setText("Rs."+new PriceFormat((SystemStatus.getfPetrol()-SystemStatus.getiDiesel())*SystemStatus.getdRate()+"").getResult()+"/-");

        cInitial.setText("Rs./-");
        cFinal.setText("Rs."+model.getCExtra()+"/-");

        netOil.setText("Rs."+new PriceFormat(model.getOilIncome()).getResult()+"/-");
        netExtra.setText("Rs."+new PriceFormat(model.getCExtra()).getResult()+"/-");

        TodayClosingTask tasks=new TodayClosingTask(model);
        TaskManager.executeTask(tasks);

    }


    public static  DailyAnalyticsController getInstance() {
        return instance;
    }


}
