package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.AdminUser;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeUserParameterTask extends Tasks {

    private String userName,password;

    public ChangeUserParameterTask(String userName,String password) {
        this.userName=userName;
        this.password=password;
    }


    @Override
    public void execute() {
        try {

            PreparedStatement updateUser=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");

            updateUser.setString(1,userName);
            updateUser.setDate(2,SystemStatus.getCurrentDate());
            updateUser.setInt(3,7);
            updateUser.addBatch();

            updateUser.setString(1,(new AdminUser()).generatePasswordHash(password));
            updateUser.setDate(2,SystemStatus.getCurrentDate());
            updateUser.setInt(3,8);
            updateUser.addBatch();

            updateUser.executeBatch();

            updateUser.close();

            connection.commit();
            SystemLogging.getLogger().fine("SystemStatus change in user parameters done");
            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("User Parameter Saved",1000);
            snackBarToastGenerator.show();


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
            SystemLogging.getLogger().warning("SystemStatus failed to change in user parameters");
            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback SystemStatus to stable state in user parameters");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning("SystemStatus failed to rollback in user parameters");
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Changing User Parameters.\n"+"Please Retry Again.";
            String title="User Parameter Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();

        }

    }
}
