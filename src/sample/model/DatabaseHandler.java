package sample.model;

import sample.controller.SystemLogging;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Calendar;


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
        Calendar todayCalendar=Calendar.getInstance();
        todayCalendar.setTime(new java.util.Date());
        try {
            Statement checkValue=connection.createStatement();
            ResultSet resultSet=checkValue.executeQuery("SELECT * FROM MeterReading ORDER BY ID DESC FETCH NEXT 1 ROWS ONLY");
            resultSet.next();

            Calendar dbCalendar=Calendar.getInstance();
            dbCalendar.setTime(resultSet.getDate(6));

            if((todayCalendar.get(Calendar.YEAR)==dbCalendar.get(Calendar.YEAR))&&(todayCalendar.get(Calendar.MONTH)==dbCalendar.get(Calendar.MONTH))&&(todayCalendar.get(Calendar.DAY_OF_MONTH)==dbCalendar.get(Calendar.DAY_OF_MONTH))) {
                SystemLogging.getLogger().fine("Initial Meter Reading Already Taken. Loading Home Screen.");
                return true;
            } else {
                SystemLogging.getLogger().fine("No Initial Meter Reading Found. Loading Splash Screen.");
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

    public static RateModel getCurrentRate() {
        RateModel rateModel=new RateModel(0,0);
        try {
            Statement getRate=connection.createStatement();
            ResultSet resultSet=getRate.executeQuery("SELECT * FROM SystemStatus WHERE ID<3");
            resultSet.next();
            rateModel.setrDiesel(resultSet.getFloat(2));
            resultSet.next();
            rateModel.setrPetrol(resultSet.getFloat(2));
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rateModel;
    }

    public static boolean setCurrentRate(float rDiesel,float rPetrol) {
        Date today=new Date(new java.util.Date().getTime());
        try {
            PreparedStatement setRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=1");
            setRate.setString(1,String.valueOf(rDiesel));
            setRate.setDate(2,today);
            setRate.execute();

            setRate=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=2");
            setRate.setString(1,String.valueOf(rPetrol));
            setRate.setDate(2,today);
            setRate.execute();

            return true;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AdminUser getAdminParameters() {
        AdminUser user=new AdminUser();
        try {
            Statement getUserData=connection.createStatement();
            ResultSet resultSet=getUserData.executeQuery("SELECT * FROM SystemStatus WHERE ID<5 AND ID>2");
            resultSet.next();
            String username=resultSet.getString(2);
            resultSet.next();
            user.initializeAdmin(username,resultSet.getString(2));
            resultSet.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return user;
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
