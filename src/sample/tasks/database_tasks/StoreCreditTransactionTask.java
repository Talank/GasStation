package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.Transaction;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import javax.activation.DataHandler;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.List;

public class StoreCreditTransactionTask extends Tasks {

    private List<Transaction> instantTransaction;

    private int[] ids;

    private int taskStatus=0;


    public void setParameters(List<Transaction> instantTransaction,int[] ids) {
        this.ids=ids;
        this.instantTransaction=instantTransaction;
    }

    @Override
    public void execute() {

        float pVolume=0,dVolume=0,ePrice=0;

        try {

            StringBuilder query=new StringBuilder("INSERT INTO "+DatabaseHandler.generateTableName(Transaction.getName(),ids[0])+" VALUES (?,?,?,?,?,?,?)");

            PreparedStatement insertCredit=connection.prepareStatement(query.toString());
            if(taskStatus==0) {
                connection.setAutoCommit(false);
           /* Statement getCompanyID=connection.createStatement();
            ResultSet resultSet=getCompanyID.executeQuery("SELECT ID FROM CreditCompany WHERE COMPANY_NAME='"+Transaction.getName()+"'");
            resultSet.next();
            int id=resultSet.getInt(1);*/

                System.out.println("ID:"+ids[1]);



                System.out.println("Query:"+query);

                Date date=DatabaseHandler.getCurrentDate();
                for(Transaction transaction:instantTransaction) {

                    insertCredit.setInt(1,ids[1]);
                    insertCredit.setString(2,transaction.getType());
                    insertCredit.setFloat(4,transaction.getRate());
                    if(transaction.getMode()==2) {
                        insertCredit.setString(3,transaction.getType());
                        insertCredit.setFloat(5,transaction.getQuantity());
                        ePrice+=transaction.getPrice();
                    } else {

                        if(transaction.getMode()==0) {
                            dVolume+=transaction.getVolume();
                        } else {
                            pVolume+=transaction.getVolume();
                        }

                        if((SystemStatus.getDieselStock()-dVolume)<0||(SystemStatus.getPetrolStock()-pVolume)<0) {
                            System.out.println("The Stock was Negative");
                            insertCredit.cancel();
                            return;
                        }

                        insertCredit.setString(3,transaction.getVehicleNo());
                        insertCredit.setFloat(5,transaction.getVolume());
                    }
                    insertCredit.setFloat(6,transaction.getPrice());
                    insertCredit.setDate(7,date);

                    insertCredit.addBatch();

                }

                insertCredit.executeBatch();
                connection.commit();
                taskStatus++;
            }

            if(taskStatus==1) {
                connection.setAutoCommit(true);

                ids[1]++;
                insertCredit=connection.prepareStatement("UPDATE CreditCompany SET TRANSACTION_ID="+ids[1]+" WHERE ID="+ids[0]);
                insertCredit.execute();

                taskStatus++;
            }

            if(taskStatus==2) {
                float petrolStock=pVolume;
                float dieselStock=dVolume;

                Statement getInstant=connection.createStatement();
                ResultSet resultSet;

                resultSet=getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7 OR ID=14 OR ID=15");


                resultSet.next();
                pVolume+=Float.parseFloat(resultSet.getString(1));
                resultSet.next();
                dVolume+=Float.parseFloat(resultSet.getString(1));
                resultSet.next();
                ePrice+=Float.parseFloat(resultSet.getString(1));
                resultSet.next();
                dieselStock=Float.parseFloat(resultSet.getString(1))-dieselStock;
                resultSet.next();
                petrolStock=Float.parseFloat(resultSet.getString(1))-petrolStock;

                SystemStatus.setfDiesel(dieselStock);
                SystemStatus.setPetrolStock(petrolStock);

                resultSet.close();
                connection.setAutoCommit(false);
                Statement updateInstant=connection.createStatement();
                updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dVolume)+"' WHERE ID=4");
                updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(pVolume)+"' WHERE ID=5");
                updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(ePrice)+"' WHERE ID=6");
                updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(dieselStock)+"' WHERE ID=14");
                updateInstant.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(petrolStock)+"' WHERE ID=15");

                updateInstant.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
                taskStatus++;
            }

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
