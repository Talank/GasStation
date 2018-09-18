package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.OutputTasks.GetSettingsTask;
import sample.controller.main_home_controller.SettingsController;
import sample.controller.SystemLogging;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.databases.DatabaseHandler;
import sample.model.DebtListModel;
import sample.model.databases.SetupDatabase;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SaveCompanyTask;
import sample.tasks.database_tasks.UpdateCompanyTask;

import java.io.IOException;
import java.sql.Date;


public class ChangeCompanyController implements BottomBarInterface {

    @FXML
    private JFXTextField companyName,creditCycle;

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

        if(status==0) {
            if(!isEditable) {

                SaveCompanyTask task=new SaveCompanyTask(companyName.getText().trim(),creditCycle.getText());
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
        this.creditCycle.setText(model.getDays());
        this.model=model;
    }

}
