package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
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
            Statement addExpenditure=connection.createStatement();

            /**
             * Query to
             * add 3 fields+ SystemStatus.getCurrentDate()  in a table Expenditure
             */

            addExpenditure.execute("INSERT INTO Expenditure VALUES ('"+memberName+"',"+expenditureAmount+",'"+expenditureRemarks+"','"+SystemStatus.getCurrentDate()+"')");
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }

        /**
         * alert!
         * the data should be removed in the beginning of each month
         */

    }
}

