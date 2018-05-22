package sample.tasks.database_tasks;

import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.SQLException;
import java.sql.Statement;

public class CompanyPaymentTask extends Tasks {

    private DebtListModel model;

    private float payment;

    public CompanyPaymentTask(DebtListModel model, float payment) {
        this.model=model;
        this.payment=payment;
    }

    @Override
    public void execute() {
        try {

            Statement updatePayment=connection.createStatement();
            updatePayment.execute("UPDATE CreditCompany SET DUE=DUE-"+payment+" WHERE ID="+model.getId());

            model.setDue(model.getDue()-payment);

            System.out.println("Company Payment Made SuccessFully");

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error making company payment");
        }
    }
}
