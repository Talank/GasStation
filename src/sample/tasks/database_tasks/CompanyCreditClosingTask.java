package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
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

                PreparedStatement deleteCredit=connection.prepareStatement("DELETE FROM "+tableName+" WHERE DATE<?");
                deleteCredit.setDate(1,model.getExpireDate());

                deleteCredit.execute();

                Statement getResults=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                ResultSet totalResult=getResults.executeQuery("SELECT TRANSACTION_ID,PRICE FROM "+tableName);

                int lastTransaction=1,prevID=0,currentID;
                float total=0;

                if(totalResult.next()) {
                    prevID=totalResult.getInt(1);
                    total+=totalResult.getFloat(2);
                    totalResult.updateInt(1,lastTransaction);
                    totalResult.updateRow();
                }

                while(totalResult.next()) {
                    currentID=totalResult.getInt(1);

                    if(prevID!=currentID) {
                        prevID=currentID;
                        lastTransaction++;
                    }

                    total+=totalResult.getFloat(2);
                    totalResult.updateInt(1,lastTransaction);
                    totalResult.updateRow();
                }

                lastTransaction++;

                deleteCredit=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE+TOTAL-?,TOTAL=?,TRANSACTION_ID=?,PREV_TRANSACTION_ID=? WHERE ID=?");
                deleteCredit.setFloat(1,total);
                deleteCredit.setFloat(2,total);
                deleteCredit.setInt(3,lastTransaction);
                deleteCredit.setInt(4,1);
                deleteCredit.setInt(5,model.getId());

                deleteCredit.execute();

                totalResult.close();
                getResults.close();
                deleteCredit.close();

                model.setDue(model.getDue()+(model.getTotal()-total));
                model.setTotal(total);
                model.setTransactionID(lastTransaction);

            } else {

                System.out.println("Model is not Expired");

                Statement delete=connection.createStatement();
                delete.execute("DELETE FROM "+tableName);

                PreparedStatement updateCreditCompany=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE+TOTAL,TOTAL=0.0,TRANSACTION_ID=?,PREV_TRANSACTION_ID=?");
                updateCreditCompany.setInt(1,1);
                updateCreditCompany.setInt(2,1);

                updateCreditCompany.execute();

                delete.close();
                updateCreditCompany.close();

                model.setDue(model.getDue()+model.getTotal());
                model.setTotal(0f);

                model.setTransactionID(1);

            }
            connection.commit();

            model.setExpired(false);


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Closing Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
