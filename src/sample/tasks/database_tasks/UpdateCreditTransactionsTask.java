package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.SystemLogging;
import sample.controller.main_home_controller.SettingsController;
import sample.library.SnackBarToastGenerator;
import sample.model.CreditCompanyList;
import sample.model.CreditDetailsModel;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateCreditTransactionsTask extends Tasks {

    private CreditDetailsModel newModel, oldModel;
    private String companyName;
    private boolean priceIncluded;

    public UpdateCreditTransactionsTask(CreditDetailsModel oldModel, CreditDetailsModel newModel, String companyName, boolean priceIncluded) {
        this.newModel = newModel;
        this.oldModel = oldModel;
        this.companyName = companyName;
        this.priceIncluded = priceIncluded;
    }

    @Override
    public void execute() {
        try {


            //update new model using old parameters as compound key
            PreparedStatement update=connection.prepareStatement("UPDATE " + DatabaseHandler.generateTableName(companyName) + " SET TYPE=?,VEHICLE_NO=?, RATE=?, VOLUME=?, PRICE=? WHERE TRANSACTION_ID=? AND TYPE=? AND RATE=? AND VOLUME=? AND PRICE=?");

            update.setString(1, newModel.getItemName());
            update.setString(2, newModel.getVehicleNo());
            update.setFloat(3, Float.parseFloat(newModel.getRate()));
            update.setFloat(4, Float.parseFloat(newModel.getVolume()));
            update.setFloat(5, Float.parseFloat(newModel.getPrice()));
            update.setInt(6, newModel.getTransactionID());
            update.setString(7, oldModel.getItemName());
            update.setFloat(8, Float.parseFloat(oldModel.getRate()));
            update.setFloat(9, Float.parseFloat(oldModel.getVolume()));
            update.setFloat(10, Float.parseFloat(oldModel.getPrice()));

            update.execute();

            //check if the price is actually changed or not, as we are casting float from string, it may result in slight amount variation even if no update to price was made
            if (priceIncluded) {
                //update the new total in the credit company
                float netUpdateTotal = (Float.parseFloat(newModel.getPrice()) - Float.parseFloat(oldModel.getPrice()));
                update = connection.prepareStatement("UPDATE CreditCompany SET TOTAL = TOTAL + ? WHERE COMPANY_NAME = ?");
                update.setFloat(1, netUpdateTotal);
                update.setString(2, companyName);

                update.execute();

                DebtListModel model = CreditCompanyList.getCompany(companyName);
                model.setTotal(model.getTotal() + netUpdateTotal);

            }



            update.close();


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
