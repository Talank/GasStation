package sample.tasks.database_tasks;

import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        /**
         * Here we first get the PREV_TRANSACTION_ID
         * if PREV_TRANSACTION_ID=1,the we know that the company credit has already been closed.
         * So we update the DUE of the company and also the DATE and make it equal to PREV_EXPIRE_DATE so,that the system doesn't list the company as expired anymore
         * Other wise,the user is pressing the PAY button without first pressing CLOSE button
         *
         * We don't check if payment is greater than due because,it's already checked in PaymentDialogController.java
         *
         */

        try {

            Statement updatePayment=connection.createStatement();

            ResultSet resultSet=updatePayment.executeQuery("SELECT PREV_TRANSACTION_ID FROM CreditCompany WHERE ID="+model.getId());
            resultSet.next();
            if(resultSet.getInt(1)==1) {

                PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE-?,PREV_TRANSACTION_ID=0"+",DATE=PREV_EXPIRE_DATE WHERE ID=?");
                creditPayment.setFloat(1,payment);
                creditPayment.setInt(2,model.getId());
                creditPayment.execute();

                creditPayment.close();

                System.out.println("Company Payment Made SuccessFully");
            } else {
                PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE-? WHERE ID=?");
                creditPayment.setFloat(1,payment);
                creditPayment.setInt(2,model.getId());
                creditPayment.execute();

                creditPayment.close();

                System.out.println("The Debt of the company has been deducted.");
            }

            resultSet.close();

            model.setDue(model.getDue()-payment);
            model.setDate(model.getPrevExpireDate());

            connection.commit();

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Payment SuccessFull",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error making company payment");

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error in Company Payment of '"+model.getCompanyName()+".\n"+"Please Retry Again.";
            String title="Company Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
