package sample.controller.main_home_controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.controller.dialog_controller.ClosingCalculationController;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import javafx.stage.Modality;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.GetDailyTotalAnalyticsModelTask;
import sample.tasks.database_tasks.TodayClosingTask;

import java.io.IOException;


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


        /*pInitial.setText(String.valueOf(SystemStatus.getiPetrol())+" L");
        pFinal.setText(String.valueOf(SystemStatus.getfPetrol())+" L");
        pNet.setText("Rs."+String.valueOf((SystemStatus.getpRate()*(SystemStatus.getfPetrol()-SystemStatus.getiPetrol())))+"/-");

        dInitial.setText(String.valueOf(SystemStatus.getiDiesel())+" L");
        dFinal.setText(String.valueOf(SystemStatus.getfDiesel())+" L");
        dNet.setText("Rs."+String.valueOf((SystemStatus.getdRate()*(SystemStatus.getfDiesel()-SystemStatus.getiDiesel())))+"/-");

        cInitial.setText("Rs."+String.valueOf(SystemStatus.getiCounter()));

        float price=DatabaseHandler.getTotalInstantPrice();
        if(!(price==0||price==-1)) {
            cFinal.setText("Rs."+String.valueOf(price+SystemStatus.getiCounter())+"/-");
            cNet.setText("Rs."+String.valueOf(price)+"/-");
        }
        if(DatabaseHandler.updateFinalReadings(SystemStatus.getfDiesel(),SystemStatus.getfPetrol(),price)) {
            SystemLogging.getLogger().fine("SuccessFully Updated Final Readings.");
        } else {
            SystemLogging.getLogger().severe("Error Updating Final Readings!");
        }*/

    }

    public void setModel(DailyTotalAnalyticModel model) {
        this.model=model;
        pInitial.setText(model.getIPetrolVol()+" L");
        pFinal.setText(model.getCPetrolVol()+" L");
        pNet.setText("Rs."+(Float.parseFloat(model.getIPetrolVol())*SystemStatus.getpRate())+"/-");

        dInitial.setText(model.getIDieselVol()+" L");
        dFinal.setText(model.getCDieselVol()+" L");
        dNet.setText("Rs."+(Float.parseFloat(model.getIDieselVol())*SystemStatus.getdRate())+"/-");

        cInitial.setText("Rs."+model.getIExtra()+"/-");
        cFinal.setText("Rs."+model.getCExtra()+"/-");

        netOil.setText("Rs."+model.getOilIncome()+"/-");
        netExtra.setText("Rs."+model.getIExtra()+"/-");


    }


    @FXML
    public void loadClosing(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/finalReadings.fxml"));
        Stage stage=new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void showResults() throws IOException {

        TodayClosingTask task=new TodayClosingTask(model);
        TaskManager.executeTask(task);


        float[] data={(Float.parseFloat(model.getIDieselVol())+Float.parseFloat(model.getCDieselVol())),(SystemStatus.getfDiesel()-SystemStatus.getiDiesel()),((Float.parseFloat(model.getIPetrolVol())+Float.parseFloat(model.getCPetrolVol()))),(SystemStatus.getfPetrol()-SystemStatus.getiPetrol())};
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/closingCalculation.fxml"));
        Parent root=loader.load();
        ClosingCalculationController controller=loader.getController();
        controller.setData(data);
        Stage stage=new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();


    }

    public static  DailyAnalyticsController getInstance() {
        return instance;
    }


}
