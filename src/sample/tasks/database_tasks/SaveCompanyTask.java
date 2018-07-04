package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.SystemLogging;
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

/**
 *Saves the company in the system, and creates all the necessary database tables and entries.
 */

public class SaveCompanyTask extends Tasks {

    private String companyName,creditCycle;

    /**
     *
     * @param companyName the name of the company to be saved
     * @param creditCycle the cycle time of the company
     */

    public SaveCompanyTask(String companyName,String creditCycle) {
        this.companyName=companyName;
        this.creditCycle=creditCycle;
    }

    /**
     * If first searches if the company already exists, if not then the company table is created along with its default value
     */

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

            PreparedStatement insertCompany=connection.prepareStatement("INSERT INTO CreditCompany VALUES(?,?,?,?,?,?,?,?,?)");
            insertCompany.setString(1,companyName);
            insertCompany.setInt(2,1);
            insertCompany.setDate(3,SystemStatus.getCurrentDate());
            insertCompany.setInt(4,0);
            insertCompany.setString(5,creditCycle);
            insertCompany.setFloat(6,0);
            insertCompany.setFloat(7,0);
            insertCompany.setDate(8,SystemStatus.getCurrentDate());
            insertCompany.setFloat(9,0);

            insertCompany.execute();

            resultSet.close();
            insertCompany.close();
            saveCompany.close();
            getIDs.close();

            connection.commit();
            SystemLogging.getLogger().fine(" CreditCompany added successfully");

            final DebtListModel model=new DebtListModel(companyName,creditCycle,1,1,0f,0f);
            model.setDate(SystemStatus.getNepaliCurrentDate());
            model.setPrePaid(0);

            Platform.runLater(()->SettingsController.getInstance().listModels.add(model));

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Added SuccessFully",1000);
            snackBarToastGenerator.show();

            System.out.println("Company Saved SuccessFully.");


        } catch (SQLException e) {
            SystemLogging.getLogger().warning("CreditCompany failed to save");

            rollBack();

            String message="Error Saving Company'"+companyName+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            reportError(title, message, this);

            e.printStackTrace();
            System.out.println(e.getNextException().getMessage());
        }

    }
}
