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

            ResultSet resultSet=getRefill.executeQuery("SElECT count(*) FROM DieselRefill");
            resultSet.next();

            totalSize=resultSet.getInt(1);
            resultSet.close();

            resultSet=getRefill.executeQuery("SElECT count(*) FROM PetrolRefill");
            resultSet.next();

            totalSize+=resultSet.getInt(1);

            resultSet=getRefill.executeQuery("SELECT * FROM DieselRefill");
            //resultSet.next();

          // resultSet= getRefill.executeQuery("SELECT * FROM TANKERS");
           // resultSet.next();

            ArrayList<RefillModel> smallList=new ArrayList<>();

            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),resultSet.getFloat(3),resultSet.getString(4),resultSet.getInt(5)));
                 //System.out.print(smallList.get(i).getTankerNumber());
                Thread.sleep(300);//To be removed
                currentSize++;
                updateProgress(currentSize,totalSize);

            }

            arrayList.add(smallList);



            resultSet=getRefill.executeQuery("SELECT * FROM PetrolRefill");

            smallList=new ArrayList<>();
            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),resultSet.getFloat(3),resultSet.getString(4),resultSet.getInt(5)));

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
