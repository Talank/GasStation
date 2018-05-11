package sample.tasks.database_tasks;

import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

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
            PreparedStatement insertStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+counterStock+"', DATE='"+DatabaseHandler.getCurrentDate()+"' WHERE ID=13");
            insertStock.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
