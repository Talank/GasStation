package sample.model;

import sample.controller.SystemLogging;

import java.sql.*;

public class DatabaseHandler {
    private static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL="jdbc:derby:ohmoilpump;create=false";
    private static final String JDBC_SHUTDOWN="jdbc:derby:;shutdown=true";
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


    public static boolean resumeSystem() {
        Date today=new Date((new java.util.Date()).getTime());
        try {
            Statement checkValue=connection.createStatement();
            ResultSet resultSet=checkValue.executeQuery("SELECT * FROM MeterReading WHERE DATE ='"+today+"' ORDER BY ID DESC FETCH NEXT 1 ROWS ONLY");
            if(resultSet.getFetchSize()!=0) {
                SystemLogging.getLogger().fine("Initial Meter Reading Already Taken. Loading Home Screen.");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean addInitialMeterReading(float intDiesel,float intPetrol) {
        Date today=new Date((new java.util.Date()).getTime());
        try {
            PreparedStatement insertStat=connection.prepareStatement("INSERT INTO MeterReading(IDiesel,IPetrol,FDiesel,FPetrol,Date) VALUES (?,?,?,?,?)");
            insertStat.setFloat(1,intDiesel);
            insertStat.setFloat(2,intPetrol);
            insertStat.setInt(3,-1);
            insertStat.setInt(4,-1);
            insertStat.setDate(5,today);
            insertStat.execute();
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
