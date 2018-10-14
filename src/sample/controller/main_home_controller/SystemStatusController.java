package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sample.OutputTasks.GetExpenditureTask;
import sample.controller.main_home_controller.credit_company_details_controller.NoSelectionModel;
import sample.controller.main_home_controller.expenditure_list_controller.ExpenditureListCell;
import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;
import sample.model.ExpenditureModel;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.tasks.TaskManager;

public class SystemStatusController implements BackButtonInterface {

    @FXML
    private Label systemDate;

    @FXML
    private Label transactionDate;

    @FXML
    private Label initialDieselReadings;

    @FXML
    private Label initialPetrolReadings;

    @FXML
    private ListView<ExpenditureModel> expenditureList;

    @FXML
    private Label total;

    @FXML
    private OutputTaskLoading progressLayout;

    private Precision precision;
    private PriceFormat priceFormat;

    private GetExpenditureTask task;


    @FXML
    private ToolBar toolBar;

    @FXML
    public void initialize() {

        toolBar.setTitle("System Status");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.addBackButtonInterface(this);

        precision = new Precision();

        priceFormat = new PriceFormat();

        expenditureList.setCellFactory(lv->new ExpenditureListCell());
        expenditureList.setSelectionModel(new NoSelectionModel<>());

        task=new GetExpenditureTask();
        task.setOnSucceeded((WorkerStateEvent e)->{
            expenditureList.setItems(FXCollections.observableList(task.getValue()));

            float sum=0;
            for (ExpenditureModel model:task.getValue()) {
                sum+=Integer.parseInt(model.getAmount());
            }

            total.setText("Total Expenditure:Rs "+priceFormat.format(precision.format(sum))+"/-");

            expenditureList.setVisible(true);
            progressLayout.setVisible(false);
        });

        progressLayout.setProgressProperty(task.progressProperty());

        systemDate.setText(SystemStatus.getNepaliCurrentDate());
        transactionDate.setText(SystemStatus.getNepaliTransactionDate());

        initialDieselReadings.setText("" + priceFormat.format(precision.format(SystemStatus.getiDiesel())) + " Litres");

        initialPetrolReadings.setText("" + priceFormat.format(precision.format(SystemStatus.getiPetrol())) + " Litres");

        TaskManager.getInstance().executeInstantTask(task);

    }

    @Override
    public void cleanUp() {
        if(task!=null) {
            task.cancel();
        }
    }
}
