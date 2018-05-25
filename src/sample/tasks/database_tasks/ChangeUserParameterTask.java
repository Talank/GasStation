package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
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

            Statement updateUser=connection.createStatement();
            updateUser.addBatch("UPDATE SystemStatus SET VALUE='"+userName+"',DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=7");
            updateUser.addBatch("UPDATE SystemStatus SET VALUE='"+password+"',DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=8");

            updateUser.executeBatch();
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
