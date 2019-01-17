package np.com.spirrotech.application.daily_analytics.today_closing;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private ToolBar toolBar;

    private Precision precision;
    private PriceFormat priceFormat;


    @FXML
    public void initialize() {

        toolBar.setTitle("Today Analytics");//title
        toolBar.setBackPath("application/daily_analytics/detail_analytics/dailyTotalAnalytics.fxml");

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
