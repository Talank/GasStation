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

/**
 *Closes the company, prints the company bill and removes all the data up to the expired date.
 */

public class CompanyCreditClosingTask extends Tasks {


    private DebtListModel model;

    /**
     * @see DebtListModel
     * @param model the Company model which is to be closed
     */

    public CompanyCreditClosingTask(DebtListModel model) {
        this.model=model;
    }

    /**
     * First it is checked if the company is expired, if it is then all the data up to the expiry period is
     * deleted and parameters are updated in "CreditCompany' table.
     */

    @Override
    public void execute() {
        try {

            String tableName=DatabaseHandler.generateTableName(model.getCompanyName());

            CreditCompanyClosingBill bill=new CreditCompanyClosingBill(model);
            if(bill.printBill()) {
                SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Closed. Printing Bill",1000);
                snackBarToastGenerator.show();

                if(model.isExpired()) {

                    //Delete all the data up to the expiry period, we don't use prev_transactionID as and index to delete the data due to "POSTING" requirement

                    PreparedStatement deleteCredit=connection.prepareStatement("DELETE FROM "+tableName+" WHERE DATE<?");
                    deleteCredit.setDate(1,model.getExpireDate());
                    deleteCredit.execute();

                    //Configure the cursor to be updatable

                    Statement getResults=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                    ResultSet totalResult=getResults.executeQuery("SELECT TRANSACTION_ID,PRICE FROM "+tableName);

                    //new parameters to be applied to the data existing in the table after expiry period
                    //i.e we start from 0 and transactions having same id are given same number(i.e 0) and the number is incremented when the transaction id differs (i.e another transaction)

                    int lastTransaction=1,prevID=0,currentID;
                    float total=0;

                    //read the first data row to initialize and apply to the next while loop

                    if(totalResult.next()) {
                        prevID=totalResult.getInt(1);
                        total+=totalResult.getFloat(2);
                        totalResult.updateInt(1,lastTransaction);//begin from remaining the transaction from 1 and then increment afterward for others transaction not in the same session
                        totalResult.updateRow();
                    }



                    while(totalResult.next()) {

                        //get the current transaction id of the row

                        currentID=totalResult.getInt(1);

                        // if prevID and currentID are equal, then all the transactions are the part of the same session, so we need not update the currentID
                        //Otherwise we update the lastTransaction

                        if(prevID!=currentID) {
                            prevID=currentID;
                            lastTransaction++;
                        }

                        total+=totalResult.getFloat(2);
                        totalResult.updateInt(1,lastTransaction);
                        totalResult.updateRow();
                    }

                    //Update the lastTransaction and store it in Transaction_ID of creditCompany table so, next transactions can start from there

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

                    //update the model, the changes will be reflected through observable list

                    model.setDue(model.getDue()+(model.getTotal()-total));
                    model.setTotal(total);
                    model.setTransactionID(lastTransaction);

                } else {

                    /*
                     * Since, the company is not expired, so we delete all the data in the table, and update credit company and reset the model
                     * */

                    Statement delete=connection.createStatement();
                    delete.execute("DELETE FROM "+tableName);

                    PreparedStatement updateCreditCompany=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE+TOTAL,TOTAL=0.0,TRANSACTION_ID=?,PREV_TRANSACTION_ID=? WHERE company_name=?");
                    updateCreditCompany.setInt(1,1);
                    updateCreditCompany.setInt(2,1);
                    updateCreditCompany.setString(3, model.getCompanyName());

                    updateCreditCompany.execute();

                    delete.close();
                    updateCreditCompany.close();

                    connection.commit();

                    SystemLogging.getLogger().fine("CreditCompany successfully updated ");

                    model.setDue(model.getDue()+model.getTotal());
                    model.setTotal(0f);
                    model.setTransactionID(1);

                }

            } else {
                SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Closed.But Couldn't Print Bill",1000);
                snackBarToastGenerator.show();
            }



        } catch (SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("CreditCompany failed to update");
            System.out.println(e.getMessage());

            rollBack();

            String message="Error Closing Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            reportError(title, message, this);
        }
    }
}
