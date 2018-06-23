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
import sample.model.nepaliPriceFormat.PriceFormat;

public class SystemStatusController implements BackButtonInterface {

    @FXML
    private Label systemDate;

    @FXML
    private Label transactionDate;

    @FXML
    private Label currentDieselStock;

    @FXML
    private Label currentPetrolStock;

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

    private GetExpenditureTask task;


    @FXML
    private ToolBar toolBar;

    @FXML
    public void initialize() {

        toolBar.setTitle("System Status");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.addBackButtonInterface(this);

        expenditureList.setCellFactory(lv->new ExpenditureListCell());
        expenditureList.setSelectionModel(new NoSelectionModel<>());

        task=new GetExpenditureTask();
        task.setOnSucceeded((WorkerStateEvent e)->{
            expenditureList.setItems(FXCollections.observableList(task.getValue()));

            int sum=0;
            String totalSum;

            for (ExpenditureModel model:task.getValue()) {
                sum+=Integer.parseInt(model.getAmount());
            }
            PriceFormat priceFormat=new PriceFormat();
            priceFormat.setFormat(sum+"");

            totalSum=priceFormat.getResult();

            total.setText("Total Expenditure:Rs "+totalSum+"/-");

            expenditureList.setVisible(true);
            progressLayout.setVisible(false);
        });
        PriceFormat priceFormat=new PriceFormat();

        progressLayout.setProgressProperty(task.progressProperty());

        systemDate.setText(SystemStatus.getNepaliCurrentDate());
        transactionDate.setText(SystemStatus.getNepaliTransactionDate());

        priceFormat.setFormat(String.valueOf(SystemStatus.getDieselStock()));
        currentDieselStock.setText(priceFormat.getResult()+" Litres");
        priceFormat.setFormat(String.valueOf(SystemStatus.getPetrolStock()));
        currentPetrolStock.setText(priceFormat.getResult()+" Litres");
        priceFormat.setFormat(String.valueOf(SystemStatus.getiDiesel()));
        initialDieselReadings.setText(priceFormat.getResult()+" Litres");
        priceFormat.setFormat(String.valueOf(SystemStatus.getiPetrol()));
        initialPetrolReadings.setText(priceFormat.getResult()+" Litres");

        Thread mainThread=new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();

    }

    @Override
    public void cleanUp() {
        if(task!=null) {
            task.cancel();
        }
    }
}
