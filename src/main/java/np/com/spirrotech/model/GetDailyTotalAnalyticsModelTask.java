package np.com.spirrotech.model;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.dao.transaction_model.DailyTotalAnalyticModel;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *Computes and returns the {@link DailyTotalAnalyticModel} from the database
 */

public class GetDailyTotalAnalyticsModelTask extends Tasks {

    @Override
    public void execute() {
        DailyTotalAnalyticModel model=new DailyTotalAnalyticModel();
        try {

            Statement getAnalytics= Tasks.getConnection().createStatement();
            ResultSet resultSet=getAnalytics.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7");
            resultSet.next();
            model.setCDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setCPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setCExtra(resultSet.getString(1));
            model.setExtraIncome(model.getCExtra());
            resultSet.close();

            float iDieselVol = SystemStatus.getfDiesel()-(SystemStatus.getiDiesel()+Float.parseFloat(model.getCDieselVol()));

            float iPetrolVol = SystemStatus.getfPetrol()-(SystemStatus.getiPetrol()+Float.parseFloat(model.getCPetrolVol()));

            if (iDieselVol < 0 || iPetrolVol < 0) {
                taskInterface.onError("Diesel: " + iDieselVol + ", Petrol: " + iPetrolVol);
                return;
            } else {
                model.setIDieselVol(String.valueOf(iDieselVol));
                model.setIPetrolVol(String.valueOf(iPetrolVol));
            }

            model.setOilIncome(SystemStatus.getdRate(),SystemStatus.getpRate());
            model.setTotal();
            SystemLogging.getLogger().fine("DailyAnalytics fetched ");


        } catch(SQLException e) {
            SystemLogging.getLogger().warning("DailyAnalytics couldn't be fetched ");
            System.out.println(e.getMessage());
        }

        taskInterface.onComplete(model);
    }

    /**
     *Gets the information from SystemStatus table, makes the model and passes to the caller.
     */

}
