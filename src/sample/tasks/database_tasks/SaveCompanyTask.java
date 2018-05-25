package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.sql.*;

public class SaveCompanyTask extends Tasks {

    private String companyName,creditCycle;

    public SaveCompanyTask(String companyName,String creditCycle) {
        this.companyName=companyName;
        this.creditCycle=creditCycle;
    }


    @Override
    public void execute() {
        int i=0,j=0;
        boolean status;
        try {
            Statement getIDs=connection.createStatement();
            ResultSet resultSet=getIDs.executeQuery("SELECT COUNT(*) FROM CreditCompany");
            resultSet.next();
            int idArray[]=new int[resultSet.getInt(1)];
            resultSet.close();

            resultSet=getIDs.executeQuery("SELECT ID FROM CreditCompany");

            while(resultSet.next())
                idArray[i++]=resultSet.getInt(1);
            j=1;


            while(true) {
                status=false;
                for(i=0;i<idArray.length;i++) {
                    if(j==idArray[i]) {
                        status=true;
                        j++;
                        break;
                    }
                }
                if(!status)
                    break;
            }

            Statement saveCompany=connection.createStatement();

            saveCompany.addBatch("INSERT INTO CreditCompany VALUES("+j+",'"+companyName+"',1,'"+SystemStatus.getCurrentDate().toString()+"',0,'"+creditCycle+"',0,0,'"+SystemStatus.getCurrentDate().toString()+"')");
            saveCompany.addBatch("CREATE TABLE "+DatabaseHandler.generateTableName(companyName,j)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");

            saveCompany.executeBatch();
            connection.commit();

            System.out.println("Company Saved SuccessFully.");

            final int data=j;

            Platform.runLater(()->{
                if(taskInterface!=null) {
                    taskInterface.onComplete(data);
                }

            });


        } catch (SQLException e) {
            retryStatus++;
            System.out.println("Error Saving Company.ReScheduling Task Again. Retry:"+retryStatus);

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            TaskManager.scheduleTask(this,5000);
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }

    }
}
