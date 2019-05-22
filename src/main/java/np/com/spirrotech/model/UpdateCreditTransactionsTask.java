package np.com.spirrotech.model;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.dao.CreditCompanyList;
import np.com.spirrotech.dao.CreditDetailsModel;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.utils.databases.DatabaseHandler;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCreditTransactionsTask extends Tasks {

    private CreditDetailsModel newModel, oldModel;
    private String companyName;
    private int status;

    public UpdateCreditTransactionsTask(CreditDetailsModel oldModel, CreditDetailsModel newModel, String companyName, int status) {
        this.newModel = newModel;
        this.oldModel = oldModel;
        this.companyName = companyName;
        this.status = status;
    }

    @Override
    public void execute() {
        try {


            //update new model using old parameters as compound key
            PreparedStatement update;

            switch (status) {
                //this is case when either rate or volume is changed, so we need to modify the company's total amount
                case 0:
                    update =connection.prepareStatement("UPDATE " + DatabaseHandler.generateTableName(companyName) + " SET RATE=?, VOLUME=?, PRICE=?, DATE=? WHERE ID=?");

                    update.setFloat(1, Float.parseFloat(newModel.getRate()));
                    update.setFloat(2, Float.parseFloat(newModel.getVolume()));
                    update.setFloat(3, Float.parseFloat(newModel.getPrice()));
                    update.setDate(4, newModel.getSqlDate());
                    update.setInt(5, newModel.getPrimaryID());

                    update.execute();
                    //update the new total in the credit company
                    float netUpdateTotal = (Float.parseFloat(newModel.getPrice()) - Float.parseFloat(oldModel.getPrice()));
                    update = connection.prepareStatement("UPDATE CreditCompany SET TOTAL = TOTAL + ? WHERE COMPANY_NAME = ?");
                    update.setFloat(1, netUpdateTotal);
                    update.setString(2, companyName);

                    update.execute();

                    update.close();

                    DebtListModel model = CreditCompanyList.getCompany(companyName);
                    model.setTotal(model.getTotal() + netUpdateTotal);
                    break;

                    //this is the case when we need to update the date of the company
                case 1:
                    //this query changes the transaction date of the entire entries in the transactions
                    update =connection.prepareStatement("UPDATE " + DatabaseHandler.generateTableName(companyName) + " SET DATE=? WHERE TRANSACTION_ID=?");

                    update.setDate(1, newModel.getSqlDate());
                    update.setInt(2, newModel.getTransactionID());

                    update.execute();
                    update.close();

                    break;
                    //this is the normal case
                case 2:
                    update = connection.prepareStatement("UPDATE " + DatabaseHandler.generateTableName(companyName) + " SET TYPE=?,VEHICLE_NO=? WHERE ID=?");

                    update.setString(1, newModel.getItemName());
                    update.setString(2, newModel.getVehicleNo());
                    update.setInt(3, newModel.getPrimaryID());
                    System.out.println("VehicleName"+newModel.getVehicleNo());
                    System.out.println("Primaryid"+newModel.getPrimaryID());

                    update.execute();
                    break;

                    //case 3 is for changing itemName, rate and price
                case 3:
                    update = connection.prepareStatement("UPDATE " + DatabaseHandler.generateTableName(companyName) + " SET TYPE=?,RATE=?,PRICE=? WHERE ID=?");
                    update.setString(1, newModel.getItemName());
                    update.setFloat(2, Float.parseFloat(newModel.getRate()));
                    update.setFloat(3, Float.parseFloat(newModel.getPrice()));
                    update.setInt(4, newModel.getPrimaryID());

                    update.execute();
                    break;
            }






            connection.commit();
            SystemLogging.getLogger().fine("Credit Transaction Updated For " + companyName);


            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Transaction SuccessFully Updated",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("Credit Transaction Update failed to do.");

            rollBack();

            String message="Error Updating Company '"+companyName+"' Credit Transaction.\n"+"Please Retry Again.";
            String title="Update Error";
            reportError(title, message, this);
        }

    }
}
