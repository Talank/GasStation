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

/**
 * @deprecated
 * Changes username and password
 */

public class ChangeUserParameterTask extends Tasks {

    private String userName,password;


    /**
     *
     * @param userName the new username
     * @param password the new password
     */
    public ChangeUserParameterTask(String userName,String password) {
        this.userName=userName;
        this.password=password;
    }


    /**
     *Changes the current user name and password
     */

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

            rollBack();

            String message="Error Changing User Parameters.\n"+"Please Retry Again.";
            String title="User Parameter Error";
            reportError(title, message, this);

        }

    }
}
