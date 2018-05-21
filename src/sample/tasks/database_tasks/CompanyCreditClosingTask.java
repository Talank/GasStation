package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CompanyCreditClosingTask extends Tasks {


    private DebtListModel model;

    public CompanyCreditClosingTask(DebtListModel model) {
        this.model=model;
    }

    @Override
    public void execute() {
        try {

            String tableName=DatabaseHandler.generateTableName(model.getCompanyName(),model.getId());
            if(model.isExpired()) {

                System.out.println("Model is Expired");

                Statement delete=connection.createStatement();

                delete.execute("DELETE FROM "+tableName+" WHERE EXPIRE_ID<="+model.getPrevExpireID());



                connection.setAutoCommit(false);

                delete=connection.createStatement();

                Statement getResults=connection.createStatement();
                ResultSet totalResult=getResults.executeQuery("SELECT EXPIRE_ID,TRANSACTION_ID FROM "+tableName);

                int lastExpire=0,lastTransaction=0;
                while(totalResult.next()) {

                    lastExpire=(totalResult.getInt(1)-model.getPrevExpireID());
                    lastTransaction=(totalResult.getInt(2)-model.getPrevTransactionID());

                    delete.addBatch("UPDATE "+tableName+" SET EXPIRE_ID="+lastExpire+", TRANSACTION_ID="+lastTransaction+" WHERE EXPIRE_ID="+totalResult.getInt(1));
                }

                lastExpire++;
                lastTransaction++;

                delete.executeBatch();

                delete=connection.createStatement();

                delete.execute("UPDATE CreditCompany SET TRANSACTION_ID="+lastTransaction+",EXPIRE_ID="+lastExpire+",PREV_EXPIRE_ID="+1+",PREV_TRANSACTION_ID="+1+",DATE='"+SystemStatus.getCurrentDate()+"'");


            } else {

                connection.setAutoCommit(false);

                System.out.println("Model is not Expired");

                Statement delete=connection.createStatement();
                delete.addBatch("DELETE FROM "+tableName);
                delete.addBatch("UPDATE CreditCompany SET TRANSACTION_ID="+1+",EXPIRE_ID="+1+",PREV_EXPIRE_ID="+1+",PREV_TRANSACTION_ID="+1+",DATE='"+SystemStatus.getCurrentDate()+"'");
                delete.executeBatch();

                model.setTransactionID(1);
                model.setExpireID(1);

            }

            connection.commit();
            connection.setAutoCommit(true);

            model.setExpired(false);


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
