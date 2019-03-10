package np.com.spirrotech.application.daily_analytics.today_closing;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import np.com.spirrotech.application.home.AppHomeController;
import np.com.spirrotech.library.toolbar.ToolBar;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.model.TodayClosingTask;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;
import np.com.spirrotech.dao.transaction_model.DailyTotalAnalyticModel;

import np.com.spirrotech.utils.tasks.TaskManager;


/*
* This class provides a details of total transaction of the day
*
* */

public class DailyAnalyticsController {

    @FXML
    private Label pInitial,pFinal,dInitial,dFinal,cInitial,cFinal,pNet,dNet,netOil,netExtra;

    @FXML
    private Label dStockAdjust, dStockLoss, pStockAdjust, pStockLoss;

    @FXML
    private ToolBar toolBar;

    private Precision precision;
    private PriceFormat priceFormat;

    private DailyTotalAnalyticModel model;


    @FXML
    public void initialize() {

        toolBar.setTitle("Today Analytics");//title
        toolBar.setBackButtonInterface(() -> {
            BorderPane borderPane= FXMLLoader.load(getClass().getClassLoader().getResource("application/daily_analytics/detail_analytics/dailyTotalAnalytics.fxml"));
            AppHomeController.getInstance().loadStages(borderPane);
        });

        precision = new Precision();
        priceFormat = new PriceFormat();

    }

    public void setModel(DailyTotalAnalyticModel model) {

        this.model = model;

        pInitial.setText(priceFormat.format(precision.format(Float.parseFloat(model.getIPetrolVol())))+" L");

        pFinal.setText(priceFormat.format(precision.format(Float.parseFloat(model.getCPetrolVol())))+" L");
        pNet.setText("Rs. "+priceFormat.format(precision.format((SystemStatus.getfPetrol()-SystemStatus.getiPetrol())*SystemStatus.getpRate()))+"/-");

        dInitial.setText(priceFormat.format(precision.format(Float.parseFloat(model.getIDieselVol())))+" L");
        dFinal.setText(priceFormat.format(precision.format(Float.parseFloat(model.getCDieselVol())))+" L");
        dNet.setText("Rs. "+priceFormat.format(precision.format((SystemStatus.getfDiesel()-SystemStatus.getiDiesel())*SystemStatus.getdRate()))+"/-");

        cInitial.setText("Rs. 0.0 /-");
        cFinal.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getCExtra())))+"/-");

        netOil.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getOilIncome())))+"/-");
        netExtra.setText("Rs. "+priceFormat.format(precision.format(Float.parseFloat(model.getCExtra())))+"/-");

        float dieselStock = (SystemStatus.getDieselStock()-Float.parseFloat(model.getIDieselVol()));

        float petrolStock = (SystemStatus.getPetrolStock()-Float.parseFloat(model.getIPetrolVol()));

        if (dieselStock < 0) {
            //we have a -ve stock i.e the instant transaction is greater than the stock present
            dStockLoss.setText(priceFormat.format(precision.format(Math.abs(dieselStock))) + " L");

            //the final stock will be 0
            //no need to set the dStockAdjust value to 0, as this is it's default value
        } else {
            //we don't have -ve stock, so the loss will be 0
            //default value for the dStockLoss is 0

            //the final stock will be equal to dieselStock
            dStockAdjust.setText(priceFormat.format(precision.format(Math.abs(dieselStock)) + " L"));
        }

        if (petrolStock < 0) {
            pStockLoss.setText(priceFormat.format(precision.format(Math.abs(petrolStock))) + " L");
        } else {
            pStockAdjust.setText(priceFormat.format(precision.format(Math.abs(petrolStock)) + " L"));
        }

    }

    @FXML
    void conformClosing(MouseEvent event) {

        //close the current session
        TodayClosingTask tasks=new TodayClosingTask(model);
        TaskManager.getInstance().executeTask(tasks);
    }



}
