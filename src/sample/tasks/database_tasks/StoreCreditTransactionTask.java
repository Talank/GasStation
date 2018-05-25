package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.Transaction;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import javax.activation.DataHandler;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.List;

public class StoreCreditTransactionTask extends Tasks {

    private List<Transaction> instantTransaction;

    private DebtListModel model;

    private boolean isAdmin;

    private Date date;

    public StoreCreditTransactionTask(List<Transaction> instantTransaction, DebtListModel model) {
        this.instantTransaction = instantTransaction;
        this.model=model;
    }

    public void setAdminDate(boolean isAdmin,Date date) {
        this.isAdmin=isAdmin;
        this.date=date;
    }

    @Override
    public void execute() {

        float pVolume=0,dVolume=0,ePrice=0;

        int ids[]=new int[]{model.getId(),model.getTransactionID()};

        try {

            float total=0;

            String tableName=DatabaseHandler.generateTableName(Transaction.getName(),ids[0]);
            String transactionDate;
            if(isAdmin) {
                transactionDate=date.toString();
            } else {
                transactionDate=SystemStatus.getCurrentDate().toString();
            }

            Statement storeCredit=connection.createStatement();

            System.out.println("ID:"+ids[1]);

            for(Transaction transaction:instantTransaction) {

                if(transaction.getMode()==2) {

                    storeCredit.addBatch("INSERT INTO "+tableName+" VALUES("+ids[1]+",'"+transaction.getType()+"','"+transaction.getType()+"',"+transaction.getRate()+","+transaction.getQuantity()+","+transaction.getPrice()+",'"+transactionDate+"')");

                    ePrice+=transaction.getPrice();
                } else {

                    if(transaction.getMode()==0) {
                        dVolume+=transaction.getVolume();
                    } else {
                        pVolume+=transaction.getVolume();
                    }

                    if((SystemStatus.getDieselStock()-dVolume)<0||(SystemStatus.getPetrolStock()-pVolume)<0) {
                        System.out.println("The Stock was Negative");

                        storeCredit.cancel();

                        return;
                    }

                    total+=transaction.getPrice();


                    storeCredit.addBatch("INSERT INTO "+tableName+" VALUES("+ids[1]+",'"+transaction.getType()+"','"+transaction.getVehicleNo()+"',"+transaction.getRate()+","+transaction.getVolume()+","+transaction.getPrice()+",'"+transactionDate+"')");

                }


            }


            ids[1]++;

            storeCredit.addBatch("UPDATE CreditCompany SET TRANSACTION_ID="+ids[1]+",TOTAL=TOTAL+"+total+" WHERE ID="+ids[0]);


            model.setTotal(model.getTotal()+total);
            float petrolStock=pVolume;
            float dieselStock=dVolume;

            Statement getInstant=connection.createStatement();
            ResultSet resultSet;

            resultSet=getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7 OR ID=14 OR ID=15");


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


            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dVolume)+"' WHERE ID=4");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(pVolume)+"' WHERE ID=5");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(ePrice)+"' WHERE ID=6");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dieselStock)+"' WHERE ID=14");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(petrolStock)+"' WHERE ID=15");

            storeCredit.executeBatch();
            connection.commit();

            SystemLogging.getLogger().fine("Credit Transaction Stored in Database");

        }  catch(SQLException e) {
            System.out.println(e.getMessage());

            retryStatus++;
            System.out.println("Error Inserting Credit Transaction.ReScheduling Task Again. Retry:"+retryStatus);

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            TaskManager.scheduleTask(this,5000);
            System.out.println("Scheduling Task Again.");
            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }


    }
}
