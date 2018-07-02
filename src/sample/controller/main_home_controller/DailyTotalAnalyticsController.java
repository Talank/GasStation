package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.OutputTasks.GetDailyTotalAnalyticsTask;
import sample.controller.dialog_controller.FinalReadingsController;
import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.DailyTotalAnalyticModel;

import java.io.IOException;


/*
* This class provides the info on daily transaction and also the transaction from past 30 days
*
* A simple table view is provided
*
* The table is populated using DailyTotalAnalyticModel using beans Reflection
*
*
* */


public class DailyTotalAnalyticsController implements BackButtonInterface {

    @FXML
    private TableView<DailyTotalAnalyticModel> tableView;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> iDieselVol;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> iPetrolVol;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> cDieselVol;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> cPetrolVol;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> extraIncome;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> oilIncome;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> total;

    @FXML
    private TableColumn<DailyTotalAnalyticModel,String> date;

    @FXML
    private ToolBar toolBar;

    @FXML
    private VBox dailyTotalAnalyticsLayout;

    @FXML
    private OutputTaskLoading progressLayout;


    private GetDailyTotalAnalyticsTask task=null;



    @FXML
    public void initialize() {

        toolBar.setTitle("Daily Analytics");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.addBackButtonInterface(this);

        dailyTotalAnalyticsLayout.setVisible(false);
        progressLayout.setVisible(true);


        iDieselVol.setCellValueFactory(new PropertyValueFactory<>("iDieselVol"));
        iPetrolVol.setCellValueFactory(new PropertyValueFactory<>("iPetrolVol"));
        cDieselVol.setCellValueFactory(new PropertyValueFactory<>("cDieselVol"));
        cPetrolVol.setCellValueFactory(new PropertyValueFactory<>("cPetrolVol"));
        extraIncome.setCellValueFactory(new PropertyValueFactory<>("extraIncome"));
        oilIncome.setCellValueFactory(new PropertyValueFactory<>("oilIncome"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        task=new GetDailyTotalAnalyticsTask();

        progressLayout.setProgressProperty(task.progressProperty());

        task.setOnSucceeded((WorkerStateEvent e)->{
            tableView.setItems(FXCollections.observableList(task.getValue()));
            progressLayout.setVisible(false);
            dailyTotalAnalyticsLayout.setVisible(true);
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

    /*
    * This Function is called when 'Close' Button is clicked
    *
    * Just load the final Reading dialog
    *
    *
    * */

    @FXML
    public void showTodayAnalytics(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/finalReadings.fxml"));
        Parent root=loader.load();

        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/logo.png")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }

}
