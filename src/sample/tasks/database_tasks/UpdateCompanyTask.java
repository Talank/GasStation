package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.SystemLogging;
import sample.controller.main_home_controller.SettingsController;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.DebtListModel;
import sample.tasks.Tasks;

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


            PreparedStatement update=connection.prepareStatement("UPDATE CreditCompany SET COMPANY_NAME=?,DAY=? WHERE COMPANY_NAME=?");
            update.setString(1,model.getCompanyName());
            update.setString(2,model.getDays());
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
