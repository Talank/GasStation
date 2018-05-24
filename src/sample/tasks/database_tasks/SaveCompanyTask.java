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

    private int taskStatus=0;

    public SaveCompanyTask(String companyName,String creditCycle) {
        this.companyName=companyName;
        this.creditCycle=creditCycle;
    }


    @Override
    public void execute() {
        int i=0,j=0;
        boolean status;
        try {

            connection.setAutoCommit(false);

            if(taskStatus==0) {
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

                saveCompany.addBatch("INSERT INTO CreditCompany VALUES("+j+",'"+companyName+"',1,'"+SystemStatus.getCurrentDate().toString()+"',1,'"+creditCycle+"',0,0,'"+SystemStatus.getCurrentDate().toString()+"')");
                saveCompany.addBatch("CREATE TABLE "+DatabaseHandler.generateTableName(companyName,j)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");

                saveCompany.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }


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

            TaskManager.scheduleTask(this,5000);
            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }

    }
}
