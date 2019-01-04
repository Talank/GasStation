package np.com.spirrotech.application.daily_analytics.detail_analytics;

import javafx.concurrent.Task;
import np.com.spirrotech.model.transaction_model.DailyTotalAnalyticModel;
import np.com.spirrotech.utils.nepali_date.NepaliDateGenerator;
import np.com.spirrotech.utils.tasks.Tasks;

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

            NepaliDateGenerator generator=new NepaliDateGenerator();

            resultSet=getAnalyticModel.executeQuery("SELECT * FROM DailyAnalytics");

            DailyTotalAnalyticModel model;

            while(resultSet.next()) {
                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                model=new DailyTotalAnalyticModel(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),String.valueOf(resultSet.getFloat(5)),generator.convertDate(resultSet.getDate(8)));
                model.setOilIncome(resultSet.getFloat(6),resultSet.getFloat(7));
                model.setTotal();

                arrayList.add(model);

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
