package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.RefillModel;
import sample.nepali_date.NepaliDateGenerator;
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


            ArrayList<RefillModel> smallList=new ArrayList<>();

            NepaliDateGenerator generator=new NepaliDateGenerator();

            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),generator.convertDate(resultSet.getDate(4)),resultSet.getString(3)));

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

                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),generator.convertDate(resultSet.getDate(4)),resultSet.getString(3)));
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
