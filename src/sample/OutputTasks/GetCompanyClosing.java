package sample.OutputTasks;

import sample.model.CreditCompanyClosingBill;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class GetCompanyClosing extends Tasks {
    private DebtListModel model;
    private double total,due,grandTotal;
    //private double due;

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

        Statement getData = connection.createStatement();
        ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName);
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

        Statement getTotal = Tasks.getConnection().createStatement();
        ResultSet rs = getTotal.executeQuery("SELECT SUM(PRICE) FROM "+tableName);

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