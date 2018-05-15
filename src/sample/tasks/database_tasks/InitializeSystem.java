package sample.tasks.database_tasks;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import sample.controller.SystemLogging;
import sample.model.*;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InitializeSystem extends Tasks {

    @Override
    public void execute() {

        SystemLogging.initializeLogger();//Initialize The Logger
        SystemStatus.setMainHomePath("../../view/main_home_views/mainHome.fxml");

        SystemStatus.setCurrentDate(Date.valueOf((new NepaliDateGenerator()).getConvertedNepaliDate()));
        SystemStatus.setTransactionDate(Date.valueOf((new NepaliDateGenerator()).getConvertedNepaliDate()));



        LocalTime time=LocalTime.now();
        int hourDiff=17-time.getHour();

        if(hourDiff<0) {
            System.out.println("System Restart After 5 PM. Not Scheduling Task.");
        } else {
            final long delay=(hourDiff*3600)+time.getMinute()*60+time.getSecond();

            System.out.println("Hour Delay:"+hourDiff);


            Thread scheduleThread= new Thread(()->{
                try {

                    System.out.println("Total Delay:"+delay*1000);

                    Thread.sleep(delay*1000);

                    Platform.runLater(()->{

                        try {
                            Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/finalReadings.fxml"));
                            Stage stage=new Stage();
                            stage.setScene(new Scene(root));
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });

                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

            });
            scheduleThread.setDaemon(true);
            scheduleThread.start();

        }


        System.out.println(time);





        ArrayList<DebtListModel> arrayList=new ArrayList<>();
        try {
            Statement getDebitList=connection.createStatement();
            ResultSet resultSet=getDebitList.executeQuery("SELECT * FROM CreditCompany");
            while(resultSet.next()) {
                arrayList.add(new DebtListModel(resultSet.getString(2),resultSet.getDate(5).toString(),resultSet.getString(4),resultSet.getInt(1),resultSet.getInt(3)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        CreditCompanyList.setCompanyList(arrayList);

        try {
            Statement getValues=connection.createStatement();
            ResultSet resultSet=getValues.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>10 AND ID<16");

            resultSet.next();
            SystemStatus.setiDiesel(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiPetrol(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setiCounter(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setDieselStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.next();
            SystemStatus.setPetrolStock(Float.parseFloat(resultSet.getString(1)));
            resultSet.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        //This code is moved to Today Closing task as it is more Feasible to reset system when Closing is done rather than at start up

        /*try {

            connection.setAutoCommit(false);
            Statement resetSystem=connection.createStatement();
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=1");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=2");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=3");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=4");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=5");
            resetSystem.addBatch("UPDATE SystemStatus SET VALUE='0' WHERE ID=6");

            resetSystem.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }*/

        try {
            Statement getRate=connection.createStatement();
            ResultSet resultSet=getRate.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID>8 AND ID<11");
            resultSet.next();

            SystemStatus.setdRate((Float.parseFloat(resultSet.getString(1))));
            resultSet.next();
            SystemStatus.setpRate(Float.parseFloat(resultSet.getString(1)));
            resultSet.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
