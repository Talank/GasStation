package sample.tasks.database_tasks;

import sample.SystemValidation;
import sample.controller.SystemLogging;
import sample.model.CreditCompanyList;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.DateConverter;
import sample.nepali_date.NepaliDateGenerator;
import sample.nepali_date.ParseNepaliDate;
import sample.tasks.Tasks;

import java.sql.*;
import java.util.ArrayList;


public class InitializeSystem extends Tasks {


    private String errorMessage;

    public boolean customExecute() {

        SystemLogging.initializeLogger();//Initialize The Logger
        SystemStatus.setMainHomePath("../../view/main_home_views/mainHome.fxml");


        NepaliDateGenerator generator = new NepaliDateGenerator();

        Date currentDate = new Date((new java.util.Date()).getTime());

        SystemStatus.setCurrentDate(currentDate);
        SystemStatus.setTransactionDate(currentDate);

        SystemStatus.setNepaliCurrentDate(generator.toString());
        SystemStatus.setNepaliTransactionDate(generator.toString());

        System.out.println("System Current English Date:" + SystemStatus.getCurrentDate());
        System.out.println("System Current Nepali Date:" + SystemStatus.getNepaliCurrentDate());

        int nDay = generator.getDateFormat().getnDay();

        int totalDaysOfMonth = generator.getDateFormat().getTotalDaysOfMonth();

        int nMonth = generator.getDateFormat().getnMonth();
        int nYear=generator.getDateFormat().getnYear();

        ParseNepaliDate initialDate = new ParseNepaliDate();

        ArrayList<DebtListModel> arrayList = new ArrayList<>();

        ArrayList<DebtListModel> expiredList = new ArrayList<>();

        DebtListModel model;

        Date date=new Date(((new java.util.Date()).getTime()));
        DateConverter converter=new DateConverter();

        try {
            Statement getDebitList = connection.createStatement();
            ResultSet resultSet = getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while (resultSet.next()) {

                model = new DebtListModel(resultSet.getString(2), resultSet.getString(6), resultSet.getInt(1), resultSet.getInt(3), resultSet.getInt(5), resultSet.getFloat(7), resultSet.getFloat(8));

                model.setDate(generator.convertDate(resultSet.getDate(9)));
                model.setPrevExpireDate(generator.convertDate(resultSet.getDate(4)));
                model.setExpireDate(resultSet.getDate(4));


                initialDate.setNepaliDate(model.getDate());

                /**
                 *We have generally two cases
                 * 1st when the last time the company credited was in 12th month,then the year may change,so we check if the year has changed,if so we expire the company in new year day
                 * other wise we check if the year is same,then it might be the case that the 15 days credit cycle company may expire,so we check that
                 *
                 * 2nd case is when the year is same and month is different,then the case is similar to 1st
                 */


                if(initialDate.getnMonth()==12) {
                    if(initialDate.getnYear()<nYear) {
                        date=converter.getEnglishDate(initialDate.getnYear()+1,1,1);
                        expiredList.add(model);
                        model.setExpired(true);
                    } else if(initialDate.getnYear()==nYear) {
                        if (nDay >= 15 && (Integer.parseInt(model.getDays()) != 30)&&initialDate.getnDay()<15) {
                            date=converter.getEnglishDate(nYear,nMonth,15);
                            expiredList.add(model);
                            model.setExpired(true);
                        }
                    }
                } else {
                    if (initialDate.getnMonth() < nMonth) {
                        date=converter.getEnglishDate(initialDate.getnYear(),initialDate.getnMonth()+1,1);
                        expiredList.add(model);
                        model.setExpired(true);
                    } else if (nDay >= 15 && (Integer.parseInt(model.getDays()) != 30)&&initialDate.getnDay()<15) {
                        date=converter.getEnglishDate(nYear,nMonth,15);
                        expiredList.add(model);
                        model.setExpired(true);
                    }
                }

                System.out.println("Current Expired English Date:"+date.toString());


                if (model.isExpired()) {

                    /**
                     *Here we check the value of PREV_TRANSACTION_ID
                     * if it is 0 means that the company is not expired,if it is 1 then the company credit closing is done but payment is remaining
                     *
                     * We check if it is 0,then since the Company has just expired,we note the current date
                     */

                    if (resultSet.getInt(5) == 0) {

                        System.out.println("Company previous due was cleared!");

                        Statement getCounts = connection.createStatement();
                        ResultSet countResult = getCounts.executeQuery("SELECT COUNT(*) FROM " + DatabaseHandler.generateTableName(model.getCompanyName(), model.getId()));
                        countResult.next();
                        int count = countResult.getInt(1);
                        countResult = getCounts.executeQuery("SELECT TRANSACTION_ID FROM " + DatabaseHandler.generateTableName(model.getCompanyName(), model.getId()) + " OFFSET " + (count - 1) + " ROWS FETCH NEXT 1 ROWS ONLY");
                        countResult.next();

                        int transactionID = countResult.getInt(1);

                        PreparedStatement updateExpireID = connection.prepareStatement("UPDATE CreditCompany SET PREV_TRANSACTION_ID=" + transactionID + ",PREV_EXPIRE_DATE='" + date.toString() + "' WHERE ID=" + model.getId());
                        updateExpireID.execute();

                        countResult.close();
                        getCounts.close();
                        updateExpireID.close();

                        model.setPrevExpireDate(generator.toString());
                        model.setExpireDate(date);
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

            connection.commit();

            CreditCompanyList.setCompanyList(arrayList);
            CreditCompanyList.setExpiredCompanyList(expiredList);
            Statement getValues = connection.createStatement();

            resultSet = getValues.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>10 AND ID<19");

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
            resultSet.next();

            SystemValidation validation=new SystemValidation();
            if(!validation.validate(resultSet.getString(1))) {
                System.out.println("System Runtime Validation Failed");
                errorMessage="System Validation Failed.Could not Initialize System.Please Contact The Company Officials.";
                return false;
            }

            Statement getRate = connection.createStatement();
            resultSet = getRate.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>8 AND ID<11");
            resultSet.next();

            SystemStatus.setdRate((Float.parseFloat(resultSet.getString(1))));
            resultSet.next();
            SystemStatus.setpRate(Float.parseFloat(resultSet.getString(1)));

            resultSet.close();
            getRate.close();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
            errorMessage="Database Error Detected.Could not Initialize System.Please Contact The Company Officials.";
        }

        return false;

    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
