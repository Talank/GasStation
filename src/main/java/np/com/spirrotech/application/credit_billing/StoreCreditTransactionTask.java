package np.com.spirrotech.application.credit_billing;

import javafx.application.Platform;
import np.com.spirrotech.application.home.AppHomeController;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.ErrorMessageDialogGenerator;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.model.DebtListModel;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.model.databases.DatabaseHandler;
import np.com.spirrotech.model.transaction_model.Transaction;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.*;
import java.util.List;

/**
 *Stores the credit transaction into the specified database tables.
 * The stock is checked in every time, and if the stock is -ve the current task is aborted and the user is notified.
 */

public class StoreCreditTransactionTask extends Tasks {

    private List<Transaction> instantTransaction;

    private DebtListModel model;

    private boolean isAdmin;

    private Date date;

    /**
     *
     * @param instantTransaction the list of transaction to be stored in the same transaction session
     * @param model the model of the compnany
     */

    public StoreCreditTransactionTask(List<Transaction> instantTransaction, DebtListModel model) {
        this.instantTransaction = instantTransaction;
        this.model=model;
    }

    /**
     *
     * @param isAdmin true if Admin mode is on
     * @param date the date to be set
     */

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
                    total+=transaction.getPrice();

                } else {

                    if(transaction.getMode()==0) {
                        dVolume+=transaction.getVolume();
                    } else {
                        pVolume+=transaction.getVolume();
                    }

                    if((SystemStatus.getDieselStock()-dVolume)<0||(SystemStatus.getPetrolStock()-pVolume)<0) {
                        System.out.println("The Stock was Negative");

                        String message="The Stock Value is Negative. Please Adjust the Stock and try again.";
                        String title="Stock Error";
                        ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
                        generator.setTasks(this);
                        generator.showDialog();

                        storeCredit.clearParameters();
                        storeCredit.clearBatch();
                        storeCredit.close();

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

            rollBack();

            String message="Error Storing Transaction For Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Transaction Error";
            reportError(title, message, this);

            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }


    }
}
