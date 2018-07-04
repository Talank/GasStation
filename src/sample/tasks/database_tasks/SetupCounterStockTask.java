package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *Changes the default counter value.
 */

public class SetupCounterStockTask extends Tasks {

    private String counterStock;

    /**
     *
     * @param counterStock the counter stock to be saved
     */

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
            SystemLogging.getLogger().fine(" SystemStatus counter stock is updated");
            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Counter Saved",1000);
            snackBarToastGenerator.show();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning(" SystemStatus counter stock couldn't be updated");

            rollBack();

            String message="Error Setting Up Counter Value.\n"+"Please Retry Again.";
            String title="Counter Setup Error";
            reportError(title, message, this);
        }
    }
}
