package sample.OutputTasks;

import javafx.concurrent.Task;
import sample.controller.main_home_controller.expenditure_list_controller.ExpenditureListCell;
import sample.model.ExpenditureModel;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GetExpenditureTask extends Task<ArrayList<ExpenditureModel>> {
    @Override
    protected ArrayList<ExpenditureModel> call() throws Exception {
        int totalSize,currentSize=0;
        ArrayList<ExpenditureModel> expenditureModels=new ArrayList<>();

        try {

            Statement getTaskCount= Tasks.getConnection().createStatement();
            ResultSet resultSet=getTaskCount.executeQuery("SELECT COUNT(*) FROM Expenditure");
            resultSet.next();
            totalSize=resultSet.getInt(1);

            NepaliDateGenerator generator=new NepaliDateGenerator();

            resultSet=getTaskCount.executeQuery("SELECT * FROM Expenditure");

            ExpenditureModel model;

            while(resultSet.next()) {

                if(isCancelled()) {
                    expenditureModels.clear();
                    break;
                }

                model=new ExpenditureModel(resultSet.getString(1),resultSet.getString(3),resultSet.getString(2),generator.convertDate(resultSet.getDate(4)));
                expenditureModels.add(model);
                currentSize++;
                updateProgress(currentSize,totalSize);
            }

            resultSet.close();

            getTaskCount.close();


        } catch(SQLException e) {
            e.printStackTrace();
        }



        return expenditureModels;
    }
}
