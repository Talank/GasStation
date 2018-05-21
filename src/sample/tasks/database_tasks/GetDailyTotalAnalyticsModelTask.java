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
            ResultSet resultSet=getAnalytics.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7");
            resultSet.next();
            model.setCDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setCPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setCExtra(resultSet.getString(1));
            resultSet.close();

            System.out.println("iDiesel:"+SystemStatus.getiDiesel());


            model.setIDieselVol(String.valueOf(SystemStatus.getfDiesel()-(SystemStatus.getiDiesel()+Float.parseFloat(model.getCDieselVol()))));
            model.setIPetrolVol(String.valueOf(SystemStatus.getfPetrol()-(SystemStatus.getiPetrol()+Float.parseFloat(model.getCPetrolVol()))));

            model.setOilIncome(String.valueOf(Float.parseFloat(model.getCDieselVol())*SystemStatus.getdRate()+Float.parseFloat(model.getCPetrolVol())*SystemStatus.getpRate()));

            if(taskInterface!=null) {
                taskInterface.onComplete(model);
            }


        } catch(SQLException e) {
            System.out.println(e.getMessage());
            taskInterface.onError();
        }
    }
}
