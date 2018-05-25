package sample.tasks.database_tasks;

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
            PreparedStatement insertStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+counterStock+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=13");
            insertStock.execute();
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }
    }
}
