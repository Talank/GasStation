package np.com.spirrotech.model;

import javafx.concurrent.Task;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.dao.TankerModel;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class GetTankRemarkTask extends Task<TankerModel> {

    private String tankerNumber;

    public GetTankRemarkTask(String tankerNumber) {
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

            ResultSet resultSet=getStatus.executeQuery("SELECT * FROM Tankers WHERE TANKER_NO='"+tankerNumber + "'");

            if (resultSet.next()) {
                avg = resultSet.getFloat(2) / resultSet.getInt(3);
                count = resultSet.getInt(3);
                type = resultSet.getString(4);
                tankerModel = new TankerModel(avg, count, type, (tankerNumber + "X"));
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
