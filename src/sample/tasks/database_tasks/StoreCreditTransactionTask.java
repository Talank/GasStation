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

    public StoreCreditTransactionTask(List<Transaction> instantTransaction, DebtListModel model) {
        this.instantTransaction = instantTransaction;
        this.model=model;
    }

    @Override
    public void execute() {

        float pVolume=0,dVolume=0,ePrice=0;

        int ids[]=new int[]{model.getId(),model.getTransactionID()};

        try {

            float total=0;

            String tableName=DatabaseHandler.generateTableName(Transaction.getName(),ids[0]);

            StringBuilder query=new StringBuilder("INSERT INTO "+DatabaseHandler.generateTableName(Transaction.getName(),ids[0])+" VALUES (?,?,?,?,?,?,?)");

            /*PreparedStatement insertCredit=connection.prepareStatement(query.toString());*/




            connection.setAutoCommit(false);
            Statement storeCredit=connection.createStatement();

            System.out.println("ID:"+ids[1]);

            System.out.println("Query:"+query);

            for(Transaction transaction:instantTransaction) {


               /* insertCredit.setInt(1,ids[1]);
                insertCredit.setString(2,transaction.getType());
                insertCredit.setFloat(4,transaction.getRate());*/
                if(transaction.getMode()==2) {

                    storeCredit.addBatch("INSERT INTO "+tableName+" VALUES("+ids[1]+",'"+transaction.getType()+"','"+transaction.getType()+"',"+transaction.getRate()+","+transaction.getQuantity()+","+transaction.getPrice()+",'"+SystemStatus.getTransactionDate().toString()+"')");

                    /*insertCredit.setString(3,transaction.getType());
                    insertCredit.setFloat(5,transaction.getQuantity());*/
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

                        /*insertCredit.cancel();*/
                        return;
                    }

                    total+=transaction.getPrice();


                    storeCredit.addBatch("INSERT INTO "+tableName+" VALUES("+ids[1]+",'"+transaction.getType()+"','"+transaction.getVehicleNo()+"',"+transaction.getRate()+","+transaction.getVolume()+","+transaction.getPrice()+",'"+SystemStatus.getTransactionDate().toString()+"')");

                    /*insertCredit.setString(3,transaction.getVehicleNo());
                    insertCredit.setFloat(5,transaction.getVolume());*/
                }
                /*insertCredit.setFloat(6,transaction.getPrice());
                insertCredit.setDate(7,SystemStatus.getTransactionDate());*/

                /*insertCredit.addBatch();*/

            }

            /*insertCredit.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);*/

            ids[1]++;

            storeCredit.addBatch("UPDATE CreditCompany SET TRANSACTION_ID="+ids[1]+",TOTAL=TOTAL+"+total+" WHERE ID="+ids[0]);

            /*insertCredit=connection.prepareStatement("UPDATE CreditCompany SET TRANSACTION_ID="+ids[1]+",TOTAL=TOTAL+"+total+" WHERE ID="+ids[0]);
            insertCredit.execute();*/

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
            /*connection.setAutoCommit(false);
            Statement updateInstant=connection.createStatement();*/

            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dVolume)+"' WHERE ID=4");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(pVolume)+"' WHERE ID=5");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(ePrice)+"' WHERE ID=6");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dieselStock)+"' WHERE ID=14");
            storeCredit.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(petrolStock)+"' WHERE ID=15");

            storeCredit.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);

            /*updateInstant.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);*/



            SystemLogging.getLogger().fine("Credit Transaction Stored in Database");

        }  catch(SQLException e) {
            System.out.println(e.getMessage());

            retryStatus++;
            System.out.println("Error Inserting Credit Transaction.ReScheduling Task Again. Retry:"+retryStatus);

            TaskManager.scheduleTask(this,5000);
            System.out.println("Scheduling Task Again.");
            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }


    }
}
