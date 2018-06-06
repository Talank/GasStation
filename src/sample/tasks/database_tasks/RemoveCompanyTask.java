package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
import sample.model.DebtListModel;
import sample.model.databases.DatabaseHandler;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

            delete.close();

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

            String message="Error Removing Company '"+model.getCompanyName()+"'.\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
