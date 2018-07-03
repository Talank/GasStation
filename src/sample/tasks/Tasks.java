package sample.tasks;

import sample.Reference;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This is the super class of all the tasks that are performed by the system.
 * It stores all database connections and other information about the interfaces that are listening for the task completion.
 */

public class Tasks {

    protected static final String DRIVER="org.apache.derby.jdbc.ClientDriver";

    protected static Connection connection;

    protected int retryStatus=0;

    protected TaskInterface taskInterface;

    /**
     * Tries to establish connection with derby database
     * @return true if connection is established
     */

    public static boolean setupConnection() {
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(Reference.JDBC_URL);
            connection.setAutoCommit(false);
            if(connection!=null) {
                System.out.println("Connection Established");
                return true;
            } else
                System.out.println("Error Connecting Database");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @return the current database connection
     */

    public static Connection getConnection() {
        return connection;
    }

    /**
     * Adds the listener for the tasks
     * @param taskInterface the interested interface
     */

    public void addTaskInterfaceListener(TaskInterface taskInterface) {
        this.taskInterface=taskInterface;
    }

    /**
     * @deprecated
     * @return
     */

    public int getRetryStatus() {
        return retryStatus;
    }

    /**
     * implemented by the sub classes
     */

    public void execute() {

    }

}
