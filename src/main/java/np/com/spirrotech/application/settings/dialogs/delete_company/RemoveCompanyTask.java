package np.com.spirrotech.application.settings.dialogs.delete_company;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.model.DebtListModel;
import np.com.spirrotech.model.databases.DatabaseHandler;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *Removes the company from the system. All the data associated with the company is removed, including the database tables.
 */

public class RemoveCompanyTask extends Tasks {


    private DebtListModel model;

    public RemoveCompanyTask(DebtListModel model) {
        this.model=model;
    }

    @Override
    public void execute() {
        try {
            PreparedStatement delete=connection.prepareStatement("DELETE FROM CreditCompany WHERE COMPANY_NAME='"+model.getCompanyName()+"'");
            delete.execute();
            delete=connection.prepareStatement("DROP TABLE "+DatabaseHandler.generateTableName(model.getCompanyName()));
            delete.execute();

            delete.close();

            connection.commit();
            SystemLogging.getLogger().fine("CreditCompany removal done ");

            SettingsController.getInstance().removeModel(model);

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Removed SuccessFully",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("CreditCompany removal couldn't be done ");
            System.out.println(e.getMessage());

            rollBack();

            String message="Error Removing Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            reportError(title, message, this);
        }
    }
}
