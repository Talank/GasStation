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

/**
 *Clears the daily analytics up to current month 1st day
 */

public class ClearDailyAnalyticsTask extends Tasks {



    @Override
    public void execute() {
        try {
            NepaliDateGenerator generator=new NepaliDateGenerator();
            DateConverter converter=new DateConverter();

            Date clearDate=converter.getEnglishDate(generator.getDateFormat().getnYear(),generator.getDateFormat().getnMonth(),1);

            PreparedStatement clearPetrolRefill=connection.prepareStatement("DELETE FROM DailyAnalytics WHERE DATE<?");
            clearPetrolRefill.setDate(1,clearDate);

            clearPetrolRefill.execute();

            clearPetrolRefill.close();

            connection.commit();
            SystemLogging.getLogger().fine("DailyAnalytics successfully clears the records in database");

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Daily Analytics Cleared",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("DailyAnalytics failed to clear the records");

            rollBack();

            String message="Error Clearing Daily Analytics.\n"+"Please Retry Again.";
            String title="Daily Analytics Error";
            reportError(title, message, this);
        }
    }
}
