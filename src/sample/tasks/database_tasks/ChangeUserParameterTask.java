package sample.tasks.database_tasks;

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


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }

    }
}
