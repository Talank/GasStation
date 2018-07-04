package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.SystemStatus;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.Tasks;

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

            Statement getAnalytics=connection.createStatement();
            ResultSet resultSet=getAnalytics.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7");
            resultSet.next();
            model.setCDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setCPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setCExtra(resultSet.getString(1));
            model.setExtraIncome(model.getCExtra());
            resultSet.close();

            model.setIDieselVol(String.valueOf(SystemStatus.getfDiesel()-(SystemStatus.getiDiesel()+Float.parseFloat(model.getCDieselVol()))));
            model.setIPetrolVol(String.valueOf(SystemStatus.getfPetrol()-(SystemStatus.getiPetrol()+Float.parseFloat(model.getCPetrolVol()))));


            model.setOilIncome(SystemStatus.getdRate(),SystemStatus.getpRate());
            model.setTotal();
            SystemLogging.getLogger().fine("DailyAnalytics fetched ");

            if(taskInterface!=null) {
                taskInterface.onComplete(model);
            }


        } catch(SQLException e) {
            SystemLogging.getLogger().warning("DailyAnalytics couldn't be fetched ");
            System.out.println(e.getMessage());
            taskInterface.onError();
        }
    }
}
