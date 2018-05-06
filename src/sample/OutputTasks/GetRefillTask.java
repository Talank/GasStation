package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.RefillModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GetRefillTask extends Task<ArrayList<RefillModel>> {

    @Override
    protected ArrayList<RefillModel> call() throws Exception {
        ArrayList<RefillModel> arrayList=new ArrayList<>();

        int totalSize,currentSize=0;

        try {
            Statement getRefill= Tasks.getConnection().createStatement();

            ResultSet resultSet=getRefill.executeQuery("SElECT count(*) FROM Refill");
            resultSet.next();

            totalSize=resultSet.getInt(1);

            resultSet=getRefill.executeQuery("SELECT * FROM Refill");
            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                arrayList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),resultSet.getFloat(3),resultSet.getFloat(4),resultSet.getFloat(5),resultSet.getFloat(6),resultSet.getDate(7)));

                Thread.sleep(300);//To be removed

                currentSize++;
                updateProgress(currentSize,totalSize);
            }
            resultSet.close();
            getRefill.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
