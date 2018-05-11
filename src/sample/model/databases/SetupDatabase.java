package sample.model.databases;

import sample.Reference;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SetupDatabase {
    private static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    private static Connection connection;


    public static boolean setupDatabase() {
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(Reference.SETUP_URL);
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

    public static boolean createCompanyTable(String name) {
        try {
            Statement createTable=connection.createStatement();
            createTable.execute("CREATE TABLE "+name+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");
            return true;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }


    private static boolean createTable() {
        try {
            Statement createTable=connection.createStatement();
            createTable.execute("CREATE TABLE MeterReading (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1),IDiesel FLOAT NOT NULL,IPetrol FLOAT NOT NULL,ICounter FLOAT NOT NULL,FDiesel FLOAT NOT NULL,FPetrol FLOAT NOT NULL,FCounter FLOAT NOT NULL,Date DATE NOT NULL)");
            createTable.execute("CREATE TABLE SystemStatus (ID INT NOT NULL,VALUE VARCHAR(30) NOT NULL,Date DATE NOT NULL)");
            createTable.execute("CREATE TABLE CreditCompany (ID INT NOT NULL,COMPANY_NAME VARCHAR(30) NOT NULL,TRANSACTION_ID INT NOT NULL,DAY VARCHAR(2) NOT NULL,DATE DATE NOT NULL)");
            createTable.execute("CREATE TABLE CreditTransaction (ID INT NOT NULL,VEHICLE_NO VARCHAR(15) NOT NULL,TYPE VARCHAR(1) NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL");
            createTable.execute("CREATE TABLE Refill(DEXPECTED FLOAT NOT NULL,DACTUAL FLOAT NOT NULL,PEXPECTED FLOAT NOT NULL,PACTUAL FLOAT NOT NULL,DLOSS FLOAT NOT NULL,PLOSS FLOAT NOT NULL,DATE DATE NOT NULL)");
            createTable.execute("CREATE TABLE InstantTransaction (MODE VARCHAR(1) NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL)");
            createTable.execute("CREATE TABLE DailyAnalytics (IDVOL FLOAT NOT NULL,IPVOL FLOAT NOT NULL,CDVOL FLOAT NOT NULL,CPVOL FLOAT NOT NULL,IEPRICE FLOAT NOT NULL,CEPRICE FLOAT NOT NULL,OILPRICE FLOAT NOT NULL,DRATE FLOAT NOT NULL,PRATE FLOAT NOT NULL,DATE DATE NOT NULL)");


            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
