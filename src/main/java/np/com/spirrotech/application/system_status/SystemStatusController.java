package np.com.spirrotech.application.system_status;

import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import np.com.spirrotech.library.NoSelectionModel;
import np.com.spirrotech.library.output_task_loading.OutputTaskLoading;
import np.com.spirrotech.library.toolbar.BackButtonInterface;
import np.com.spirrotech.library.toolbar.ToolBar;
import np.com.spirrotech.dao.ExpenditureModel;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.model.GetExpenditureTask;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;
import np.com.spirrotech.utils.tasks.TaskManager;

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
