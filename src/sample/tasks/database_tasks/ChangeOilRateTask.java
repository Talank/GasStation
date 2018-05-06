package sample.tasks.database_tasks;

import sample.model.RateModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.Statement;

public class ChangeOilRateTask extends Tasks {

    private RateModel model;

    public ChangeOilRateTask(RateModel model) {
        this.model=model;
    }



    @Override
    public void execute() {
        try {

            connection.setAutoCommit(false);

            Statement setRate=connection.createStatement();
            setRate.addBatch("UPDATE SystemStatus SET VALUE="+String.valueOf(model.getrDiesel())+",DATE="+DatabaseHandler.getCurrentDate().toString()+" WHERE ID=9");
            setRate.addBatch("UPDATE SystemStatus SET VALUE="+String.valueOf(model.getrPetrol())+",DATE="+DatabaseHandler.getCurrentDate().toString()+" WHERE ID=10");
            setRate.executeBatch();

            connection.commit();
            connection.setAutoCommit(true);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
