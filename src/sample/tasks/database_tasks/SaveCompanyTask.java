package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.main_home_controller.SettingsController;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.DebtListModel;
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
        try {
            String lowerCompanyName=companyName.toLowerCase();
            Statement getIDs=connection.createStatement();
            ResultSet resultSet=getIDs.executeQuery("SELECT * FROM CreditCompany WHERE LOWER(COMPANY_NAME)='"+lowerCompanyName+"'");
            if(resultSet.next()) {
                SnackBarToastGenerator generator=new SnackBarToastGenerator("Company Already Exists",2000);
                generator.show();
                return;
            }

            Statement saveCompany=connection.createStatement();
            saveCompany.execute("CREATE TABLE "+DatabaseHandler.generateTableName(companyName)+" (TRANSACTION_ID INT NOT NULL,TYPE VARCHAR(10) NOT NULL,VEHICLE_NO VARCHAR(10) NOT NULL,RATE FLOAT NOT NULL,VOLUME FLOAT NOT NULL,PRICE FLOAT NOT NULL,DATE DATE NOT NULL)");

            PreparedStatement insertCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?,?,?,?,?)");
            insertCompany.setString(1,companyName);
            insertCompany.setInt(2,1);
            insertCompany.setDate(3,SystemStatus.getCurrentDate());
            insertCompany.setInt(4,0);
            insertCompany.setString(5,creditCycle);
            insertCompany.setFloat(6,0);
            insertCompany.setFloat(7,0);
            insertCompany.setDate(8,SystemStatus.getCurrentDate());

            insertCompany.execute();

            resultSet.close();
            insertCompany.close();
            saveCompany.close();
            getIDs.close();

            connection.commit();

            Platform.runLater(()->SettingsController.getInstance().listModels.add(new DebtListModel(companyName,creditCycle,1,1,0f,0f)));

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Added SuccessFully",1000);
            snackBarToastGenerator.show();

            System.out.println("Company Saved SuccessFully.");


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
