package np.com.spirrotech.model;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SettingsTask extends Tasks {


    private String[] settingValue;
    private int[] settingID;
    private String[] errorMsg;

    public SettingsTask(String[] settingValue, int[] settingID, String[] errorMsg) {
        this.settingValue = settingValue;
        this.settingID = settingID;
        this.errorMsg = errorMsg;
    }

    @Override
    public void execute() {

        try {
            PreparedStatement settings=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
            for (int i=0; i < settingID.length; i++) {
                settings.setString(1, settingValue[i]);
                settings.setDate(2, SystemStatus.getCurrentDate());
                settings.setInt(3, settingID[i]);
                settings.addBatch();
            }
            settings.executeBatch();
            settings.close();
            connection.commit();
            if (taskInterface != null) {
                taskInterface.onComplete(null);
            }
            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Settings Saved",1000);
            snackBarToastGenerator.show();
        } catch(SQLException e) {
            System.out.println(e.getNextException().getMessage());
            SystemLogging.getLogger().warning(errorMsg[1]);
            rollBack();
            reportError(errorMsg[0], errorMsg[1], this);
        }

    }
}
