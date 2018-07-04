package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.nepali_date.DateConverter;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *Clears the expenditure upto 1st day of current month.
 */

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

            clearExpenditure.close();

            connection.commit();
            SystemLogging.getLogger().fine("Expenditure successfully cleared");

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Expenditure Cleared",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("Expenditure failed to clear");
            System.out.println(e.getMessage());

            rollBack();

            String message="Error Clearing Expenditure.\n"+"Please Retry Again.";
            String title="Expenditure Error";
            reportError(title, message, this);
        }


    }
}
