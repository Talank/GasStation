package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.controller.SystemLogging;
import sample.model.SystemStatus;
import sample.model.TankerModel;
import sample.tasks.Tasks;

import javax.swing.plaf.nimbus.State;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class GetTankRemarkTask extends Task<TankerModel> {

    private int tankerNumber;

    public GetTankRemarkTask(int tankerNumber) {
        this.tankerNumber = tankerNumber;
    }

    @Override
    protected TankerModel call() throws Exception {
        TankerModel tankerModel = null;
        try {

            float avg;
            int count;
            String type;

            Statement getStatus=Tasks.getConnection().createStatement();

            ResultSet resultSet=getStatus.executeQuery("SELECT * FROM Tankers WHERE TANKER_NO="+tankerNumber);

            if (resultSet.next()) {
                avg = resultSet.getFloat(2) / resultSet.getInt(3);
                count = resultSet.getInt(3);
                type = resultSet.getString(4);
                tankerModel = new TankerModel(avg, count, type, (String.valueOf(tankerNumber) + "X"));
            } else {
                failed();
            }

        } catch(SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("Tankers remark not done");
            System.out.println(e.getMessage());
        }

        return tankerModel;
    }

}
