package sample.OutputTasks;

import sample.model.CreditCompanyClosingBill;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;

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
        String tableName = DatabaseHandler.generateTableName(model.getCompanyName());

        expiry_date = model.getExpireDate();

        Statement getData = connection.createStatement();
        ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName+" WHERE DATE<'"+expiry_date+"'");
        String type;
        while (resultSet.next()){
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


        /*
        Getting the previous expiry date of the company
         */


        Statement getTotal = Tasks.getConnection().createStatement();
        ResultSet rs = getTotal.executeQuery("SELECT SUM(PRICE) FROM "+tableName+" WHERE DATE<'"+expiry_date+"'");

        rs.next();
        total = (rs.getString(1) != null)? Double.parseDouble(rs.getString(1)): 0;

        ResultSet rs1 = getTotal.executeQuery("SELECT DUE FROM CREDITCOMPANY WHERE COMPANY_NAME='"+tableName+"'");

        if(rs1.next()) {
            due = Double.parseDouble(rs1.getString(1));
        }

        grandTotal = due+total;
        return builder;
    }
}