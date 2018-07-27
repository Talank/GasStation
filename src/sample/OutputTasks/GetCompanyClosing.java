package sample.OutputTasks;

import sample.model.CreditCompanyClosingBill;
import sample.model.DebtListModel;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String tableName = model.getCompanyName();
        tableName = tableName.replace(" ","_");

        Statement getData = Tasks.getConnection().createStatement();
        ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName);
        while (resultSet.next()){
            //builder.append(resultSet.getString(1)+"\t\t");
            builder.append(resultSet.getString(2)+"  \t");
            builder.append(resultSet.getString(3)+"         \t");
            builder.append(resultSet.getString(4)+"   \t");
            builder.append(resultSet.getString(5)+"     \t");
            builder.append(resultSet.getString(6)+"  \t");
            builder.append(resultSet.getString(7)+"\n");
        }

        Statement getTotal = Tasks.getConnection().createStatement();
        ResultSet rs = getTotal.executeQuery("SELECT SUM(PRICE) FROM "+tableName);

        rs.next();
        total = (rs.getString(1) != null)? Double.parseDouble(rs.getString(1)): 0;

        tableName = tableName.replace("_"," ");
        ResultSet rs1 = getTotal.executeQuery("SELECT DUE FROM CREDITCOMPANY WHERE COMPANY_NAME='"+tableName+"'");

        if(rs1.next()) {
            due = Double.parseDouble(rs1.getString(1));
        }

        grandTotal = due+total;
        return builder;
    }
}