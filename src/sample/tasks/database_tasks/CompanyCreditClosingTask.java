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

                delete.execute("DELETE FROM "+tableName+" WHERE DATE<='"+model.getExpireDate().toString()+"'");

                Statement getResults=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                ResultSet totalResult=getResults.executeQuery("SELECT TRANSACTION_ID,PRICE FROM "+tableName);

                int lastTransaction=0;
                float total=0;
                while(totalResult.next()) {
                    lastTransaction=(totalResult.getInt(1)-model.getPrevTransactionID());
                    total+=totalResult.getFloat(2);
                    totalResult.updateInt(1,lastTransaction);
                    totalResult.updateRow();

/*                    delete.addBatch("UPDATE "+tableName+" SET EXPIRE_ID="+lastExpire+", TRANSACTION_ID="+lastTransaction+" WHERE EXPIRE_ID="+totalResult.getInt(1));*/
                }

                lastTransaction++;

                delete=connection.createStatement();

                model.setDue(model.getDue()+model.getTotal());
                model.setTotal(total);
                model.setTransactionID(lastTransaction);

                delete.execute("UPDATE CreditCompany SET DUE=DUE+TOTAL,TOTAL="+total+",TRANSACTION_ID="+lastTransaction+",PREV_TRANSACTION_ID="+1+",DATE='"+SystemStatus.getCurrentDate().toString()+"'");


            } else {

                System.out.println("Model is not Expired");

                Statement delete=connection.createStatement();
                delete.addBatch("DELETE FROM "+tableName);
                delete.addBatch("UPDATE CreditCompany SET DUE=DUE+TOTAL,TOTAL=0.0,TRANSACTION_ID="+1+",PREV_TRANSACTION_ID="+1+",DATE='"+SystemStatus.getCurrentDate().toString()+"'");
                delete.executeBatch();

                model.setDue(model.getDue()+model.getTotal());
                model.setTotal(0f);

                model.setTransactionID(1);
                /*model.setExpireID(1);*/

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
