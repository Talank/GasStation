package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.model.SystemStatus;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.io.IOException;
import java.sql.Date;
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

            connection.setAutoCommit(false);
            Statement closing=connection.createStatement();
            closing.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getfDiesel())+"',DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=11");
            closing.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getfPetrol())+"',DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=12");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=1");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=2");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=3");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=4");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=5");
            closing.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=6");
            closing.addBatch("INSERT INTO DailyAnalytics VALUES ("+Float.parseFloat(model.getIDieselVol())
                    +","+Float.parseFloat(model.getIPetrolVol())+","+Float.parseFloat(model.getCDieselVol())+","
                    +Float.parseFloat(model.getCPetrolVol())+","+Float.parseFloat(model.getIExtra())+","+
                    Float.parseFloat(model.getCExtra())+","+Float.parseFloat(model.getOilIncome())+","+
                    SystemStatus.getdRate()+","+SystemStatus.getpRate()+",'"+SystemStatus.getCurrentDate()+"')");

            closing.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

            SystemStatus.setfDiesel(0f);
            SystemStatus.setfPetrol(0f);

            LocalDate tomorrow=LocalDate.now().plusDays(1);
            SystemStatus.setTransactionDate((new NepaliDateGenerator(tomorrow)).toString());

            SystemLogging.getLogger().fine("Closing Successfully Done. Next Transaction Stored on Date:"+SystemStatus.getTransactionDate());
            System.out.println("Closing Successfully Done. Next Transaction Stored on Date:"+SystemStatus.getTransactionDate());


        } catch (SQLException e) {
            retryStatus++;

            SystemLogging.getLogger().severe("Couldn't Perform Today Closing.Rescheduling Again.Retry Counter:"+retryStatus);

            System.out.println("Couldn't Perform Today Closing.Rescheduling Again.Retry Counter:"+retryStatus);
            TaskManager.scheduleTask(this,5000);


            System.out.println(e.getNextException().getMessage());
        }


    }
}
