package np.com.spirrotech.application.settings;

import javafx.concurrent.Task;
import np.com.spirrotech.utils.nepali_date.NepaliDateGenerator;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetSettingsTask extends Task<String[][]> {

    @Override
    protected String[][] call() throws Exception {
        String model[][];
        model=new String[10][2];
        try {
            Statement getSettings= Tasks.getConnection().createStatement();
            ResultSet resultSet=getSettings.executeQuery("SELECT VALUE,DATE FROM SystemStatus WHERE ID<18 AND ID>6");
            int i=0;

            NepaliDateGenerator generator=new NepaliDateGenerator();

            while(resultSet.next()) {
                if(i==1) {
                    resultSet.next();
                }
                model[i][0]=resultSet.getString(1);
                model[i][1]=generator.convertDate(resultSet.getDate(2));
                i++;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return model;

    }
}
