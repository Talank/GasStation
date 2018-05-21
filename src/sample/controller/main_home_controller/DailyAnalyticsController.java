package sample.controller.main_home_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
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

        toolBar.setTitle("Today Analytics");
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
        pInitial.setText(model.getIPetrolVol()+" L");

        pFinal.setText(model.getCPetrolVol()+" L");
        pNet.setText("Rs."+(SystemStatus.getfDiesel()-SystemStatus.getiPetrol())*SystemStatus.getpRate()+"/-");

        dInitial.setText(model.getIDieselVol()+" L");
        dFinal.setText(model.getCDieselVol()+" L");
        dNet.setText("Rs."+(SystemStatus.getfPetrol()-SystemStatus.getiDiesel())*SystemStatus.getdRate()+"/-");

        cInitial.setText("Rs."+model.getIExtra()+"/-");
        cFinal.setText("Rs."+model.getCExtra()+"/-");

        netOil.setText("Rs."+model.getOilIncome()+"/-");
        netExtra.setText("Rs."+model.getCExtra()+"/-");

        TodayClosingTask tasks=new TodayClosingTask(model);
        TaskManager.executeTask(tasks);

    }


    public static  DailyAnalyticsController getInstance() {
        return instance;
    }


}
