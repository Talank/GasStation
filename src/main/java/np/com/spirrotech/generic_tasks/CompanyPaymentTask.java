package np.com.spirrotech.generic_tasks;

import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.model.DebtListModel;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *Payment to company by deducting in due. The task is completed and the settings controller is updated
 */

public class CompanyPaymentTask extends Tasks {

    private DebtListModel model;

    private float payment;

    private int mode;


    /**
     *
     * @param model the company whose payment is to be done
     * @param payment the amount to be paid
     * @param mode mode = 0 (Normal payment) and mode = 1 (Prepaid payment)
     */

    public CompanyPaymentTask(DebtListModel model, float payment,int mode) {
        this.model=model;
        this.payment=payment;
        this.mode=mode;
    }

    @Override
    public void execute() {

        /*
         * Here we first get the PREV_TRANSACTION_ID
         * if PREV_TRANSACTION_ID=1,the we know that the company credit has already been closed.
         * So we update the DUE of the company and also the DATE and make it equal to PREV_EXPIRE_DATE so,that the system doesn't list the company as expired anymore
         * Other wise,the user is pressing the PAY button without first pressing CLOSE button
         *
         * We don't check if payment is greater than due because,it's already checked in PaymentDialogController.java
         *
         */

        try {


            float prepaid,due;
            if(mode==0) {
                Statement updatePayment=connection.createStatement();

                ResultSet resultSet=updatePayment.executeQuery("SELECT PREV_TRANSACTION_ID,DUE,PRE_PAID FROM CreditCompany WHERE COMPANY_NAME='"+model.getCompanyName()+"'");
                resultSet.next();

                float netAmount=(payment+resultSet.getFloat(3))-resultSet.getFloat(2);

                if(resultSet.getInt(1)==1) {

                    //The company is closed

                    if(netAmount<0) {
                        /*
                         *The payment and prepaid doesn't pays off the due
                         * so,we set prepaid =0,as it is totally used and set due=netAmount
                         * we also set the Date of the company to prev_transaction_date which is the date that the company expired, and is either 1st day of month or 15th of month
                         */
                        due=Math.abs(netAmount);
                        prepaid=0;
                        PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET DUE=?,PRE_PAID=0,PREV_TRANSACTION_ID=0"+",DATE=PREV_EXPIRE_DATE WHERE COMPANY_NAME=?");
                        creditPayment.setFloat(1,due);
                        creditPayment.setString(2,model.getCompanyName());

                        creditPayment.execute();

                        creditPayment.close();
                    } else {

                        //The due is completely paid off and the extra amount is assigned to 'prepaid'

                        due=0;
                        prepaid=netAmount;
                        PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET DUE=0,PRE_PAID=?,PREV_TRANSACTION_ID=0"+",DATE=PREV_EXPIRE_DATE WHERE COMPANY_NAME=?");
                        creditPayment.setFloat(1,netAmount);
                        creditPayment.setString(2,model.getCompanyName());

                        creditPayment.execute();

                        creditPayment.close();
                    }

                    System.out.println("Company Payment Made SuccessFully");
                } else {

                    //The company is not closed so, we only deduct the due of the company

                    due=resultSet.getFloat(2)-payment;
                    prepaid=model.getPrePaid();
                    PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET DUE=DUE-? WHERE COMPANY_NAME=?");
                    creditPayment.setFloat(1,payment);
                    creditPayment.setString(2,model.getCompanyName());
                    creditPayment.execute();

                    creditPayment.close();

                    System.out.println("The Debt of the company has been deducted.");
                }

                //update the model and mark the company as not expired, the also applies when the company has prepaid and the company is closed
                model.setDue(due);
                model.setDate(model.getPrevExpireDate());
                model.setExpired(false);
                model.setPrePaid(prepaid);


                resultSet.close();
            } else {
                /*
                 *The use only wants to do prepaid so, update the prepaid
                 */
                prepaid=model.getPrePaid()+payment;
                PreparedStatement creditPayment=connection.prepareStatement("UPDATE CreditCompany SET PRE_PAID=PRE_PAID+?"+" WHERE COMPANY_NAME=?");
                creditPayment.setFloat(1,payment);
                creditPayment.setString(2,model.getCompanyName());
                creditPayment.execute();

                //update the company prepaid, the company is still expired
                model.setPrePaid(prepaid);

                creditPayment.close();
            }


            connection.commit();
            SystemLogging.getLogger().fine("CreditCompany payment successfully done ");


            SettingsController.getInstance().refreshList(model);

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Company Payment SuccessFull",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("CreditCompany payment failed ");
            System.out.println("Error making company payment");

            rollBack();

            String message="Error in Company Payment of '"+model.getCompanyName()+".\n"+"Please Retry Again.";
            String title="Company Error";
            reportError(title, message, this);
        }
    }
}
