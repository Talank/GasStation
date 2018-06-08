package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetupCounterStockTask extends Tasks {

    private String counterStock;

    public SetupCounterStockTask(String counterStock) {
        this.counterStock=counterStock;
    }


    @Override
    public void execute() {
        try {
            PreparedStatement insertStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
            insertStock.setString(1,counterStock);
            insertStock.setDate(2,SystemStatus.getCurrentDate());
            insertStock.setInt(3,13);
            insertStock.execute();

            insertStock.close();

            connection.commit();

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Counter Saved",1000);
            snackBarToastGenerator.show();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Setting Up Counter Value.\n"+"Please Retry Again.";
            String title="Counter Setup Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
