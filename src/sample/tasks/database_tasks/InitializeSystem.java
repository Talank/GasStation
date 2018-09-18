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

/**+
 * This class initializes the system from database
 * First task to be executed by the system
 *
 */

public class InitializeSystem extends Tasks {


    private String errorMessage;

    /**+
     * This function initializes the {@link SystemStatus}, {@link CreditCompanyList}.
     * It also check for the company expiry, and notifies if it has
     * @return true if the initialization completed without errors
     */

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

                model = new DebtListModel(resultSet.getString(1), resultSet.getString(5), resultSet.getInt(2), resultSet.getInt(4), resultSet.getFloat(6), resultSet.getFloat(7));

                model.setDate(generator.convertDate(resultSet.getDate(8 )));
                model.setPrevExpireDate(generator.convertDate(resultSet.getDate(3)));
                model.setExpireDate(resultSet.getDate(3));
                model.setPrePaid(resultSet.getFloat(9));


                initialDate.setNepaliDate(model.getDate());

                /*
                * Our Case: Two types of company, company that expires in 15 days and other in 30 (i.e. 1st day of next month)
                * So, the main idea is, both these company expires in 1st day, however only 15 days company expires twice a month
                *
                * We check if the year has changed, if it has, then the companies are expired, no matter which one, otherwise the only chances of expiry is for 15 days company (as 30 day will expire next year)
                *
                * Otherwise, we check if the month has changed since the company registered, if so, then both the companies are expired, otherwise we now check if the day is > 15 as the 15 days
                * company may have expired.
                *
                * */

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


                if (model.isExpired()) {

                    /*
                     *Here we check the value of PREV_TRANSACTION_ID
                     * if it is 0 means that the company is not expired,if it is 1 then the company credit closing is done but payment is remaining
                     * and other value indicate that the company closing is not done
                     *
                     * We check if it is 0,then since the Company has just expired,we note the date, the company has expired i.e. either 15 or 1
                     */

                    if (resultSet.getInt(4) == 0) {

                        System.out.println("Company previous due was cleared!");
                        //Get the Transaction ID of the last Transaction performed, i.e transactionID
                        Statement getCounts = connection.createStatement();
                        ResultSet countResult = getCounts.executeQuery("SELECT COUNT(*) FROM " + DatabaseHandler.generateTableName(model.getCompanyName()));
                        countResult.next();
                        int count = countResult.getInt(1);
                        countResult = getCounts.executeQuery("SELECT TRANSACTION_ID FROM " + DatabaseHandler.generateTableName(model.getCompanyName()) + " OFFSET " + (count - 1) + " ROWS FETCH NEXT 1 ROWS ONLY");
                        countResult.next();

                        int transactionID = countResult.getInt(1);

                        //Store the transactionID in prev_transaction field, it is only used to indicate that the company has expired and not closed
                        //To adjust the id of other transactions when the company is closed, date is used instead
                        PreparedStatement updateExpireID = connection.prepareStatement("UPDATE CreditCompany SET PREV_TRANSACTION_ID=?,PREV_EXPIRE_DATE=? WHERE COMPANY_NAME=?");
                        updateExpireID.setInt(1,transactionID);
                        updateExpireID.setDate(2,date);
                        updateExpireID.setString(3,model.getCompanyName());

                        updateExpireID.execute();

                        countResult.close();
                        getCounts.close();
                        updateExpireID.close();

                        model.setPrevExpireDate(generator.toString());
                        model.setExpireDate(date);
                        model.setTransactionID(resultSet.getInt(2));
                        model.setPrevTransactionID(transactionID);
                    } else {
                        System.out.println("Company Previous due was unclear");
                        model.setTransactionID(resultSet.getInt(2));
                        model.setPrevTransactionID(resultSet.getInt(4));
                    }

                }
                arrayList.add(model);
            }

            connection.commit();
            SystemLogging.getLogger().fine("Initialize System done");

            //initialize the credit companies and add it to the CreditCompanyList

            CreditCompanyList.setCompanyList(arrayList);
            CreditCompanyList.setExpiredCompanyList(expiredList);
            Statement getValues = connection.createStatement();

            //Initialize all the parameters of the SystemStatus class from the databases

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

            //Perform System Validation

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
            SystemLogging.getLogger().warning("Failed to initialize the system ");

            rollBack();

            errorMessage="Database Error Detected.Could not Initialize System.Please Contact The Company Officials.";
        }

        return false;

    }

    /**
     * provides the error message associated with the initialization
     * @return the description of error
     */

    public String getErrorMessage() {
        return errorMessage;
    }

}
