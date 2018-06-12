package sample.model.databases;

import sample.controller.SystemLogging;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

    private static final String JDBC_SHUTDOWN="jdbc:derby:;shutdown=true";

    public static String generateTableName(String name) {

        String tableName=name.replace(' ','_').toLowerCase();
        System.out.println("Table Name:"+tableName);
        return tableName;
    }

    public static void closeConnection() {
        try {
            DriverManager.getConnection(JDBC_SHUTDOWN);
        } catch (SQLException e) {
            if(e.getSQLState().equals("XJ015"))
                SystemLogging.getLogger().fine("Derby System Closed Successfully.");
            else
                SystemLogging.getLogger().warning("Failed to ShutDown Derby System. May result in uneven id due to caching!");
        }
    }

}
