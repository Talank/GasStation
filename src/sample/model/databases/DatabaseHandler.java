package sample.model.databases;

import javafx.stage.Stage;
import sample.controller.SystemLogging;
import sample.model.*;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
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
        try {
            Statement checkValue=connection.createStatement();
            ResultSet resultSet=checkValue.executeQuery("SELECT * FROM MeterReading ORDER BY ID DESC FETCH NEXT 1 ROWS ONLY");
            resultSet.next();

            if(checkCurrentDate(resultSet.getDate(8))) {
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


    public static boolean addInitialMeterReading(float intDiesel,float intPetrol,float intCounter) {
        Date today=new Date((new java.util.Date()).getTime());
        try {
            PreparedStatement insertStat=connection.prepareStatement("INSERT INTO MeterReading(IDiesel,IPetrol,ICounter,FDiesel,FPetrol,FCounter,Date) VALUES (?,?,?,?,?,?,?)");
            insertStat.setFloat(1,intDiesel);
            insertStat.setFloat(2,intPetrol);
            insertStat.setFloat(3,intCounter);
            insertStat.setFloat(4,-1f);
            insertStat.setFloat(5,-1f);
            insertStat.setFloat(6,-1f);
            insertStat.setDate(7,today);
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

    public static boolean setAdminParameters(String userName,String password) {
        Date today=new Date(new java.util.Date().getTime());
        try {
            PreparedStatement updateParameters=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=3");
            updateParameters.setString(1,userName);
            updateParameters.setDate(2,today);
            updateParameters.execute();

            updateParameters=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=4");
            updateParameters.setString(1,password);
            updateParameters.setDate(2,today);
            updateParameters.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void storeCreditTransaction(CreditTransaction creditTransaction) {
        try {
            Statement getCompanyID=connection.createStatement();
            ResultSet resultSet=getCompanyID.executeQuery("SELECT ID FROM CreditCompany WHERE COMPANY_NAME='"+creditTransaction.getCompanyName()+"'");
            resultSet.next();
            PreparedStatement insertCredit=connection.prepareStatement("INSERT INTO CreditTransaction VALUES(?,?,?,?,?,?)");
            insertCredit.setInt(1,resultSet.getInt(1));
            insertCredit.setString(2,creditTransaction.getVehicleNO());
            insertCredit.setString(3,creditTransaction.getType()+"");
            insertCredit.setFloat(4,creditTransaction.getVolume());
            insertCredit.setFloat(5,creditTransaction.getPrice());
            insertCredit.setDate(6,creditTransaction.getDate());
            insertCredit.execute();

            SystemLogging.getLogger().fine("Credit Transaction Stored in Database");

        }  catch(Exception e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }
    }

    public static boolean storeInstantTransaction(float price,float volume) {

        try {
            PreparedStatement insertInstantTransaction=connection.prepareStatement("INSERT INTO InstantTransaction VALUES (?,?)");
            insertInstantTransaction.setFloat(1,price);
            insertInstantTransaction.setFloat(2,volume);
            insertInstantTransaction.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static float getTotalInstantPrice() {
        float price=-1;
        try {
            Statement getPrice=connection.createStatement();
            ResultSet resultSet=getPrice.executeQuery("Select PRICE FROM InstantTransaction");
            price=0;
            while(resultSet.next()) {
                price+=resultSet.getFloat(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return price;
    }


    public static ArrayList<DebtListModel> getDebtCompany() {
        ArrayList<DebtListModel> arrayList=new ArrayList<>();
        try {
            Statement getDebitList=connection.createStatement();
            ResultSet resultSet=getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while(resultSet.next()) {
                arrayList.add(new DebtListModel(resultSet.getString(1),resultSet.getDate(4).toString(),resultSet.getString(3),resultSet.getInt(2)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    public static ArrayList<CreditDetailsModel> getDebtCompanyDetails(int id) {
        ArrayList<CreditDetailsModel> arrayList=new ArrayList<>();
        try {
            String type;
            Statement getDetails=connection.createStatement();
            ResultSet resultSet=getDetails.executeQuery("SELECT VEHICLE_NO,TYPE,VOLUME,PRICE,DATE FROM CreditTransaction WHERE ID="+id);
            while(resultSet.next()) {
                if(resultSet.getString(2).equals("1"))
                    type="Diesel";
                else
                    type="Petrol";
                arrayList.add(new CreditDetailsModel(resultSet.getString(1),type,resultSet.getDate(5).toString(),String.valueOf(resultSet.getFloat(3)),String.valueOf(resultSet.getFloat(4))));
            }
            resultSet.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    public static int saveCompany(String companyName,String creditCycle) {
        int i=0,j=-1;
        boolean status;
        try {
            Statement getIDs=connection.createStatement();
            ResultSet resultSet=getIDs.executeQuery("SELECT COUNT(*) FROM CreditCompany");
            resultSet.next();
            int idArray[]=new int[resultSet.getInt(1)];
            resultSet.close();

            resultSet=getIDs.executeQuery("SELECT ID FROM CreditCompany");

            while(resultSet.next())
                idArray[i++]=resultSet.getInt(1);
            j=1;
            while(true) {
                status=false;
                for(i=0;i<idArray.length;i++) {
                    if(j==idArray[i]) {
                        status=true;
                        j++;
                        break;
                    }
                }
                if(!status)
                    break;
            }

            PreparedStatement addCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?)");
            addCompany.setString(1,companyName);
            addCompany.setInt(2,j);
            addCompany.setString(3,creditCycle);
            addCompany.setDate(4,new Date((new java.util.Date()).getTime()));
            addCompany.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return j;
    }

    public static boolean deleteCompany(String query) {
        try {
            PreparedStatement delete=connection.prepareStatement("DELETE FROM CreditCompany WHERE ID IN ("+query+")");
            delete.execute();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateCompany(DebtListModel model) {
        try {
            PreparedStatement update=connection.prepareStatement("UPDATE CreditCompany SET COMPANY_NAME='"+model.getCompanyName()+"',DAY='"+model.getDays()+"' WHERE ID="+model.getId());
            update.execute();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DailyAnalyticsModel getInitialValues() {
        DailyAnalyticsModel model=new DailyAnalyticsModel(-1f,-1f,-1f);
        try {
            Statement getValues=connection.createStatement();
            ResultSet resultSet=getValues.executeQuery("SELECT IPetrol,IDiesel,ICounter,Date FROM MeterReading ORDER BY ID DESC FETCH NEXT 1 ROWS ONLY");

            resultSet.next();

            if(checkCurrentDate(resultSet.getDate(4))) {
                model.setcInitial(resultSet.getFloat(3));
                model.setdInitial(resultSet.getFloat(2));
                model.setpInitial(resultSet.getFloat(1));
            } else {
                SystemLogging.getLogger().severe("Date Doesn't Match,May be Wrong Start Up Of System!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public static boolean updateFinalReadings(float fDiesel,float fPetrol,float fCounter) {
        try {
            Statement getID=connection.createStatement();
            ResultSet resultSet=getID.executeQuery("SELECT ID,Date FROM MeterReading ORDER BY ID DESC FETCH NEXT 1 ROWS ONLY");
            resultSet.next();
            int id=resultSet.getInt(1);
            if(checkCurrentDate(resultSet.getDate(2))) {
                PreparedStatement updateReading=connection.prepareStatement("UPDATE MeterReading SET FDiesel="+fDiesel+",FPetrol="+fPetrol+",FCounter="+fCounter+" WHERE ID="+id);
                updateReading.execute();
                return true;
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<RefillModel> getRefillModel() {
        ArrayList<RefillModel> arrayList=new ArrayList<>();

        try {
            Statement getRefill=connection.createStatement();
            ResultSet resultSet=getRefill.executeQuery("SELECT * FROM Refill");
            while(resultSet.next()) {
                arrayList.add(new RefillModel(String.valueOf(resultSet.getFloat(1)),String.valueOf(resultSet.getFloat(3)),String.valueOf(resultSet.getFloat(2)),resultSet.getDate(4)));
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public static boolean checkCurrentDate(Date date) {
        Calendar todayCalendar=Calendar.getInstance();
        todayCalendar.setTime(new java.util.Date());

        Calendar dbCalendar=Calendar.getInstance();
        dbCalendar.setTime(date);

        return (todayCalendar.get(Calendar.YEAR)==dbCalendar.get(Calendar.YEAR))&&(todayCalendar.get(Calendar.MONTH)==dbCalendar.get(Calendar.MONTH))&&(todayCalendar.get(Calendar.DAY_OF_MONTH)==dbCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean storeRefill(RefillModel model) {
        try {
            PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO Refill VALUES (?,?,?,?)");
            insertRefill.setFloat(1,Float.parseFloat(model.getExpected()));
            insertRefill.setFloat(2,Float.parseFloat(model.getActual()));
            insertRefill.setFloat(3,Float.parseFloat(model.getLoss()));
            insertRefill.setDate(4,model.getDate());
            insertRefill.execute();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
