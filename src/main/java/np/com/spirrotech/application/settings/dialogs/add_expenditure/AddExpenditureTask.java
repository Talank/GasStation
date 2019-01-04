package np.com.spirrotech.application.settings.dialogs.add_expenditure;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Adds the expenditure to the database.
 */

public class AddExpenditureTask extends Tasks {

    private String memberName,expenditureRemarks;
    private String expenditureAmount;

    public AddExpenditureTask(String memberName, String expenditureAmount, String expenditureRemarks) {
        this.memberName= memberName;
        this.expenditureAmount= expenditureAmount;
        this.expenditureRemarks= expenditureRemarks;
    }

    /**
     *Gets the expenditure data and stores it into 'Expenditure' Table.
     */

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
            SystemLogging.getLogger().fine("Expenditure stored in database");

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Expenditure Added",1000);
            snackBarToastGenerator.show();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("Expenditure error stored in database");

            rollBack();

            String message="Error Storing Expenditure For User '"+memberName+"'.\n"+"Please Retry Again.";
            String title="Expenditure Error";
            reportError(title, message, this);


        }

        /**
         * alert!
         * the data should be removed in the beginning of each month
         */

    }
}

