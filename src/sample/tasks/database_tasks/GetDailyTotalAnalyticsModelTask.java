package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetDailyTotalAnalyticsModelTask extends Tasks {

    @Override
    public void execute() {
        DailyTotalAnalyticModel model=new DailyTotalAnalyticModel();
        try {

            Statement getAnalytics=connection.createStatement();
            ResultSet resultSet=getAnalytics.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID<7");
            resultSet.next();

            model.setIDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setIPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setIExtra(resultSet.getString(1));
            resultSet.next();
            model.setCDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setCPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setCExtra(resultSet.getString(1));
            resultSet.close();

            model.setOilIncome(String.valueOf(((Float.parseFloat(model.getIDieselVol())+Float.parseFloat(model.getCDieselVol()))*SystemStatus.getdRate()+(Float.parseFloat(model.getIPetrolVol())+ Float.parseFloat(model.getCPetrolVol())*SystemStatus.getpRate()))));

            taskInterface.onComplete(model);

        } catch(SQLException e) {
            System.out.println(e.getMessage());
            taskInterface.onError();
        }
    }
}
