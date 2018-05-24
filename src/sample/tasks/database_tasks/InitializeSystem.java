package sample.tasks.database_tasks;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import sample.controller.SystemLogging;
import sample.model.*;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDate;
import sample.nepali_date.NepaliDateFormat;
import sample.nepali_date.NepaliDateGenerator;
import sample.nepali_date.ParseNepaliDate;
import sample.tasks.Tasks;


import java.sql.*;

import java.time.LocalDate;
import java.util.ArrayList;


public class InitializeSystem extends Tasks {

    @Override
    public void execute() {

        SystemLogging.initializeLogger();//Initialize The Logger
        SystemStatus.setMainHomePath("../../view/main_home_views/mainHome.fxml");


        NepaliDateGenerator generator=new NepaliDateGenerator();

        Date currentDate=new Date((new java.util.Date()).getTime());

        SystemStatus.setCurrentDate(currentDate);
        SystemStatus.setTransactionDate(currentDate);

        SystemStatus.setNepaliCurrentDate(generator.toString());
        SystemStatus.setNepaliTransactionDate(generator.toString());

        System.out.println("System Current English Date:"+SystemStatus.getCurrentDate());
        System.out.println("System Current Nepali Date:"+SystemStatus.getNepaliCurrentDate());

        int nDay=generator.getDateFormat().getnDay();

        int totalDaysOfMonth=generator.getDateFormat().getTotalDaysOfMonth();

        int nMonth=generator.getDateFormat().getnMonth();

        ParseNepaliDate initialDate=new ParseNepaliDate();

        ArrayList<DebtListModel> arrayList=new ArrayList<>();

        ArrayList<DebtListModel> expiredList=new ArrayList<>();

        DebtListModel model;

        try {
            Statement getDebitList=connection.createStatement();
            ResultSet resultSet=getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while(resultSet.next()) {

                model=new DebtListModel(resultSet.getString(2),resultSet.getString(6),resultSet.getInt(1),resultSet.getInt(3),resultSet.getInt(5),resultSet.getFloat(7),resultSet.getFloat(8));

                model.setDate(generator.convertDate(resultSet.getDate(9)));
                model.setPrevExpireDate(generator.convertDate(resultSet.getDate(4)));
                model.setExpireDate(resultSet.getDate(4));


                initialDate.setNepaliDate(model.getDate());

                /**
                 * Here we have two types of company i.e 30 days and other days(probably 15 days)
                 * First check for the change in month,if there is month difference between the current month and the last time the company was credited,
                 * then the company is expired.
                 * It solves the problem if the software was not open in 1st day of the month ,because we are only interested in month difference
                 *
                 * Second case is for 15 days company,where we check if current day is more than 15 and the currently read company has 15 days cycle time,
                 */

                if(initialDate.getnMonth()<nMonth) {
                    expiredList.add(model);
                    model.setExpired(true);
                } else {

                    if(nDay>=15&&(Integer.parseInt(model.getDays())!=30)) {
                        expiredList.add(model);
                        model.setExpired(true);
                    }

                }

                if(model.isExpired()) {


                    if(resultSet.getInt(5)==1) {

                        System.out.println("Company previous due was cleared!");

                        Statement getCounts=connection.createStatement();
                        ResultSet countResult=getCounts.executeQuery("SELECT COUNT(*) FROM "+DatabaseHandler.generateTableName(model.getCompanyName(),model.getId()));
                        countResult.next();
                        int count=countResult.getInt(1);
                        countResult=getCounts.executeQuery("SELECT TRANSACTION_ID FROM "+DatabaseHandler.generateTableName(model.getCompanyName(),model.getId())+" OFFSET "+(count-1)+" ROWS FETCH NEXT 1 ROWS ONLY");
                        countResult.next();

                        int transactionID=countResult.getInt(1);

                        PreparedStatement updateExpireID=connection.prepareStatement("UPDATE CreditCompany SET PREV_TRANSACTION_ID="+transactionID+",PREV_EXPIRE_DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID="+model.getId());
                        updateExpireID.execute();

                        model.setPrevExpireDate(generator.toString());
                        model.setExpireDate(SystemStatus.getCurrentDate());
                        model.setTransactionID(resultSet.getInt(3));
                        model.setPrevTransactionID(transactionID);
                    } else {
                        System.out.println("Company Previous due was unclear");
                        model.setTransactionID(resultSet.getInt(3));
                        model.setPrevTransactionID(resultSet.getInt(5));
                    }


                }


                arrayList.add(model);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        CreditCompanyList.setCompanyList(arrayList);
        CreditCompanyList.setExpiredCompanyList(expiredList);

        try {
            Statement getValues=connection.createStatement();
            ResultSet resultSet=getValues.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>10 AND ID<18");

            resultSet.next();
            SystemStatus.setiDiesel(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiPetrol(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiCounter(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setDieselStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setPetrolStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setBillPrinter(resultSet.getString(1));
            resultSet.next();
            SystemStatus.setAnalyticsPrinter(resultSet.getString(1));
            resultSet.close();

        } catch(Exception e) {
            e.printStackTrace();
        }


        try {
            Statement getRate=connection.createStatement();
            ResultSet resultSet=getRate.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>8 AND ID<11");
            resultSet.next();

            SystemStatus.setdRate((Float.parseFloat(resultSet.getString(1))));
            resultSet.next();
            SystemStatus.setpRate(Float.parseFloat(resultSet.getString(1)));
            resultSet.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
