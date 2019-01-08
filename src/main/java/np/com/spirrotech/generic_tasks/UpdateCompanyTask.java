package np.com.spirrotech.generic_tasks;

import javafx.application.Platform;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *Changes the company parameters like name and cycle time.
 */

public class UpdateCompanyTask extends Tasks {

    private DebtListModel model;

    /**
     *
     * @param model the company to be updated
     */

    public UpdateCompanyTask(DebtListModel model) {
        this.model=model;
    }


    @Override
    public void execute() {
        try {


            PreparedStatement update=connection.prepareStatement("UPDATE CreditCompany SET DAY=?,PHONE=? WHERE COMPANY_NAME=?");
            update.setString(1,model.getDays());
            update.setString(2,model.getPhone());
            update.setString(3,model.getCompanyName());

            update.execute();

            update.close();

            connection.commit();
            SystemLogging.getLogger().fine("Company Update done.");

            Platform.runLater(()->SettingsController.getInstance().editModel(model));

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Updated",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("Company Update failed to do.");

            rollBack();

            String message="Error Updating Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            reportError(title, message, this);
        }

    }
}
