package np.com.spirrotech.model;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.utils.nepali_date.DateConverter;
import np.com.spirrotech.utils.nepali_date.NepaliDateGenerator;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearTableTask extends Tasks {

    private String tableName;
    private String[] errorMsg;

    public ClearTableTask(String tableName, String[] errorMsg) {
        this.tableName = tableName;
        this.errorMsg = errorMsg;
    }

    @Override
    public void execute() {
        try {
            NepaliDateGenerator generator=new NepaliDateGenerator();
            DateConverter converter=new DateConverter();

            Date clearDate=converter.getEnglishDate(generator.getDateFormat().getnYear(),generator.getDateFormat().getnMonth(),1);

            PreparedStatement clearPetrolRefill=connection.prepareStatement("DELETE FROM " + tableName + " WHERE DATE<?");
            clearPetrolRefill.setDate(1,clearDate);

            clearPetrolRefill.execute();

            clearPetrolRefill.close();

            connection.commit();

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Data Cleared",2000);
            snackBarToastGenerator.show();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning(tableName + ": failed to clear the records");

            rollBack();

            reportError(errorMsg[0], errorMsg[1], this);
        }
    }
}
