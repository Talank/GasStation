package sample.model.databases;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.controller.SystemLogging;
import sample.model.*;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.model.transaction_model.Transaction;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DatabaseHandler {
    private static final String DRIVER="org.apache.derby.jdbc.ClientDriver";
    private static final String JDBC_URL="jdbc:derby://localhost:1527/D:/InelliJIDEA/GasStation/ohmoilpump;create=false";
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


    public static void storeCreditTransaction(List<Transaction> transactions,int transactionID,int id) {

        float pVolume=0,dVolume=0,ePrice=0;

        try {
            connection.setAutoCommit(false);
           /* Statement getCompanyID=connection.createStatement();
            ResultSet resultSet=getCompanyID.executeQuery("SELECT ID FROM CreditCompany WHERE COMPANY_NAME='"+Transaction.getName()+"'");
            resultSet.next();
            int id=resultSet.getInt(1);*/

            System.out.println("ID:"+transactionID);

            StringBuilder query=new StringBuilder("INSERT INTO "+generateTableName(Transaction.getName(),id)+" VALUES (?,?,?,?,?,?,?)");

            PreparedStatement insertCredit=connection.prepareStatement(query.toString());

            System.out.println("Query:"+query);

            Date date=getCurrentDate();
            for(Transaction transaction:transactions) {

                insertCredit.setInt(1,transactionID);
                insertCredit.setString(2,transaction.getType());
                insertCredit.setFloat(4,transaction.getRate());
                if(transaction.getMode()==2) {
                    insertCredit.setString(3,transaction.getType());
                    insertCredit.setFloat(5,transaction.getQuantity());
                    ePrice+=transaction.getPrice();
                } else {

                    if(transaction.getMode()==0) {
                        dVolume+=transaction.getVolume();
                    } else {
                        pVolume+=transaction.getVolume();
                    }

                    insertCredit.setString(3,transaction.getVehicleNo());
                    insertCredit.setFloat(5,transaction.getVolume());
                }
                insertCredit.setFloat(6,transaction.getPrice());
                insertCredit.setDate(7,date);

                insertCredit.addBatch();

            }

            insertCredit.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            transactionID++;
            insertCredit=connection.prepareStatement("UPDATE CreditCompany SET TRANSACTION_ID="+transactionID+" WHERE ID="+id);
            insertCredit.execute();

            storeTotalTransaction(pVolume,dVolume,ePrice,8);

            SystemLogging.getLogger().fine("Credit Transaction Stored in Database");

        }  catch(SQLException e) {
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("Error Storing Transaction in Database");
        }
    }


    public static String generateTableName(String name,int id) {
        String tableName=name.replace(' ','_').toLowerCase()+id;
        System.out.println("Table Name:"+tableName);
        return tableName;
    }


    public static boolean storeInstantTransaction(List<Transaction> transactions) {

        float pVolume=0,dVolume=0,ePrice=0;

        try {
        /*    connection.setAutoCommit(false);
            PreparedStatement insertInstant=connection.prepareStatement("INSERT INTO InstantTransaction VALUES (?,?,?)");*/

            for(Transaction transaction:transactions) {
                if(transaction.getMode()==2) {
                  /*  insertInstant.setString(1,"2");
                    insertInstant.setFloat(2,transaction.getQuantity());*/
                    ePrice+=transaction.getPrice();
                } else {
                    if(transaction.getMode()==0) {
                        dVolume+=transaction.getVolume();
                    } else {
                        pVolume+=transaction.getVolume();
                    }
                /*    insertInstant.setString(1,String.valueOf(transaction.getMode()));
                    insertInstant.setFloat(2,transaction.getVolume());*/
                }
            /*    insertInstant.setFloat(3,transaction.getPrice());
                insertInstant.addBatch();*/
            }
        /*    insertInstant.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);*/

            storeTotalTransaction(pVolume,dVolume,ePrice,5);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void storeTotalTransaction(float pVolume,float dVolume,float ePrice,int arr) {

        try {

            connection.setAutoCommit(false);

            Statement getInstant=connection.createStatement();
            ResultSet resultSet;
            if(arr==5) {
                resultSet=getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>4 AND ID<8");
            } else {
                resultSet=getInstant.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>7 AND ID<11");
            }

            resultSet.next();
            pVolume+=Float.parseFloat(resultSet.getString(1));
            resultSet.next();
            dVolume+=Float.parseFloat(resultSet.getString(1));
            resultSet.next();
            ePrice+=Float.parseFloat(resultSet.getString(1));

            resultSet.close();

            Statement updateInstant=connection.createStatement();
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE="+dVolume+" WHERE ID="+(arr++));
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE="+pVolume+" WHERE ID"+(arr++));
            updateInstant.addBatch("UPDATE SystemStatus SET VALUE="+ePrice+" WHERE ID"+(arr));

            updateInstant.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

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
                arrayList.add(new DebtListModel(resultSet.getString(2),resultSet.getDate(5).toString(),resultSet.getString(4),resultSet.getInt(1),resultSet.getInt(3)));
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
                arrayList.add(new CreditDetailsModel(resultSet.getString(1),type,resultSet.getDate(5).toString(),String.valueOf(resultSet.getFloat(3)),String.valueOf(resultSet.getFloat(4)),""));
            }
            resultSet.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    public static List<List<CreditDetailsModel>> getCreditCompanyDetails(String tableName) {
        List<List<CreditDetailsModel>> mainList=new ArrayList<>();
        try {
            Statement getDetails=connection.createStatement();
            ResultSet resultSet=getDetails.executeQuery("SELECT * FROM "+tableName);

            List<CreditDetailsModel> smallList=new ArrayList<>();
            int  i=1;
            boolean create=false;
            while(resultSet.next()) {
                if(i==resultSet.getInt(1)) {
                    if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {
                        smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),resultSet.getDate(7).toString(),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                    } else {
                        smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),resultSet.getDate(7).toString(),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                    }

                } else {
                    mainList.add(smallList);
                    create=true;
                    i++;
                }
                if(create) {
                    smallList=new ArrayList<>();
                    if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {
                        smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),resultSet.getDate(7).toString(),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                    } else {
                        smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),resultSet.getDate(7).toString(),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                    }
                    create=false;
                }
            }
            mainList.add(smallList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



        return mainList;
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

            PreparedStatement addCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?,?)");
            addCompany.setString(2,companyName);
            addCompany.setInt(1,j);
            addCompany.setString(4,creditCycle);
            addCompany.setInt(3,1);
            addCompany.setDate(5,getCurrentDate());
            addCompany.execute();

            Statement createTable=connection.createStatement();
            createTable.execute("CREATE TABLE "+generateTableName(companyName,j)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return j;
    }

    public static boolean deleteCompany(DebtListModel model) {
        try {
            PreparedStatement delete=connection.prepareStatement("DELETE FROM CreditCompany WHERE ID="+model.getId());
            delete.execute();
            delete=connection.prepareStatement("DROP TABLE "+generateTableName(model.getCompanyName(),model.getId()));
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


    public static ArrayList<DailyTotalAnalyticModel> getDailyTotalAnalyticModel() {
        ArrayList<DailyTotalAnalyticModel> arrayList=new ArrayList<>();

        try {

            Statement getAnalyticModel=connection.createStatement();
            ResultSet resultSet=getAnalyticModel.executeQuery("SELECT * FROM DailyAnalytics");

            while(resultSet.next()) {
                arrayList.add(new DailyTotalAnalyticModel(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),String.valueOf(resultSet.getFloat(5)+resultSet.getFloat(6)),resultSet.getString(7),String.valueOf(resultSet.getFloat(7)+resultSet.getFloat(5)+resultSet.getFloat(6)),resultSet.getDate(10).toString()));
            }
            resultSet.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(arrayList.get(0).getIDieselVol());

        return arrayList;
    }

    public static DailyTotalAnalyticModel closing() {
        DailyTotalAnalyticModel model=new DailyTotalAnalyticModel();
        try {


            Statement getAnalytics=connection.createStatement();
            ResultSet resultSet=getAnalytics.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>4 AND ID<11");
            resultSet.next();

            model.setIDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setIPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setIExtra(resultSet.getString(1));
            resultSet.next();
            model.setCDieselVol(resultSet.getString(1));
            resultSet.next();
            model.setCPetrolVol(resultSet.getString(1));
            resultSet.next();
            model.setCExtra(resultSet.getString(1));
            resultSet.close();

            model.setOilIncome(String.valueOf(((Float.parseFloat(model.getIDieselVol())+Float.parseFloat(model.getCDieselVol()))*SystemStatus.getdRate()+(Float.parseFloat(model.getIPetrolVol())+ Float.parseFloat(model.getCPetrolVol())*SystemStatus.getpRate()))));

            setDailyTotalAnalyticModel(model);


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return model;
    }

    public static void setDailyTotalAnalyticModel(DailyTotalAnalyticModel model) {

        try {
            Date date=getCurrentDate();

            PreparedStatement insertTotal=connection.prepareStatement("INSERT INTO DailyAnalytics VALUES (?,?,?,?,?,?,?,?,?,?)");

            insertTotal.setFloat(1,Float.parseFloat(model.getIDieselVol()));
            insertTotal.setFloat(2,Float.parseFloat(model.getIPetrolVol()));
            insertTotal.setFloat(3,Float.parseFloat(model.getCDieselVol()));
            insertTotal.setFloat(4,Float.parseFloat(model.getCPetrolVol()));
            insertTotal.setFloat(5,Float.parseFloat(model.getIExtra()));
            insertTotal.setFloat(6,Float.parseFloat(model.getCExtra()));
            insertTotal.setFloat(7,Float.parseFloat(model.getOilIncome()));
            insertTotal.setFloat(8,SystemStatus.getdRate());
            insertTotal.setFloat(9,SystemStatus.getpRate());
            insertTotal.setDate(10,date);

            insertTotal.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    public static boolean checkCurrentDate(Date date) {
        Calendar todayCalendar=Calendar.getInstance();
        todayCalendar.setTime(new java.util.Date());

        Calendar dbCalendar=Calendar.getInstance();
        dbCalendar.setTime(date);

        return (todayCalendar.get(Calendar.YEAR)==dbCalendar.get(Calendar.YEAR))&&(todayCalendar.get(Calendar.MONTH)==dbCalendar.get(Calendar.MONTH))&&(todayCalendar.get(Calendar.DAY_OF_MONTH)==dbCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date getCurrentDate() {
        return new Date((new java.util.Date()).getTime());
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
