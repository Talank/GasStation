package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
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
import java.time.LocalDate;
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


        try {

            float total=0;

            String tableName=DatabaseHandler.generateTableName(Transaction.getName());
            Date transactionDate;
            if(isAdmin) {
                transactionDate=date;
            } else {
                transactionDate=SystemStatus.getCurrentDate();
            }

            PreparedStatement storeCredit=connection.prepareStatement("INSERT INTO "+tableName+" VALUES(?,?,?,?,?,?,?)");

            for(Transaction transaction:instantTransaction) {

                storeCredit.setInt(1,model.getTransactionID());
                storeCredit.setString(2,transaction.getType());
                storeCredit.setFloat(4,transaction.getRate());
                storeCredit.setFloat(6,transaction.getPrice());
                storeCredit.setDate(7,transactionDate);

                if(transaction.getMode()==2) {

                    storeCredit.setString(3,transaction.getType());
                    storeCredit.setFloat(5,transaction.getQuantity());

                    ePrice+=transaction.getPrice();
                    total+=ePrice;

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

                    storeCredit.setString(3,transaction.getVehicleNo());
                    storeCredit.setFloat(5,transaction.getVolume());

                }
                storeCredit.addBatch();


            }

            storeCredit.executeBatch();

            storeCredit=connection.prepareStatement("UPDATE CreditCompany SET TRANSACTION_ID=?,TOTAL=TOTAL+? WHERE COMPANY_NAME=?");
            storeCredit.setInt(1,model.getTransactionID()+1);
            storeCredit.setFloat(2,total);
            storeCredit.setString(3,model.getCompanyName());

            storeCredit.execute();

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

            storeCredit=connection.prepareStatement("UPDATE SystemStatus SET VALUE=? WHERE ID=?");
            storeCredit.setString(1,String.valueOf(dVolume));
            storeCredit.setInt(2,4);
            storeCredit.addBatch();

            storeCredit.setString(1,String.valueOf(pVolume));
            storeCredit.setInt(2,5);
            storeCredit.addBatch();

            storeCredit.setString(1,String.valueOf(ePrice));
            storeCredit.setInt(2,6);
            storeCredit.addBatch();

            storeCredit.setString(1,String.valueOf(dieselStock));
            storeCredit.setInt(2,14);
            storeCredit.addBatch();

            storeCredit.setString(1,String.valueOf(petrolStock));
            storeCredit.setInt(2,15);
            storeCredit.addBatch();

            storeCredit.executeBatch();

            resultSet.close();

            storeCredit.close();

            getInstant.close();

            connection.commit();

            model.setTransactionID(model.getTransactionID()+1);
            model.setTotal(model.getTotal()+total);

            SystemStatus.setDieselStock(dieselStock);
            SystemStatus.setPetrolStock(petrolStock);

            Platform.runLater(()-> AppHomeController.getInstance().setStock());

            SystemLogging.getLogger().fine("Credit Transaction Stored in Database");

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Credit Transaction Added",1000);
            snackBarToastGenerator.show();

        }  catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning(" Credit Transaction failed to store in db");
            System.out.println("Error Inserting Credit Transaction.ReScheduling Task Again. Retry:"+retryStatus);

            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback Credit Transaction storing to stable state");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning(" Failed to rollback CreditTransaction storing");
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Storing Transaction For Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Transaction Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();

            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }


    }
}
