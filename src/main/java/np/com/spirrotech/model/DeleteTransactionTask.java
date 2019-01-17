package np.com.spirrotech.model;

import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import np.com.spirrotech.dao.CreditCompanyList;
import np.com.spirrotech.dao.CreditDetailsModel;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.utils.databases.DatabaseHandler;
import np.com.spirrotech.utils.tasks.Tasks;

import javax.activation.DataHandler;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteTransactionTask extends Tasks {

    private ArrayList<CreditDetailsModel> creditDetailsModels;
    private String companyName;

    public DeleteTransactionTask(ArrayList<CreditDetailsModel> creditDetailsModels, String companyName) {
        this.creditDetailsModels = creditDetailsModels;
        this.companyName = companyName;
    }

    @Override
    public void execute() {
        try {

            //create a general delete statement
            PreparedStatement delete = connection.prepareStatement("DELETE FROM " + DatabaseHandler.generateTableName(companyName) + " WHERE ID=?");
            //set the update amount that should be deducted from the company total
            double updateAmount = 0;

            //loop through all the selected transactions
            for (CreditDetailsModel model : creditDetailsModels) {
                //add the price of the transactions
                updateAmount = updateAmount + Double.parseDouble(model.getPrice());
                delete.setInt(1, model.getPrimaryID());
                delete.addBatch();
            }

            delete.executeBatch();

            //query to deduct the amount from the company's total amount
            delete = connection.prepareStatement("UPDATE CreditCompany SET TOTAL = TOTAL - ? WHERE COMPANY_NAME = ?");
            delete.setDouble(1, updateAmount);
            delete.setString(2, DatabaseHandler.generateTableName(companyName));

            delete.execute();


            connection.commit();
            SystemLogging.getLogger().fine("Transactions Deleted For " + companyName);

            //update the current cached model
            DebtListModel model = CreditCompanyList.getCompany(companyName);
            model.setTotal((float) (model.getTotal() - updateAmount));

            //call the listener for task completion
            taskInterface.onComplete(null);

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Transaction Removed!",1000);
            snackBarToastGenerator.show();


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("Credit Transaction Deletion failed.");

            rollBack();

            String message="Error Deleting Company '"+companyName+"' Credit Transaction.\n"+"Please Retry Again.";
            String title="Update Error";
            reportError(title, message, this);
        }
    }
}
