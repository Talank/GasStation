package sample.model;

import java.sql.*;

public class DatabaseHandler {
    private static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL="jdbc:derby:ohmoilpump;create=false";
    private static Connection connection;

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


    public static boolean addInitialMeterReading(float intDiesel,float intPetrol) {
        try {
            PreparedStatement insertStat=connection.prepareStatement("INSERT INTO MeterReading VALUES (?,?,?,?,?)");
            insertStat.setFloat(1,intDiesel);
            insertStat.setFloat(2,intPetrol);
            insertStat.setInt(3,-1);
            insertStat.setInt(4,-1);
            insertStat.setDate(5,new Date((new java.util.Date()).getTime()));
            insertStat.executeUpdate();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addFinalMeterReading(float finDiesel,float finPetrol) {
        try {
            PreparedStatement insertStat=connection.prepareStatement("UPDATE MeterReading SET FDiesel=?,FPetrol=?");
            insertStat.setFloat(1,finDiesel);
            insertStat.setFloat(2,finPetrol);
            insertStat.executeUpdate();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
