package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

                PreparedStatement addCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?,?)");
                addCompany.setString(2,companyName);
                addCompany.setInt(1,j);
                addCompany.setString(4,creditCycle);
                addCompany.setInt(3,1);
                addCompany.setDate(5,SystemStatus.getCurrentDate());
                addCompany.execute();
                taskStatus++;
            }

            if(taskStatus==1) {
                Statement createTable=connection.createStatement();
                createTable.execute("CREATE TABLE "+DatabaseHandler.generateTableName(companyName,j)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");
                taskStatus++;
            }

            System.out.println("Company Saved SuccessFully.");

            final int data=j;

            Platform.runLater(()->{
                taskInterface.onComplete(data);
            });


        } catch (Exception e) {
            retryStatus++;
            System.out.println("Error Saving Company.ReScheduling Task Again. Retry:"+retryStatus);

            TaskManager.scheduleTask(this,5000);
            e.printStackTrace();
        }

    }
}
