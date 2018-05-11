package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;

public class RemoveCompanyTask extends Tasks {


    private DebtListModel model;

    public RemoveCompanyTask(DebtListModel model) {
        this.model=model;
    }

    @Override
    public void execute() {
        try {
            PreparedStatement delete=connection.prepareStatement("DELETE FROM CreditCompany WHERE ID="+model.getId());
            delete.execute();
            delete=connection.prepareStatement("DROP TABLE "+DatabaseHandler.generateTableName(model.getCompanyName(),model.getId()));
            delete.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
