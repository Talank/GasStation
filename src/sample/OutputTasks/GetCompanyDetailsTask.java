package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.model.CreditDetailsModel;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetCompanyDetailsTask extends Task<List<List<CreditDetailsModel>>> {


    private String tableName;

    public GetCompanyDetailsTask(String tableName) {
        this.tableName=tableName;
    }

    @Override
    protected List<List<CreditDetailsModel>> call() throws Exception {
        List<List<CreditDetailsModel>> mainList=new ArrayList<>();
        int totalSize,currentSize=0;
        try {
            Statement getDetails= Tasks.getConnection().createStatement();
            ResultSet resultSet=getDetails.executeQuery("SELECT count(*) FROM "+tableName);

            resultSet.next();

            totalSize=resultSet.getInt(1);

            resultSet=getDetails.executeQuery("SELECT * FROM "+tableName);

            List<CreditDetailsModel> smallList=new ArrayList<>();
            int  i=1;
            boolean create=false;
            while(resultSet.next()) {
                if(isCancelled()) {
                    System.out.println("Task was requested to cancel. Cleaning up resources.");
                    mainList.clear();
                    break;
                } else {
                    if(i==resultSet.getInt(1)) {
                        if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {
                            smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),resultSet.getDate(7).toString(),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                        } else {
                            smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),resultSet.getDate(7).toString(),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                        }

                    } else {
                        mainList.add(smallList);
                        create=true;
                        i++;
                    }
                    if(create) {
                        smallList=new ArrayList<>();
                        if(resultSet.getString(2).startsWith("D")||resultSet.getString(2).startsWith("P")) {
                            smallList.add(new CreditDetailsModel(resultSet.getString(3),resultSet.getString(2),resultSet.getDate(7).toString(),resultSet.getString(5),resultSet.getString(6),resultSet.getString(4)));
                        } else {
                            smallList.add(new CreditDetailsModel("XXXX",resultSet.getString(2),resultSet.getDate(7).toString(),String.valueOf(resultSet.getInt(5)),resultSet.getString(6),resultSet.getString(4)));
                        }
                        create=false;
                    }

                    Thread.sleep(100);//To be removed.just for demo

                    currentSize++;
                    updateProgress(currentSize,totalSize);
                }

            }
            mainList.add(smallList);
            resultSet.close();
            getDetails.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return mainList;
    }

}
