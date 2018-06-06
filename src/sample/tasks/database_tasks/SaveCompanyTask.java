package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.library.ErrorMessageDialogGenerator;
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
            saveCompany.execute("CREATE TABLE "+DatabaseHandler.generateTableName(companyName,j)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");

            PreparedStatement insertCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?,?,?,?,?,?)");
            insertCompany.setInt(1,j);
            insertCompany.setString(2,companyName);
            insertCompany.setInt(3,1);
            insertCompany.setDate(4,SystemStatus.getCurrentDate());
            insertCompany.setInt(5,0);
            insertCompany.setString(6,creditCycle);
            insertCompany.setFloat(7,0);
            insertCompany.setFloat(8,0);
            insertCompany.setDate(9,SystemStatus.getCurrentDate());

            insertCompany.execute();

            resultSet.close();
            insertCompany.close();
            saveCompany.close();
            getIDs.close();

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
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Saving Company'"+companyName+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();

            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }

    }
}
