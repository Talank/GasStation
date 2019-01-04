package np.com.spirrotech.application.home.dialogs;

import javafx.concurrent.Task;
import np.com.spirrotech.model.AdminUser;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetAdminUser extends Task<AdminUser> {

    @Override
    protected AdminUser call() throws Exception {
        AdminUser user=new AdminUser();
        try {
            Statement getUserData= Tasks.getConnection().createStatement();
            ResultSet resultSet=getUserData.executeQuery("SELECT VALUE FROM SystemStatus WHERE ID<9 AND ID>6");
            resultSet.next();
            String username=resultSet.getString(1);
            resultSet.next();
            user.initializeAdmin(username,resultSet.getString(1));
            resultSet.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
