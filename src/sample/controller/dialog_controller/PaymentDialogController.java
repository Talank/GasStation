package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.DebtListModel;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.CompanyPaymentTask;

import java.io.IOException;

public class PaymentDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private JFXTextField payment;

    private DebtListModel model;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Payment");

        bottomBar.setBottomBarInterface(this);
        bottomBar.setButtonText("PAY");

        RequiredFieldValidator paymentValidator=new RequiredFieldValidator();
        paymentValidator.setMessage("Payment Cannot Be Empty");
        payment.getValidators().add(paymentValidator);


        payment.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}")) {
                payment.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    payment.validate();
                } else {
                    payment.resetValidation();
                }
            }
        });


    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        if(!payment.getText().isEmpty()) {
            CompanyPaymentTask task=new CompanyPaymentTask(model,Float.parseFloat(payment.getText()));
            TaskManager.executeTask(task);

            ((Node)e.getSource()).getScene().getWindow().hide();

        } else {
            payment.validate();
        }
    }

    public void setModel(DebtListModel model) {
        this.model=model;
    }
}
