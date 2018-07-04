package sample.tasks.database_tasks;

import sample.Reference;
import sample.tasks.TaskInterface;
import sample.tasks.Tasks;

import java.sql.DriverManager;

/**
 * @deprecated 
 */

public class SetupDatabaseConnection extends Tasks {


    @Override
    public void execute() {
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(Reference.JDBC_URL);
            if(connection!=null) {
                System.out.println("Connection Established");
            } else {
                System.out.println("Error Connecting Database");
                taskInterface.onError();
            }

        } catch (Exception e) {
            e.printStackTrace();
            taskInterface.onError();
        }
    }
}
