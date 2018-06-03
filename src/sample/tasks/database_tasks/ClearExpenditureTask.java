package sample.tasks.database_tasks;

import sample.nepali_date.DateConverter;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ClearExpenditureTask extends Tasks {

    @Override
    public void execute() {

        try {

            NepaliDateGenerator generator=new NepaliDateGenerator();
            DateConverter converter=new DateConverter();

            Date clearDate=converter.getEnglishDate(generator.getDateFormat().getnYear(),generator.getDateFormat().getnMonth(),1);

            PreparedStatement clearExpenditure=connection.prepareStatement("DELETE FROM Expenditure WHERE DATE<?");
            clearExpenditure.setDate(1,clearDate);

            clearExpenditure.execute();
            connection.commit();


        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }


    }
}
