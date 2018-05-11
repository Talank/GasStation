package sample.tasks.database_tasks;

import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

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

            connection.setAutoCommit(false);

            Statement updateUser=connection.createStatement();
            updateUser.addBatch("UPDATE SystemStatus SET VALUE='"+userName+"',DATE='"+DatabaseHandler.getCurrentDate()+"' WHERE ID=7");
            updateUser.addBatch("UPDATE SystemStatus SET VALUE='"+password+"',DATE='"+DatabaseHandler.getCurrentDate()+"' WHERE ID=8");

            updateUser.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
