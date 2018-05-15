package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreditCompanyClosing extends Tasks {

    private DebtListModel model;

    public CreditCompanyClosing(DebtListModel model) {
        this.model=model;
    }

    @Override
    public void execute() {
        try {

            PreparedStatement updateCompany=connection.prepareStatement("UPDATE CreditCompany SET DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID="+model.getId());
            updateCompany.execute();


            updateCompany=connection.prepareStatement("DELETE FROM "+DatabaseHandler.generateTableName(model.getCompanyName(),model.getId()));
            updateCompany.execute();

        } catch(SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }


    }
}
