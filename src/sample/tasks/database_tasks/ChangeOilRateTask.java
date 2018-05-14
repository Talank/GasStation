package sample.tasks.database_tasks;

import sample.model.RateModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeOilRateTask extends Tasks {

    private RateModel model;

    public ChangeOilRateTask(RateModel model) {
        this.model=model;
    }



    @Override
    public void execute() {
        try {

            Date currentDate=Date.valueOf((new NepaliDateGenerator()).getConvertedNepaliDate());

            if(SystemStatus.getdRate()!=model.getrDiesel()&&SystemStatus.getpRate()!=model.getrPetrol()) {
                connection.setAutoCommit(false);
                Statement setRate=connection.createStatement();
                setRate.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(model.getrDiesel())+"',DATE='"+currentDate+"' WHERE ID=9");
                setRate.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(model.getrPetrol())+"',DATE='"+currentDate+"' WHERE ID=10");
                setRate.executeBatch();

                connection.commit();
                connection.setAutoCommit(true);

                SystemStatus.setpRate(model.getrPetrol());
                SystemStatus.setdRate(model.getrDiesel());
            } else {

                if(SystemStatus.getdRate()!=model.getrDiesel()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+String.valueOf(model.getrDiesel())+"',DATE='"+currentDate+"' WHERE ID=9");
                    updateRate.execute();
                    SystemStatus.setdRate(model.getrDiesel());
                }

                if(SystemStatus.getpRate()!=model.getrPetrol()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+String.valueOf(model.getrPetrol())+"',DATE='"+currentDate+"' WHERE ID=10");
                    updateRate.execute();
                    SystemStatus.setdRate(model.getrDiesel());
                }
            }

        } catch(SQLException e) {
            System.out.println(e.getNextException().getMessage());
        }
    }
}
