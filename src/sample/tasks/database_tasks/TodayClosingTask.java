package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;


public class TodayClosingTask extends Tasks {


    private DailyTotalAnalyticModel model;

    public TodayClosingTask(DailyTotalAnalyticModel model) {
        this.model=model;
    }

    @Override
    public void execute() {

        try {

            PreparedStatement closing=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");

            closing.setString(1,String.valueOf(SystemStatus.getfDiesel()));
            closing.setDate(2,SystemStatus.getCurrentDate());
            closing.setInt(3,11);
            closing.addBatch();

            closing.setString(1,String.valueOf(SystemStatus.getfPetrol()));
            closing.setInt(3,12);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,1);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,2);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,3);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,4);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,5);
            closing.addBatch();

            closing.setString(1,String.valueOf(0));
            closing.setInt(3,6);
            closing.addBatch();

            closing.executeBatch();

            closing=connection.prepareStatement("INSERT INTO DailyAnalytics VALUES (?,?,?,?,?,?,?,?)");
            closing.setFloat(1,Float.parseFloat(model.getIDieselVol()));
            closing.setFloat(2,Float.parseFloat(model.getIPetrolVol()));
            closing.setFloat(3,Float.parseFloat(model.getCDieselVol()));
            closing.setFloat(4,Float.parseFloat(model.getCPetrolVol()));
            closing.setFloat(5,Float.parseFloat(model.getCExtra()));
            closing.setFloat(6,SystemStatus.getdRate());
            closing.setFloat(7,SystemStatus.getpRate());
            closing.setDate(8,SystemStatus.getCurrentDate());

            closing.execute();

            closing.close();

            connection.commit();

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Today Closing SuccessFull",1000);
            snackBarToastGenerator.show();

            SystemStatus.setfDiesel(0f);
            SystemStatus.setfPetrol(0f);

            LocalDate tomorrow=LocalDate.now().plusDays(1);
            SystemStatus.setTransactionDate(Date.valueOf(tomorrow));
            SystemStatus.setNepaliTransactionDate((new NepaliDateGenerator(tomorrow)).toString());

            SystemLogging.getLogger().fine("Closing Successfully Done. Next Transaction Stored on Date:"+SystemStatus.getTransactionDate());
            System.out.println("Closing Successfully Done. Next Transaction Stored on Date:"+SystemStatus.getTransactionDate());


        } catch (SQLException e) {

            SystemLogging.getLogger().severe("Couldn't Perform Today Closing.Rescheduling Again.Retry Counter:"+retryStatus);

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Closing.\n"+"Please Retry Again.";
            String title="System Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();



            System.out.println(e.getNextException().getMessage());
        }


    }
}
