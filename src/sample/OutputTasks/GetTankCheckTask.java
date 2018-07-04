package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.RefillModel;
import sample.model.TankCheckModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
/*** This class is not currently used ****/

/**
 * @deprecated
 */

public class GetTankCheckTask extends Task<ArrayList<TankCheckModel>> {


    @Override
    protected ArrayList<TankCheckModel> call() throws Exception {
        ArrayList<TankCheckModel> smallList=new ArrayList<>();

        try {
            Statement stmt= Tasks.getConnection().createStatement();



            ResultSet resultSet=stmt.executeQuery("SELECT * FROM TANKERS");



            int i=0;

            while(resultSet.next()) {

                if(isCancelled()) {
                    smallList.clear();
                    break;
                }

                smallList.add(new TankCheckModel(resultSet.getInt(1),resultSet.getFloat(2),resultSet.getInt(3)));
                System.out.print("The tank loss"+smallList.get(i).getTankLoss() +"The count is "+smallList.get(i).getCounter());
                Thread.sleep(300);//To be removed
//                currentSize++;
//                updateProgress(currentSize,totalSize);
                i++;

            }

            //arrayList.add(smallList);





            resultSet.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return smallList;
    }
}
