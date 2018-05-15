package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.RefillModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GetRefillTask extends Task<ArrayList<ArrayList<RefillModel>>> {


    @Override
    protected ArrayList<ArrayList<RefillModel>> call() throws Exception {
        ArrayList<ArrayList<RefillModel>> arrayList=new ArrayList<>(2);

        int totalSize,currentSize=0;

        try {
            Statement getRefill= Tasks.getConnection().createStatement();

            ResultSet resultSet=getRefill.executeQuery("SElECT count(*) FROM DIESEL_REFILL");
            resultSet.next();

            totalSize=resultSet.getInt(1);
            resultSet.close();

            resultSet=getRefill.executeQuery("SElECT count(*) FROM PETROL_REFILL");
            resultSet.next();

            totalSize+=resultSet.getInt(1);

            resultSet=getRefill.executeQuery("SELECT * FROM DIESEL_REFILL");

            ArrayList<RefillModel> smallList=new ArrayList<>();

            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),resultSet.getFloat(3),resultSet.getString(4)));

                Thread.sleep(300);//To be removed
                currentSize++;
                updateProgress(currentSize,totalSize);
            }

            arrayList.add(smallList);



            resultSet=getRefill.executeQuery("SELECT * FROM PETROL_REFILL");

            smallList=new ArrayList<>();
            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),resultSet.getFloat(3),resultSet.getString(4)));

                Thread.sleep(300);//To be removed
                currentSize++;
                updateProgress(currentSize,totalSize);
            }

            arrayList.add(smallList);

            resultSet.close();
            getRefill.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
