package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.CreditCompanyClosingBill;
import sample.model.CreditDetailsModel;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import javax.xml.crypto.Data;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetCompanyClosing {
    private DebtListModel model;
    private float total,due,grandTotal;

    public GetCompanyClosing(DebtListModel model){
        this.model = model;
    }

    public List<List<CreditDetailsModel>> call() {
        List<List<CreditDetailsModel>> mainList=new ArrayList<>();
        //get the total no of rows in the company table


        try {
            Statement getDetails= Tasks.getConnection().createStatement();

            ResultSet resultSet;
            if (model.isExpired()) {
                resultSet = getDetails.executeQuery("SELECT SUM(PRICE) FROM " + DatabaseHandler.generateTableName(model.getCompanyName()) + " WHERE DATE<'" + model.getExpireDate() + "'");
                resultSet.next();
                total = (resultSet.getString(1) != null)? Float.parseFloat(resultSet.getString(1)): 0;

                resultSet=getDetails.executeQuery("SELECT * FROM " + DatabaseHandler.generateTableName(model.getCompanyName()) + " WHERE DATE<'" + model.getExpireDate() + "'");
            } else {
                resultSet = getDetails.executeQuery("SELECT SUM(PRICE) FROM " + DatabaseHandler.generateTableName(model.getCompanyName()));
                resultSet.next();
                total = (resultSet.getString(1) != null)? Float.parseFloat(resultSet.getString(1)): 0;

                resultSet=getDetails.executeQuery("SELECT * FROM "+DatabaseHandler.generateTableName(model.getCompanyName()) + " ORDER BY DATE ASC");
            }

            List<CreditDetailsModel> smallList=new ArrayList<>();
            int  i=1;//index used to group the transactions having same TransactionID
            boolean create=false;

            NepaliDateGenerator generator=new NepaliDateGenerator();

            //iterate through all the rows in the table
            while(resultSet.next()) {

                if(i==resultSet.getInt(1)) {

                    if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {//the row is for oil transaction

                        //add the data to smallList
                        smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                    } else {

                        //add the data to smallList
                        smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                    }

                } else {//the index is different from the initial index, i.e this is the next transaction
                    mainList.add(smallList); //add the list to the mainList
                    create=true; //to indicate to create another object for storing the next transaction set
                    i++; //increment the index
                }
                if(create) {

                    smallList=new ArrayList<>(); //create a new ArrayList()

                    //we are adding the result to the smallList, because we are in the iteration and , there is data in the current iteration
                    if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {

                        smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                    } else {

                        smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),generator.convertDate(resultSet.getDate(7)),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                    }
                    create=false;
                }

            }

            //add the last transaction arrayList to the main list
            mainList.add(smallList);



            resultSet = getDetails.executeQuery("SELECT DUE FROM CREDITCOMPANY WHERE UPPER(COMPANY_NAME)= UPPER('"+DatabaseHandler.generateTableName(model.getCompanyName())+"')");

            if(resultSet.next()) {
                due = Float.parseFloat(resultSet.getString(1));
            }

            grandTotal = due+total;

            //cleanup
            resultSet.close();
            getDetails.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return mainList;
    }

    public float getTotalPrice(){
        return total;
    }

    public float getDue(){
        return due;
    }

    public float getGrandTotal() { return grandTotal; }
}