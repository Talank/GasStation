package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.collections.FXCollections;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


import sample.OutputTasks.GetCompanyDetailsTask;
import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;
import sample.model.CreditDetailsModel;
import sample.tasks.TaskManager;

import java.util.List;

public class CreditCompanyDetailsController implements BackButtonInterface {

    @FXML
    private ToolBar toolBar;


    @FXML
    private OutputTaskLoading progressLayout;

    @FXML
    private ListView<List<CreditDetailsModel>> detailsListView;

    private GetCompanyDetailsTask task;


    @FXML
    public void initialize() {

        task=null;

        toolBar.setTitle("Credit Company Details");
        toolBar.setBackPath("sample/view/main_home_views/credit_company_list/debt.fxml");
        toolBar.addBackButtonInterface(this);

        progressLayout.setVisible(true);
        detailsListView.setVisible(false);


        detailsListView.setCellFactory(lv->new CreditListCell());
        detailsListView.setSelectionModel(new NoSelectionModel<>());


    }

    @Override
    public void cleanUp() {
        if(task!=null)
            task.cancel();
    }

    public void setParameters(String tableName) {
        task=new GetCompanyDetailsTask(tableName);
        progressLayout.setProgressProperty(task.progressProperty());

        task.setOnSucceeded((WorkerStateEvent e)->{
            detailsListView.getItems().addAll(FXCollections.observableList(task.getValue()));
            progressLayout.setVisible(false);
            detailsListView.setVisible(true);
        });
        TaskManager.getInstance().executeInstantTask(task);

    }
}
