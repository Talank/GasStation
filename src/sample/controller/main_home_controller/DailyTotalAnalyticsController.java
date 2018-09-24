package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.OutputTasks.GetDailyTotalAnalyticsTask;

import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;

import sample.model.SystemStatus;

import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.model.transaction_model.DailyTotalAnalyticModel;
import sample.tasks.TaskManager;

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

    private PriceFormat priceFormat;
    private Precision precision;



    @FXML
    public void initialize() {

        toolBar.setTitle("Daily Analytics");
        toolBar.setBackPath(SystemStatus.getMainHomePath());
        toolBar.addBackButtonInterface(this);

        priceFormat = new PriceFormat();
        precision = new Precision();

        dailyTotalAnalyticsLayout.setVisible(false);
        progressLayout.setVisible(true);


        iDieselVol.setCellValueFactory(new PropertyValueFactory<>("iDieselVol"));
        iDieselVol.setStyle("-fx-alignment: CENTER;");

        iDieselVol.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        iPetrolVol.setCellValueFactory(new PropertyValueFactory<>("iPetrolVol"));
        iPetrolVol.setStyle("-fx-alignment: CENTER;");

        iPetrolVol.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        cDieselVol.setCellValueFactory(new PropertyValueFactory<>("cDieselVol"));
        cDieselVol.setStyle("-fx-alignment: CENTER;");

        cDieselVol.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        cPetrolVol.setCellValueFactory(new PropertyValueFactory<>("cPetrolVol"));
        cPetrolVol.setStyle("-fx-alignment: CENTER;");

        cPetrolVol.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        extraIncome.setCellValueFactory(new PropertyValueFactory<>("extraIncome"));
        extraIncome.setStyle("-fx-alignment: CENTER;");

        extraIncome.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        oilIncome.setCellValueFactory(new PropertyValueFactory<>("oilIncome"));
        oilIncome.setStyle("-fx-alignment: CENTER;");

        oilIncome.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        total.setStyle("-fx-alignment: CENTER;");

        total.setCellFactory(new Callback<TableColumn<DailyTotalAnalyticModel, String>, TableCell<DailyTotalAnalyticModel, String>>() {
            @Override
            public TableCell<DailyTotalAnalyticModel, String> call(TableColumn<DailyTotalAnalyticModel, String> param) {
                return new TableCell<DailyTotalAnalyticModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        date.setStyle("-fx-alignment: CENTER;");

        task=new GetDailyTotalAnalyticsTask();

        progressLayout.setProgressProperty(task.progressProperty());

        task.setOnSucceeded((WorkerStateEvent e)->{
            tableView.setItems(FXCollections.observableList(task.getValue()));
            progressLayout.setVisible(false);
            dailyTotalAnalyticsLayout.setVisible(true);
        });

        TaskManager.getInstance().executeInstantTask(task);

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

        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/finalReadings.fxml"));
        Parent root=loader.load();

        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }

}
