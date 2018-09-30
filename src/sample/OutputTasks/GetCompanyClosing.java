package sample.OutputTasks;

import sample.model.CreditCompanyClosingBill;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.Date;

public class GetCompanyClosing extends Tasks {
    private DebtListModel model;
    private double total,due,grandTotal;
    //private double due;
    private Date expiry_date;


    public GetCompanyClosing(DebtListModel model){
        this.model = model;
    }

    public String getTotalPrice(){
        return String.valueOf(total);
    }

    public String getDue(){
        return String.valueOf(due);
    }

    public String getGrandTotal() { return String.valueOf(grandTotal); }

    StringBuilder builder=new StringBuilder();

    public StringBuilder call() throws SQLException {
        //String tableName = DatabaseHandler.generateTableName(model.getCompanyName());

        String tableName = model.getCompanyName();
        tableName = tableName.replace(" ","_");

        expiry_date = model.getExpireDate();
        System.out.print(expiry_date);

        Statement getData = connection.createStatement();


        /*
        Since we have deleted the data based on whether the company is expired or not,
        so, we should print the data on bill under the same condition

        if the company is expired while closing, then print the data less than previous expiry date
        else (if forced closing?) then print all data of that company
         */

        if(model.isExpired()){
            ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName+" WHERE DATE<'"+expiry_date+"'");

            String type;
            while (resultSet.next()){
                System.out.print("Inside While loop");
                //builder.append(resultSet.getString(1)+"\t\t");
                type = resultSet.getString(2);
                builder.append(type+"  \t\t");

                if ((type.equals("Petrol") || (type.equals("Diesel")))) {
                    builder.append(resultSet.getString(3) + "         \t\t");
                }

                else {
                    builder.append("xxxx" + "       \t\t");
                }

                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(4)))+"   \t\t");
                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(5)))+"     \t\t");
                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(6)))+"  \t\t");
                builder.append(resultSet.getString(7)+"\n");
            }

            //Statement getTotal = Tasks.getConnection().createStatement();
            ResultSet rs = getData.executeQuery("SELECT SUM(PRICE) FROM "+tableName+" WHERE DATE<'"+expiry_date+"'");

            rs.next();
            total = (rs.getString(1) != null)? Double.parseDouble(rs.getString(1)): 0;

        }



        else{
            /*
            else during forced closing case
             */
            ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName);

            String type;
            while (resultSet.next()){
                System.out.print("Inside While loop");
                //builder.append(resultSet.getString(1)+"\t\t");
                type = resultSet.getString(2);
                builder.append(type+"  \t\t");

                if ((type.equals("Petrol") || (type.equals("Diesel")))) {
                    builder.append(resultSet.getString(3) + "         \t\t");
                }

                else {
                    builder.append("xxxx" + "       \t\t");
                }

                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(4)))+"   \t\t");
                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(5)))+"     \t\t");
                builder.append(new DecimalFormat("##.##").format(Double.parseDouble(resultSet.getString(6)))+"  \t\t");
                builder.append(resultSet.getString(7)+"\n");
            }

            //Statement getTotal = Tasks.getConnection().createStatement();
            ResultSet rs = getData.executeQuery("SELECT SUM(PRICE) FROM "+tableName);

            rs.next();
            total = (rs.getString(1) != null)? Double.parseDouble(rs.getString(1)): 0;
        }



        /*
        In our database the company name in CreditCompany table is SPACE seperated NOT Underscore seperated so, our table name should be re-formatted
        And to avoid the risk of case sensitive in future, the tablename is converted to uppercase during comparision.
         */
        tableName = tableName.replace("_"," ");
        ResultSet rs1 = getData.executeQuery("SELECT DUE FROM CREDITCOMPANY WHERE UPPER(COMPANY_NAME)= UPPER('"+tableName+"')");

        if(rs1.next()) {
            due = Double.parseDouble(rs1.getString(1));
            System.out.print("Due amount = "+ due);
        }

        grandTotal = due+total;
        return builder;
    }
}