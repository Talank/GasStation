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


            if(SystemStatus.getdRate()!=model.getrDiesel()&&SystemStatus.getpRate()!=model.getrPetrol()) {

                PreparedStatement setRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");

                setRate.setString(1,String.valueOf(model.getrDiesel()));
                setRate.setDate(2,SystemStatus.getCurrentDate());
                setRate.setInt(3,9);
                setRate.addBatch();

                setRate.setString(1,String.valueOf(model.getrPetrol()));
                setRate.setDate(2,SystemStatus.getCurrentDate());
                setRate.setInt(3,10);
                setRate.addBatch();

                setRate.executeBatch();

                SystemStatus.setpRate(model.getrPetrol());
                SystemStatus.setdRate(model.getrDiesel());
            } else {

                if(SystemStatus.getdRate()!=model.getrDiesel()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                    updateRate.setString(1,String.valueOf(model.getrDiesel()));
                    updateRate.setDate(2,SystemStatus.getCurrentDate());
                    updateRate.setInt(3,9);

                    updateRate.execute();
                    SystemStatus.setdRate(model.getrDiesel());
                }

                if(SystemStatus.getpRate()!=model.getrPetrol()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                    updateRate.setString(1,String.valueOf(model.getrPetrol()));
                    updateRate.setDate(2,SystemStatus.getCurrentDate());
                    updateRate.setInt(3,10);

                    updateRate.execute();
                    SystemStatus.setdRate(model.getrDiesel());
                }
            }

            connection.commit();

        } catch(SQLException e) {
            System.out.println(e.getNextException().getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }


        }
    }
}
