package np.com.spirrotech.application.settings.dialogs.company_payment;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.dao.DebtListModel;

import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.model.CompanyPaymentTask;

import java.io.IOException;

public class PaymentDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private JFXRadioButton normalPayment;

    @FXML
    private ToggleGroup selection;

    @FXML
    private JFXRadioButton prepaidPayment;

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

        selection.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)->{
            if(((JFXRadioButton)new_toggle).getText().equals("Normal")) {
                prepaidPayment.setSelected(false);
                normalPayment.setSelected(true);
            } else {
                normalPayment.setSelected(false);
                prepaidPayment.setSelected(true);
            }
        });


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

            if(((JFXRadioButton)selection.getSelectedToggle()).getText().equals("Normal")) {
                if(Float.parseFloat(payment.getText())<=model.getDue()) {
                    CompanyPaymentTask task=new CompanyPaymentTask(model,Float.parseFloat(payment.getText()),0);
                    TaskManager.getInstance().executeTask(task);
                    ((Node)e.getSource()).getScene().getWindow().hide();
                } else {
                    bottomBar.setErrorText("Payment Greater Than Due Amount.");
                }
            } else {
                CompanyPaymentTask task=new CompanyPaymentTask(model,Float.parseFloat(payment.getText()),1);
                TaskManager.getInstance().executeTask(task);
                ((Node)e.getSource()).getScene().getWindow().hide();
            }




        } else {
            payment.validate();
        }
    }

    public void setModel(DebtListModel model) {
        this.model=model;
    }
}
