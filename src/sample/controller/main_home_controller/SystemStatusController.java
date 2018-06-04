package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sample.OutputTasks.GetExpenditureTask;
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

        task=new GetExpenditureTask();
        task.setOnSucceeded((WorkerStateEvent e)->{
            expenditureList.setItems(FXCollections.observableList(task.getValue()));

            int sum=0;
            String tsum;

            for (ExpenditureModel model:task.getValue()) {
                sum+=Integer.parseInt(model.getAmount());
            }
            PriceFormat priceFormat=new PriceFormat();
            priceFormat.setFormat(sum+"");

            tsum=priceFormat.getResult();

            total.setText("Total Expenditure:Rs "+tsum+"/-");

            expenditureList.setVisible(true);
            progressLayout.setVisible(false);
        });


        progressLayout.setProgressProperty(task.progressProperty());

        systemDate.setText(SystemStatus.getNepaliCurrentDate());
        transactionDate.setText(SystemStatus.getNepaliTransactionDate());
        currentDieselStock.setText(String.valueOf(SystemStatus.getDieselStock())+" Litres");
        currentPetrolStock.setText(String.valueOf(SystemStatus.getPetrolStock())+" Litres");
        initialDieselReadings.setText(String.valueOf(SystemStatus.getiDiesel())+" Litres");
        initialPetrolReadings.setText(String.valueOf(SystemStatus.getiPetrol())+" Litres");

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
