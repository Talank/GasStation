package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.CreditCompanyClosingBill;
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

            String tableName=DatabaseHandler.generateTableName(model.getCompanyName());
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

                deleteCredit=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE+TOTAL-?,TOTAL=?,TRANSACTION_ID=?,PREV_TRANSACTION_ID=? WHERE COMPANY_NAME=?");
                deleteCredit.setFloat(1,total);
                deleteCredit.setFloat(2,total);
                deleteCredit.setInt(3,lastTransaction);
                deleteCredit.setInt(4,1);
                deleteCredit.setString(5,model.getCompanyName());

                deleteCredit.execute();

                totalResult.close();
                getResults.close();
                deleteCredit.close();

                connection.commit();
                SystemLogging.getLogger().fine("CreditCompany successfully updated ");

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

                connection.commit();
                SystemLogging.getLogger().fine("CreditCompany successfully updated ");

                model.setDue(model.getDue()+model.getTotal());
                model.setTotal(0f);

                model.setTransactionID(1);

            }

            CreditCompanyClosingBill bill=new CreditCompanyClosingBill(model);
            if(bill.printBill()) {
                SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Closed. Printing Bill",1000);
                snackBarToastGenerator.show();
            } else {
                SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Closed.But Couldn't Print Bill",1000);
                snackBarToastGenerator.show();
            }

            if(taskInterface!=null) {
                taskInterface.onComplete(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("CreditCompany failed to update");
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback CreditCompany");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning("failed to rollback CreditCompany");
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
