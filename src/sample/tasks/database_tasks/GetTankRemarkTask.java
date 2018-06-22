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
            int count;
            String type;

            Statement getStatus=connection.createStatement();

            ResultSet resultSet=getStatus.executeQuery("SELECT * FROM Tankers WHERE TANKER_NO="+tankerNumber);

                 if (resultSet.next()) {
                     avg = resultSet.getFloat(2) / resultSet.getInt(3);
                     count = resultSet.getInt(3);
                     type = resultSet.getString(4);
                     TankerModel tankerModel = new TankerModel(avg, count, type, (String.valueOf(tankerNumber) + "X"));
                     if (taskInterface != null)
                         taskInterface.onComplete(tankerModel);
                 } else {
                     if (taskInterface != null)
                         taskInterface.onError();
                 }





        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
