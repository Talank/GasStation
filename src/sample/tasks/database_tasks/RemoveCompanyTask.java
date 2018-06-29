package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.controller.main_home_controller.SettingsController;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback CreditCompany removal ");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning("Rollback CreditCompany removal couldn't be done");
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Removing Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
