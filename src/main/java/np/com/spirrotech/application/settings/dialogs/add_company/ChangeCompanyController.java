package np.com.spirrotech.application.settings.dialogs.add_company;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;

import javafx.scene.input.MouseEvent;

import np.com.spirrotech.model.SaveCompanyTask;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;

import np.com.spirrotech.dao.DebtListModel;

import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.generic_tasks.UpdateCompanyTask;

import java.io.IOException;


public class ChangeCompanyController implements BottomBarInterface {

    @FXML
    private JFXTextField companyName,creditCycle,phone,due;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private DialogToolBar toolBar;

    private boolean isEditable=false;

    private DebtListModel model;

    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Add New Company");

        bottomBar.setBottomBarInterface(this);
        bottomBar.setButtonText("SAVE");

        RequiredFieldValidator nameValidator=new RequiredFieldValidator();
        nameValidator.setMessage("Company Name Cannot Be Empty");
        companyName.getValidators().add(nameValidator);

        RequiredFieldValidator cycleValidator=new RequiredFieldValidator();
        cycleValidator.setMessage("Cycle Time Cannot Be Empty");
        creditCycle.getValidators().add(cycleValidator);

        RequiredFieldValidator dueValidator=new RequiredFieldValidator();
        dueValidator.setMessage("Due Cannot Be Empty");
        due.getValidators().add(dueValidator);

        RequiredFieldValidator phoneValidator=new RequiredFieldValidator();
        phoneValidator.setMessage("Phone numbers cannot be Empty");
        phone.getValidators().add(phoneValidator);

        companyName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[a-zA-Z ]*")) {
                companyName.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    companyName.validate();
                } else {
                    companyName.resetValidation();
                }
            }
        });

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

        due.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,9}([\\.][0-9]{0,2})?")) {
                due.setText(oldValue);
            } else {
                if(newValue.isEmpty()) {
                    due.validate();
                } else {
                    due.resetValidation();
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
        if(companyName.getText().trim().isEmpty()) {
            companyName.validate();
            status++;
        }


        if(creditCycle.getText().trim().isEmpty()) {
            creditCycle.validate();
            status++;
        }

        if(due.getText().trim().isEmpty()) {
            due.validate();
            status++;
        }

        if(phone.getText().trim().isEmpty()){
            phone.validate();
            status++;
        }

        if(status==0) {
            if(!isEditable) {

                SaveCompanyTask task=new SaveCompanyTask(companyName.getText().trim(),creditCycle.getText(),phone.getText(),Float.parseFloat(due.getText()));
                task.addTaskInterfaceListener(new TaskInterface<Integer>() {
                    @Override
                    public void onComplete(Integer id) {
                        if(id!=-1) {
                            SystemLogging.getLogger().fine("Company Has Been Added.");


                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                TaskManager.getInstance().executeTask(task);
                ((Node)e.getSource()).getScene().getWindow().hide();

            } else {
                model.setCompanyName(companyName.getText().trim());
                model.setDays(creditCycle.getText());
                model.setDue(Float.parseFloat(due.getText()));
                model.setPhone(phone.getText());
                UpdateCompanyTask task=new UpdateCompanyTask(model);
                TaskManager.getInstance().executeTask(task);

                ((Node)e.getSource()).getScene().getWindow().hide();

            }
        }
    }



    public void setEditData(DebtListModel model) {
        toolBar.setDialogTitle("Edit Company");
        isEditable=true;
        this.companyName.setText(model.getCompanyName());
        this.companyName.setVisible(false);
        this.creditCycle.setText(model.getDays());
        this.phone.setText(model.getPhone());
        this.model=model;
    }

}
