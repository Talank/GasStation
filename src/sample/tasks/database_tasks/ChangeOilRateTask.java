package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.RateModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Changes the rates of diesel and petrol.
 */

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

                setRate.close();
            } else {

                if(SystemStatus.getdRate()!=model.getrDiesel()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                    updateRate.setString(1,String.valueOf(model.getrDiesel()));
                    updateRate.setDate(2,SystemStatus.getCurrentDate());
                    updateRate.setInt(3,9);

                    updateRate.execute();

                    updateRate.close();
                }

                if(SystemStatus.getpRate()!=model.getrPetrol()) {
                    PreparedStatement updateRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                    updateRate.setString(1,String.valueOf(model.getrPetrol()));
                    updateRate.setDate(2,SystemStatus.getCurrentDate());
                    updateRate.setInt(3,10);

                    updateRate.execute();

                    updateRate.close();
                }
            }

            connection.commit();
            SystemLogging.getLogger().fine("SystemStatus Oil Rates Successfully changed");

            SystemStatus.setpRate(model.getrPetrol());
            SystemStatus.setdRate(model.getrDiesel());

            Platform.runLater(()->AppHomeController.getInstance().setRates());

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Oil Rates Saved",1000);
            snackBarToastGenerator.show();


        } catch(SQLException e) {
            System.out.println(e.getNextException().getMessage());
            SystemLogging.getLogger().warning("SystemStatus failed to change oil rates");
            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback to stable stage in SystemStatus");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning("Rollback failed in SystemStatus");
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Changing Oil Rates.\n"+"Please Retry Again.";
            String title="Oil Rate Update Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();


        }
    }
}
