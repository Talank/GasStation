package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sample.OutputTasks.GetTankCheckTask;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.TankCheckModel;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class TankCheckController  {


    @FXML
    private ToolBar toolBar;

    @FXML
    private TableView<TankCheckModel> TankCheckTable;


    @FXML
    private TableColumn<TankCheckModel,String> tankerNo;

    @FXML
    private TableColumn<TankCheckModel,String> totalLoss;

    @FXML
    private TableColumn<TankCheckModel,String > averageLoss;

    @FXML
    private VBox tankCheckLayout;



    private ObservableList<TankCheckModel> tankTableData;

    private int mode=0;


    private static TankCheckController instance;

    private GetTankCheckTask task=null;


    public TankCheckController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("Tanker Check");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        //toolBar.addBackButtonInterface(this);

        tankCheckLayout.setVisible(false);
//        progressLayout.setVisible(true);

        task=new GetTankCheckTask();
//        progressLayout.setProgressProperty(task.progressProperty());

         tankerNo.setCellValueFactory(new PropertyValueFactory<>("tankNo"));
         totalLoss.setCellValueFactory(new PropertyValueFactory<>("tankLoss"));
         averageLoss.setCellValueFactory(new PropertyValueFactory<>("counter"));





        task.setOnSucceeded((WorkerStateEvent e)->{


            try {
                tankTableData=FXCollections.observableArrayList(task.get());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }



            TankCheckTable.setItems(tankTableData);


            //progressLayout.setVisible(false);
            tankCheckLayout.setVisible(true);
        });

        Thread mainThread=new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();



    }


    public static TankCheckController getInstance() {
        return instance;
    }




}


