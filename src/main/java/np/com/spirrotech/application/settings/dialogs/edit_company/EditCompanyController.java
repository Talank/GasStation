package np.com.spirrotech.application.settings.dialogs.edit_company;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.model.UpdateCompanyTask;

import java.io.IOException;


public class EditCompanyController implements BottomBarInterface {

    @FXML
    private JFXTextField creditCycle,phone;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private DialogToolBar toolBar;

    private DebtListModel model;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Edit Company");

        bottomBar.setBottomBarInterface(this);
        bottomBar.setButtonText("SAVE");


        RequiredFieldValidator cycleValidator=new RequiredFieldValidator();
        cycleValidator.setMessage("Cycle Time Cannot Be Empty");
        creditCycle.getValidators().add(cycleValidator);

        RequiredFieldValidator phoneValidator=new RequiredFieldValidator();
        phoneValidator.setMessage("Phone numbers cannot be Empty");
        phone.getValidators().add(phoneValidator);


        creditCycle.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,2}")) {
                creditCycle.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    creditCycle.validate();
                } else {
                    creditCycle.resetValidation();
                }
            }
        });


        phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}")) {
                phone.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    phone.validate();
                } else {
                    phone.resetValidation();
                }
            }
        });

    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        int status=0;


        if(creditCycle.getText().trim().isEmpty()) {
            creditCycle.validate();
            status++;
        }


        if(phone.getText().trim().isEmpty()){
            phone.validate();
            status++;
        }

        if(status==0) {

            model.setDays(creditCycle.getText());
            model.setPhone(phone.getText());
            UpdateCompanyTask task=new UpdateCompanyTask(model);
            TaskManager.getInstance().executeTask(task);

            ((Node)e.getSource()).getScene().getWindow().hide();
        }
    }



    public void setEditData(DebtListModel model) {
        creditCycle.setText(model.getDays());
        phone.setText(model.getPhone());
        this.model=model;
    }

}
