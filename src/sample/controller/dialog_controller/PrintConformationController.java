package sample.controller.dialog_controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.DebtListModel;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.CompanyCreditClosingTask;
import sample.tasks.database_tasks.CompanyPaymentTask;

import java.io.IOException;

public class PrintConformationController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label remarkLabel;

    private float prepaid;

    private DebtListModel model;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Print Conformation");

        bottomBar.setButtonText("SUCCESS");

        bottomBar.setBottomBarInterface(this);

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {

        //uncomment for production
        //close the company before making payment
        TaskManager.getInstance().executeTask(new CompanyCreditClosingTask(model));

        //execute the closing task for prepaid once the printing of the bill succeeds
        if (prepaid > 0) {//we have prepaid

            //close the company using the default CompanyPaymentTask

            //we are performing payment with amount 0, as this uses the prepaid amount from the company in normal mode (i.e mode 0)
            CompanyPaymentTask paymentTask = new CompanyPaymentTask(model, 0, 0);
            paymentTask.execute();

        }

        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public void initializeData(DebtListModel model, float prepaid) {
        this.prepaid = prepaid;
        this.model = model;
    }
}
