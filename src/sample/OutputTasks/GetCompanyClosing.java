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
    private String total;

    public GetCompanyClosing(DebtListModel model){
        this.model = model;
    }

    public String getTotalPrice(){
        return total;
    }

    StringBuilder builder=new StringBuilder();

    public StringBuilder call() throws SQLException {
        String tableName = model.getCompanyName();

            Statement getData = Tasks.getConnection().createStatement();
            ResultSet resultSet = getData.executeQuery("SELECT * FROM "+tableName);
            while (resultSet.next()){
                //builder.append(resultSet.getString(1)+"\t\t");
                builder.append(resultSet.getString(2)+"  \t");
                builder.append(resultSet.getString(3)+"         \t");
                builder.append(resultSet.getString(4)+"   \t");
                builder.append(resultSet.getString(5)+"  \t");
                builder.append(resultSet.getString(6)+"  \t");
                builder.append(resultSet.getString(7)+"\n");
            }

        Statement getTotal = Tasks.getConnection().createStatement();
        ResultSet rs = getData.executeQuery("SELECT SUM(PRICE) FROM "+tableName);
        rs.next();
        total= String.valueOf(rs.getString(1));

        return builder;
    }


}
