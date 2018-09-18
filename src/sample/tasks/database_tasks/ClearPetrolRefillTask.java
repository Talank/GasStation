package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.nepali_date.DateConverter;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @deprecated
 *Clears petrol refill up to 1st day of current month.
 */

public class ClearPetrolRefillTask extends Tasks {

    @Override
    public void execute() {

        try {
            NepaliDateGenerator generator=new NepaliDateGenerator();
            DateConverter converter=new DateConverter();

            Date clearDate=converter.getEnglishDate(generator.getDateFormat().getnYear(),generator.getDateFormat().getnMonth(),1);

            PreparedStatement clearPetrolRefill=connection.prepareStatement("DELETE FROM PetrolRefill WHERE DATE<?");
            clearPetrolRefill.setDate(1,clearDate);

            clearPetrolRefill.execute();

            clearPetrolRefill.close();

            connection.commit();
            SystemLogging.getLogger().fine("PetrolRefill successfully cleared");

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Petrol Refill Cleared",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("PetrolRefill failed to clear");
            System.out.println(e.getMessage());

            rollBack();

            String message="Error Clearing Petrol Refill.\n"+"Please Retry Again.";
            String title="Petrol Refill Error";
            reportError(title, message, this);
        }


    }
}
