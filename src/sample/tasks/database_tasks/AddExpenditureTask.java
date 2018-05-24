package sample.tasks.database_tasks;

import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * alert!
         * the data should be removed in the beginning of each month
         */

    }
}

