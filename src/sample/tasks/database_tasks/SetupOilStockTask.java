package sample.tasks.database_tasks;

import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.SQLException;
import java.sql.Statement;

public class SetupOilStockTask extends Tasks {

    private String dieselStock,petrolStock;

    public SetupOilStockTask(String dieselStock,String petrolStock) {
        this.dieselStock=dieselStock;
        this.petrolStock=petrolStock;
    }


    @Override
    public void execute() {
        try {
            connection.setAutoCommit(false);

            Statement updateStock=connection.createStatement();
            updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+dieselStock+"', DATE='"+DatabaseHandler.getCurrentDate()+"' WHERE ID=14");
            updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+petrolStock+"', DATE='"+DatabaseHandler.getCurrentDate()+"' WHERE ID=15");
            updateStock.executeBatch();

            connection.commit();

            connection.setAutoCommit(true);



        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
