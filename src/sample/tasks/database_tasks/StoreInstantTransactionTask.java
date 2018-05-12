package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.SystemStatus;

import sample.model.transaction_model.Transaction;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class StoreInstantTransactionTask extends Tasks {

    private List<Transaction> instantTransaction;


    public StoreInstantTransactionTask(List<Transaction> instantTransaction) {
        this.instantTransaction=instantTransaction;
    }

    @Override
    public void execute() {
        float pVolume=0,dVolume=0,ePrice=0;


        for(Transaction transaction:instantTransaction) {
            if(transaction.getMode()==2) {
                ePrice+=transaction.getPrice();
            } else {
                if(transaction.getMode()==0) {
                    dVolume+=transaction.getVolume();
                } else {
                    pVolume+=transaction.getVolume();
                }
            }
        }

        if((SystemStatus.getDieselStock()-dVolume)<0||(SystemStatus.getPetrolStock()-pVolume)<0) {
            System.out.println("Out Of Stock!!!");
            return;//Out Of Stock
        }

        try {

            float petrolStock=pVolume;
            float dieselStock=dVolume;

            Statement getInstant=connection.createStatement();
            ResultSet resultSet;

            resultSet=getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>0 AND ID<4 OR ID=14 OR ID=15");

            resultSet.next();
            dVolume+=Float.parseFloat(resultSet.getString(1));
            resultSet.next();
            pVolume+=Float.parseFloat(resultSet.getString(1));
            resultSet.next();
            ePrice+=Float.parseFloat(resultSet.getString(1));
            resultSet.next();
            dieselStock=Float.parseFloat(resultSet.getString(1))-dieselStock;
            resultSet.next();
            petrolStock=Float.parseFloat(resultSet.getString(1))-petrolStock;

            SystemStatus.setDieselStock(dieselStock);
            SystemStatus.setPetrolStock(petrolStock);

            resultSet.close();
            connection.setAutoCommit(false);
            Statement updateInstant=connection.createStatement();
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dVolume)+"' WHERE ID=1");
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(pVolume)+"' WHERE ID=2");
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(ePrice)+"' WHERE ID=3");
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dieselStock)+"' WHERE ID=14");
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(petrolStock)+"' WHERE ID=15");

            updateInstant.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            SystemLogging.getLogger().fine("Instant Transaction Stored SuccessFully.");

            System.out.println("Transaction Inserted Successfully.");

        } catch(SQLException e) {
            System.out.println(e.getNextException().getMessage());


            retryStatus++;
            System.out.println("Error Inserting Instant Transaction.ReScheduling Task Again. Retry:"+retryStatus);
            TaskManager.scheduleTask(this,4000);

            SystemLogging.getLogger().severe("Couldn't Store The Transaction,Scheduling Again.");

            System.out.println("Error Inserting Transaction.");
        }


    }

}
