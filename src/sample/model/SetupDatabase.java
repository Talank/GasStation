package sample.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SetupDatabase {
    private static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL="jdbc:derby:ohmoilpump;create=true";
    private static Connection connection;

    private static final String TAG="SetupDataBase";


    public static boolean setupDatabase() {
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(JDBC_URL);
            if(connection!=null) {
                System.out.println("Connection Established");
                if(createTable())
                    System.out.println("Tables Created");
                else
                    System.out.println("Error Creating Tables");
            } else
                System.out.println("Error Connecting Database");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static boolean createTable() {
        try {
            Statement createTable=connection.createStatement();
            createTable.execute("CREATE TABLE MeterReading (IDiesel FLOAT NOT NULL,IPetrol FLOAT NOT NULL,FDiesel FLOAT NOT NULL,FPetrol FLOAT NOT NULL,Date DATE NOT NULL)");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
