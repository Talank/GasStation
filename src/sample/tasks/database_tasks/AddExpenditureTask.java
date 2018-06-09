package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddExpenditureTask extends Tasks {

    private String memberName,expenditureRemarks;
    private String expenditureAmount;

    public AddExpenditureTask(String memberName, String expenditureAmount, String expenditureRemarks) {
        this.memberName= memberName;
        this.expenditureAmount= expenditureAmount;
        this.expenditureRemarks= expenditureRemarks;
    }


    @Override
    public void execute() {
        try {

            PreparedStatement addExpenditure=connection.prepareStatement("INSERT INTO Expenditure VALUES (?,?,?,?)");

            addExpenditure.setString(1,memberName);
            addExpenditure.setInt(2,Integer.parseInt(expenditureAmount));
            addExpenditure.setString(3,expenditureRemarks);
            addExpenditure.setDate(4,SystemStatus.getCurrentDate());

            addExpenditure.execute();

            addExpenditure.close();

            connection.commit();

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Expenditure Added",1000);
            snackBarToastGenerator.show();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Storing Expenditure For User '"+memberName+"'.\n"+"Please Retry Again.";
            String title="Expenditure Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();


        }

        /**
         * alert!
         * the data should be removed in the beginning of each month
         */

    }
}

