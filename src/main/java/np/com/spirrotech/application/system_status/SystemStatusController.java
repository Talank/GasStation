package np.com.spirrotech.application.system_status;

import javafx.application.Platform;
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
import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

        task.addTaskInterfaceListener(new TaskInterface() {
            @Override
            public void onComplete(Object data) {
                Platform.runLater(() -> {
                    expenditureList.setItems(FXCollections.observableList((ArrayList<ExpenditureModel>) data));

                    float sum=0;
                    for (ExpenditureModel model:(ArrayList<ExpenditureModel>) data) {
                        sum+=Integer.parseInt(model.getAmount());
                    }

                    total.setText("Total Expenditure:Rs "+priceFormat.format(precision.format(sum))+"/-");

                    expenditureList.setVisible(true);
                    progressLayout.setVisible(false);
                });


            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void updateProgress(double current, double total) {
                Platform.runLater(() -> {
                    progressLayout.setProgress(current / total);
                });


            }
        });


        systemDate.setText(SystemStatus.getNepaliCurrentDate());
        transactionDate.setText(SystemStatus.getNepaliTransactionDate());

        initialDieselReadings.setText("" + priceFormat.format(precision.format(SystemStatus.getiDiesel())) + " Litres");

        initialPetrolReadings.setText("" + priceFormat.format(precision.format(SystemStatus.getiPetrol())) + " Litres");

        TaskManager.getInstance().executeInstantTask(task);

    }

    @Override
    public void cleanUp() {
        if(task!=null) {
            task.setCancelled(true);
        }
    }
}
