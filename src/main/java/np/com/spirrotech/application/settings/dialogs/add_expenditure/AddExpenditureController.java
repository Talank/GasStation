package np.com.spirrotech.application.settings.dialogs.add_expenditure;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;


import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;


import np.com.spirrotech.utils.tasks.TaskManager;


import java.io.IOException;

public class AddExpenditureController implements BottomBarInterface {

    @FXML
    private JFXTextField memberName,expenditureAmount,expenditureRemarks;


    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Add Expenditure");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator memberNameValidator=new RequiredFieldValidator();
        memberNameValidator.setMessage("Member Name Cannot Be Empty");
        memberName.getValidators().add(memberNameValidator);

        RequiredFieldValidator expenditureAmountValidator=new RequiredFieldValidator();
        expenditureAmountValidator.setMessage("Amount Cannot Be Empty");
        expenditureAmount.getValidators().add(expenditureAmountValidator);

        RequiredFieldValidator expenditureRemarksValidator=new RequiredFieldValidator();
        expenditureRemarksValidator.setMessage("Remarks Cannot Be Empty");
        expenditureRemarks.getValidators().add(expenditureRemarksValidator);

        memberName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.trim().isEmpty()) {
                memberName.validate();
            } else {
                memberName.resetValidation();
            }
        });

        expenditureAmount.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}")) {
                expenditureAmount.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    expenditureAmount.validate();
                } else {
                    expenditureAmount.resetValidation();
                }
            }
        });

        expenditureRemarks.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(newValue.trim().isEmpty()) {
                expenditureRemarks.validate();
            } else {
                expenditureRemarks.resetValidation();
            }
        });


    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        int status=0;
        if(memberName.getText().trim().isEmpty()) {
            memberName.validate();
            status++;
        }

        if(expenditureAmount.getText().trim().isEmpty()) {
            expenditureAmount.validate();
            status++;
        }

        if(expenditureRemarks.getText().trim().isEmpty()) {
            expenditureRemarks.validate();
            status++;
        }

        if(status==0) {

            AddExpenditureTask task=new AddExpenditureTask(memberName.getText().trim(),expenditureAmount.getText().trim(),expenditureRemarks.getText().trim());
            TaskManager.getInstance().executeTask(task);

            ((Node)e.getSource()).getScene().getWindow().hide();

        }
    }

}
