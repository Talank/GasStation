package sample.tasks;

import java.sql.Connection;
import java.sql.DriverManager;

public class Tasks {

    protected static final String DRIVER="org.apache.derby.jdbc.ClientDriver";
    protected static final String JDBC_URL="jdbc:derby://localhost:1527/D:/InelliJIDEA/GasStation/ohmoilpump;create=false";

    protected static Connection connection;

    protected TaskInterface taskInterface;

    public static boolean setupConnection() {
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(JDBC_URL);
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

    public void execute() {

    }

}
