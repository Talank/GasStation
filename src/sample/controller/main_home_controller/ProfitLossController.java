package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.OutputTasks.GetRefillTask;
import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.RefillModel;

import java.io.IOException;


/*
* This Class Provides and stores the info for the tanker refill
*
*
* */

public class ProfitLossController implements BackButtonInterface {

    @FXML
    private JFXButton reFill;

    @FXML
    private ToolBar toolBar;

    @FXML
    private TableView<RefillModel> tableView;

    @FXML
    private TableColumn<RefillModel,String> expectedDiesel;

    @FXML
    private TableColumn<RefillModel,String> actualDiesel;

    @FXML
    private TableColumn<RefillModel,String> expectedPetrol;

    @FXML
    private TableColumn<RefillModel,String> actualPetrol;

    @FXML
    private TableColumn<RefillModel,String> petrolLoss;

    @FXML
    private TableColumn<RefillModel,String> dieselLoss;


    @FXML
    private TableColumn<RefillModel,String> date;

    @FXML
    private VBox profitLossLayout;

    @FXML
    private OutputTaskLoading progressLayout;


    private ObservableList<RefillModel> tableData;

    private static ProfitLossController instance;

    private GetRefillTask task=null;


    public ProfitLossController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("ReFill Management");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.addBackButtonInterface(this);

        profitLossLayout.setVisible(false);
        progressLayout.setVisible(true);

        task=new GetRefillTask();
        progressLayout.setProgressProperty(task.progressProperty());

        expectedDiesel.setCellValueFactory(new PropertyValueFactory<>("expectedDiesel"));
        actualDiesel.setCellValueFactory(new PropertyValueFactory<>("actualDiesel"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        expectedPetrol.setCellValueFactory(new PropertyValueFactory<>("expectedPetrol"));
        actualPetrol.setCellValueFactory(new PropertyValueFactory<>("actualPetrol"));
        petrolLoss.setCellValueFactory(new PropertyValueFactory<>("petrolLoss"));
        dieselLoss.setCellValueFactory(new PropertyValueFactory<>("dieselLoss"));

        task.setOnSucceeded((WorkerStateEvent e)->{
            tableData=FXCollections.observableList(task.getValue());
            tableView.setItems(tableData);
            progressLayout.setVisible(false);
            profitLossLayout.setVisible(true);
        });

        Thread mainThread=new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();


    }

    @Override
    public void cleanUp() {
        if(task!=null)
            task.cancel();
    }

    public void addModel(RefillModel model) {
        tableData.add(model);
    }

    public static ProfitLossController getInstance() {
        return instance;
    }

    @FXML
    public void loadRefillChange(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/refillDialog.fxml"));
        Stage stage=new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
