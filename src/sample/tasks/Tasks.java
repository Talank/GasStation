package sample.tasks;

import sample.Reference;

import java.sql.Connection;
import java.sql.DriverManager;

public class Tasks {

    protected static final String DRIVER="org.apache.derby.jdbc.ClientDriver";

    protected static Connection connection;

    protected int retryStatus=0;

    protected TaskInterface taskInterface;

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

    public static Connection getConnection() {
        return connection;
    }

    public void addTaskInterfaceListener(TaskInterface taskInterface) {
        this.taskInterface=taskInterface;
    }

    public int getRetryStatus() {
        return retryStatus;
    }

    public void execute() {

    }

}
