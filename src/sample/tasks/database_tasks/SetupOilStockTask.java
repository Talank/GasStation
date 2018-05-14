package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
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

            Date currentDate=Date.valueOf((new NepaliDateGenerator()).getConvertedNepaliDate());

            if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselStock)&&SystemStatus.getPetrolStock()!=Float.parseFloat(petrolStock)) {
                connection.setAutoCommit(false);

                Statement updateStock=connection.createStatement();
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+dieselStock+"', DATE='"+currentDate+"' WHERE ID=14");
                updateStock.addBatch("UPDATE SystemStatus SET VALUE='"+petrolStock+"', DATE='"+currentDate+"' WHERE ID=15");
                updateStock.executeBatch();

                connection.commit();

                connection.setAutoCommit(true);
                SystemStatus.setDieselStock(Float.parseFloat(dieselStock));
                SystemStatus.setPetrolStock(Float.parseFloat(petrolStock));


            } else {

                if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselStock)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+dieselStock+"', DATE='"+currentDate+"' WHERE ID=14");
                    updateStock.execute();
                    SystemStatus.setDieselStock(Float.parseFloat(dieselStock));
                }

                if(SystemStatus.getPetrolStock()!=Float.parseFloat(petrolStock)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+petrolStock+"', DATE='"+currentDate+"' WHERE ID=15");
                    updateStock.execute();
                    SystemStatus.setPetrolStock(Float.parseFloat(petrolStock));
                }

            }


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
