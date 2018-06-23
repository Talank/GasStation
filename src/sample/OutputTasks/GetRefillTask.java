package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.RefillModel;
import sample.model.TankerModel;
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

            resultSet=getRefill.executeQuery("SELECT * FROM DieselRefill,Tankers where DieselRefill.Tanker_no=Tankers.Tanker_no");

            ArrayList<RefillModel> smallList=new ArrayList<>();

            NepaliDateGenerator generator=new NepaliDateGenerator();

            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

            //The average value is to be calculated and Pass as a parameter to check the tanker status accordingly

                float avgValue=resultSet.getFloat(6)/resultSet.getInt(7);
                StringBuilder stringBuilder=new StringBuilder(resultSet.getString(3));

                if (avgValue >= 0 && avgValue <= 20) {
                    stringBuilder.append("G");
                } else if (avgValue >= 21 && avgValue <= 50) {
                    stringBuilder.append("B");
                } else if (avgValue > 50) {
                    stringBuilder.append("R");
                } else
                    stringBuilder.append("P");

               String codeData= stringBuilder.toString();


                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),generator.convertDate(resultSet.getDate(4)),
                        new TankerModel(avgValue,resultSet.getInt(7),resultSet.getString(8),codeData)));

                currentSize++;
                updateProgress(currentSize,totalSize);

            }

            arrayList.add(smallList);



            resultSet=getRefill.executeQuery("SELECT * FROM PetrolRefill,Tankers where PetrolRefill.Tanker_no=Tankers.Tanker_no");

            smallList=new ArrayList<>();
            while(resultSet.next()) {

                if(isCancelled()) {
                    arrayList.clear();
                    break;
                }

                //The average value is to be calculated and Pass as a parameter to check the tanker status accordingly

                float avgValue=resultSet.getFloat(6)/resultSet.getInt(7);
                StringBuilder stringBuilder=new StringBuilder(resultSet.getString(3));

                if (avgValue >= 0 && avgValue <= 20) {
                    stringBuilder.append("G");
                } else if (avgValue >= 21 && avgValue <= 50) {
                    stringBuilder.append("B");
                } else if (avgValue > 50) {
                    stringBuilder.append("R");

                } else
                    stringBuilder.append("P");

                String codeData= stringBuilder.toString();


                smallList.add(new RefillModel(resultSet.getFloat(1),resultSet.getFloat(2),generator.convertDate(resultSet.getDate(4)),
                        new TankerModel(avgValue,resultSet.getInt(7),resultSet.getString(8),codeData)));
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
