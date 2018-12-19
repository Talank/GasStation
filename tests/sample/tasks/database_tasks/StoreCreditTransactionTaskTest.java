package sample.tasks.database_tasks;

import org.junit.Test;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.Transaction;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class StoreCreditTransactionTaskTest {


    @Test
    public void loadData() {

        Transaction transaction;
        ArrayList<Transaction> transactions;
        StoreCreditTransactionTask task;

        DebtListModel model = new DebtListModel("ABC", "0", 1, 0, 0, 0,"9444111");
        LocalDate localDate;

        Tasks.setupConnection();
        SystemStatus.setCurrentDate(Date.valueOf(LocalDate.of(2018, 9, 1)));
        localDate = LocalDate.of(2018, 9, 1);

        Transaction.setName(model.getCompanyName());

        int j, k, l, m;

        for (int i = 0; i < 1; i++) {
            j = (int) (Math.random() * 1 + 4);//select the no of rows to be added to the company table
            for (k = 0; k < j; k++) {
                l = (int) (Math.random() * 1 + 5);//set the no of transaction within the Transaction
                transactions = new ArrayList<>();
                for (m = 0; m < l; m++) {
                    transaction = new Transaction();
                    if(m%2 == 0) {
                        transaction.extraTransaction("Castrol" + (int) (Math.random() * 1000 + 1), (float) (Math.random() * 1000 + 10), (int) (Math.random() * 1000 + 1));
                        transaction.calcExtraPrice();
                        Transaction.setTotal(Transaction.getTotal() + transaction.getPrice());
                    } else {
                        int mode = (int) (Math.random() + 0.5);
                        String type = (mode == 0 ? "Diesel" : "Petrol");
                        float volume = (float) (Math.random() * 100 + 1);
                        transaction.oilTransaction(type, "5454", volume, ((mode == 0 ? (volume * 100f) : (volume * 200f))), mode);
                        transaction.setRate((mode == 0 ? 100f : 200f));
                        Transaction.setTotal(Transaction.getTotal() + transaction.getPrice());
                    }
                    transactions.add(transaction);
                }

                SystemStatus.setCurrentDate(Date.valueOf(localDate));

                float pVolume = 0, dVolume = 0, ePrice = 0;


                try {

                    float total = 0;

                    String tableName = DatabaseHandler.generateTableName(Transaction.getName());
                    Date transactionDate;
                    transactionDate = SystemStatus.getCurrentDate();

                    PreparedStatement storeCredit = Tasks.getConnection().prepareStatement("INSERT INTO " + tableName + " VALUES(?,?,?,?,?,?,?)");

                    for (Transaction transactionses : transactions) {

                        storeCredit.setInt(1, model.getTransactionID());
                        storeCredit.setString(2, transactionses.getType());
                        storeCredit.setFloat(4, transactionses.getRate());
                        storeCredit.setFloat(6, transactionses.getPrice());
                        storeCredit.setDate(7, transactionDate);

                        if (transactionses.getMode() == 2) {

                            storeCredit.setString(3, transactionses.getType());
                            storeCredit.setFloat(5, transactionses.getQuantity());

                            ePrice += transactionses.getPrice();
                            total += transactionses.getPrice();

                        } else {

                            if (transactionses.getMode() == 0) {
                                dVolume += transactionses.getVolume();
                            } else {
                                pVolume += transactionses.getVolume();
                            }

                            total += transactionses.getPrice();

                            storeCredit.setString(3, transactionses.getVehicleNo());
                            storeCredit.setFloat(5, transactionses.getVolume());

                        }
                        storeCredit.addBatch();


                    }

                    storeCredit.executeBatch();

                    storeCredit = Tasks.getConnection().prepareStatement("UPDATE CreditCompany SET TRANSACTION_ID=?,TOTAL=TOTAL+? WHERE COMPANY_NAME=?");
                    storeCredit.setInt(1, model.getTransactionID() + 1);
                    storeCredit.setFloat(2, total);
                    storeCredit.setString(3, model.getCompanyName());

                    storeCredit.execute();
/*
                    float petrolStock = pVolume;
                    float dieselStock = dVolume;

                    Statement getInstant = Tasks.getConnection().createStatement();
                    ResultSet resultSet;

                    resultSet = getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>3 AND ID<7 OR ID=14 OR ID=15");

                    resultSet.next();
                    dVolume += Float.parseFloat(resultSet.getString(1));
                    resultSet.next();
                    pVolume += Float.parseFloat(resultSet.getString(1));
                    resultSet.next();
                    ePrice += Float.parseFloat(resultSet.getString(1));
                    resultSet.next();
                    dieselStock = Float.parseFloat(resultSet.getString(1)) - dieselStock;
                    resultSet.next();
                    petrolStock = Float.parseFloat(resultSet.getString(1)) - petrolStock;

                    storeCredit = Tasks.getConnection().prepareStatement("UPDATE SystemStatus SET VALUE=? WHERE ID=?");
                    storeCredit.setString(1, String.valueOf(dVolume));
                    storeCredit.setInt(2, 4);
                    storeCredit.addBatch();

                    storeCredit.setString(1, String.valueOf(pVolume));
                    storeCredit.setInt(2, 5);
                    storeCredit.addBatch();

                    storeCredit.setString(1, String.valueOf(ePrice));
                    storeCredit.setInt(2, 6);
                    storeCredit.addBatch();

                    storeCredit.setString(1, String.valueOf(dieselStock));
                    storeCredit.setInt(2, 14);
                    storeCredit.addBatch();

                    storeCredit.setString(1, String.valueOf(petrolStock));
                    storeCredit.setInt(2, 15);
                    storeCredit.addBatch();

                    storeCredit.executeBatch();*/

        //            resultSet.close();

                    storeCredit.close();

       //             getInstant.close();

                    Tasks.getConnection().commit();

                    model.setTransactionID(model.getTransactionID() + 1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
            localDate = localDate.plusDays(1);

        }

    }
}