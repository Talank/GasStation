package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.TankerModel;
import sample.tasks.Tasks;

import javax.swing.plaf.nimbus.State;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetTankRemarkTask extends Tasks {

    private int tankerNumber;

    public GetTankRemarkTask(int tankerNumber) {
        this.tankerNumber = tankerNumber;
    }

    @Override
    public void execute() {

        try {

            float avg;
            int count=0;
            String type;

            Statement getStatus=connection.createStatement();

            System.out.println("Tanker Number:"+tankerNumber);

            ResultSet resultSet=getStatus.executeQuery("SELECT * FROM Tankers WHERE TANKER_NO="+tankerNumber);
            resultSet.next();
            avg = resultSet.getFloat(2) / resultSet.getInt(3);
            count=resultSet.getInt(3);
            type=resultSet.getString(4);

            TankerModel tankerModel=new TankerModel(avg,count,type);

            System.out.println("Average:"+avg);
            if(taskInterface!=null)
                taskInterface.onComplete(tankerModel);
            /*if(resultSet.next()) {

            } else {
                System.out.println("No result Found");
            }*/


        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
