package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
import sample.model.DebtListModel;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCompanyTask extends Tasks {

    private DebtListModel model;

    public UpdateCompanyTask(DebtListModel model) {
        this.model=model;
    }


    @Override
    public void execute() {
        try {


            PreparedStatement update=connection.prepareStatement("UPDATE CreditCompany SET COMPANY_NAME=?,DAY=? WHERE ID=?");
            update.setString(1,model.getCompanyName());
            update.setString(2,model.getDays());
            update.setInt(3,model.getId());

            update.execute();

            update.close();

            connection.commit();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
            String message="Error Updating Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }

    }
}
