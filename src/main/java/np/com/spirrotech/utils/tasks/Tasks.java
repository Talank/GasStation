package np.com.spirrotech.utils.tasks;

import np.com.spirrotech.config.Reference;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.ErrorMessageDialogGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This is the super class of all the tasks that are performed by the system.
 * It stores all database connections and other information about the interfaces that are listening for the task completion.
 */

public class Tasks {

    protected static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";

    protected static Connection connection;

    protected int retryStatus=0;

    private boolean cancelled = false;

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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    protected  void updateProgress(double current, double total) {
        taskInterface.updateProgress(current, total);
    }

    protected void reportError(String title, String message, Tasks tasks) {
        ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
        generator.setTasks(this);
        generator.showDialog();
    }

    protected void rollBack() {
        try {
            connection.rollback();
            SystemLogging.getLogger().fine("Rollback to stable stage in SystemStatus");
            System.out.println("Error in Database detected. Rolling back Database to stable stage.");

        } catch (SQLException e1) {
            SystemLogging.getLogger().warning("Rollback failed in SystemStatus");
            System.out.println("Unable to undo Changes.It restores when system restarts.");
        }
    }


}
