package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GetDailyTotalAnalyticsTask extends Task<ArrayList<DailyTotalAnalyticModel>> {

    @Override
    protected ArrayList<DailyTotalAnalyticModel> call() throws Exception {

        int totalSize,currentSize=0;
        ArrayList<DailyTotalAnalyticModel> arrayList=new ArrayList<>();

        try {

            Statement getAnalyticModel= Tasks.getConnection().createStatement();
            ResultSet resultSet=getAnalyticModel.executeQuery("SELECT count(*) FROM DailyAnalytics");

            resultSet.next();

            totalSize=resultSet.getInt(1);

            resultSet=getAnalyticModel.executeQuery("SELECT * FROM DailyAnalytics");

            while(resultSet.next()) {
                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                arrayList.add(new DailyTotalAnalyticModel(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),String.valueOf(resultSet.getFloat(5)+resultSet.getFloat(6)),resultSet.getString(7),String.valueOf(resultSet.getFloat(7)+resultSet.getFloat(5)+resultSet.getFloat(6)),resultSet.getString(10)));

                currentSize++;
                updateProgress(currentSize,totalSize);
            }
            resultSet.close();
            getAnalyticModel.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return arrayList;

    }
}
