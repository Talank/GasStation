package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;

public class UpdateCompanyTask extends Tasks {

    private DebtListModel model;

    public UpdateCompanyTask(DebtListModel model) {
        this.model=model;
    }


    @Override
    public void execute() {
        try {
            PreparedStatement update=connection.prepareStatement("UPDATE CreditCompany SET COMPANY_NAME='"+model.getCompanyName()+"',DAY='"+model.getDays()+"' WHERE ID="+model.getId());
            update.execute();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
