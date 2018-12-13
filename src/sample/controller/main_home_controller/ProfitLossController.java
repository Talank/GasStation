package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import sample.OutputTasks.GetRefillTask;
import sample.controller.dialog_controller.RefillDialogController;
import sample.controller.dialog_controller.TankRemarkController;
import sample.library.output_task_loading.OutputTaskLoading;
import sample.library.toolbar.BackButtonInterface;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.RefillModel;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;
import sample.tasks.TaskManager;


import java.io.IOException;



/*
* This Class Provides and stores the info for the tanker refill
* just to remove merge
*
* */

public class ProfitLossController implements BackButtonInterface {

    @FXML
    private JFXButton reFill;

    @FXML
    private JFXButton checkTanker;

    @FXML
    private JFXTextField searchTanker;

    @FXML
    private JFXRadioButton dieselButton,petrolButton;

    @FXML
    private ToggleGroup selection;


    @FXML
    private ToolBar toolBar;

    @FXML
    private TableView<RefillModel> dieselTable;



    @FXML
    private TableColumn<RefillModel,String> dieselExpected;

    @FXML
    private TableColumn<RefillModel,String> dieselActual;

    @FXML
    private TableColumn<RefillModel,String> petrolLoss;

    @FXML
    private TableColumn<RefillModel,String> dieselDate;

    @FXML
    private TableColumn<RefillModel,String> petrolRemarks;

    @FXML
    private TableView<RefillModel> petrolTable;

    @FXML
    private TableColumn<RefillModel,String> petrolExpected;

    @FXML
    private TableColumn<RefillModel,String> petrolActual;

    @FXML
    private TableColumn<RefillModel,String> dieselLoss;

    @FXML
    private TableColumn<RefillModel,String> dieselRemarks;


    @FXML
    private TableColumn<RefillModel,String> petrolDate;



    @FXML
    private VBox profitLossLayout;

    @FXML
    private OutputTaskLoading progressLayout;

    private PriceFormat priceFormat;
    private Precision precision;


    private ObservableList<RefillModel> dieselTableData,petrolTableData;

    private int mode=0;


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

        precision = new Precision();
        priceFormat = new PriceFormat();

        RequiredFieldValidator tankerValidator=new RequiredFieldValidator();
        tankerValidator.setMessage("Invalid Tanker Number");
        searchTanker.getValidators().add(tankerValidator);


        searchTanker.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{

            if(!newValue.matches("[-a-zA-Z0-9]{0,10}")) {
                searchTanker.validate();
                searchTanker.setText(oldValue);
            } else {
                searchTanker.resetValidation();
            }
        });


        profitLossLayout.setVisible(false);
        progressLayout.setVisible(true);

        task=new GetRefillTask();
        progressLayout.setProgressProperty(task.progressProperty());


        dieselExpected.setCellValueFactory(new PropertyValueFactory<>("expectedString"));
        dieselExpected.setStyle("-fx-alignment: CENTER;");

        dieselExpected.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        dieselActual.setCellValueFactory(new PropertyValueFactory<>("actualString"));
        dieselActual.setStyle("-fx-alignment: CENTER;");

        dieselActual.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        dieselLoss.setCellValueFactory(new PropertyValueFactory<>("lossString"));
        dieselLoss.setStyle("-fx-alignment: CENTER;");

        dieselLoss.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        dieselDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        dieselDate.setStyle("-fx-alignment: CENTER;");
        dieselRemarks.setCellValueFactory(new PropertyValueFactory<>("TankerNumber"));
        dieselRemarks.setStyle("-fx-alignment: CENTER;");
        dieselRemarks.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);
                        if (!isEmpty()) {

                            if(item.charAt(item.length() - 1) == 'G') {
                                this.setTextFill(Color.GREEN);
                            } else if(item.charAt(item.length() - 1) == 'B') {
                                this.setTextFill(Color.BLUE);
                            } else if(item.charAt(item.length() - 1) == 'R') {
                                this.setTextFill(Color.RED);
                            } else {
                                this.setTextFill(Color.BLACK);
                            }
                            item=item.substring(0,(item.length() - 1));

                            setText(item);

                        }
                    }
                };
            }
        });


        // dieselRemarks.setStyle("-fx-selection-bar: red; -fx-selection-bar-non-focused: salmon;");

        petrolExpected.setCellValueFactory(new PropertyValueFactory<>("expectedString"));
        petrolExpected.setStyle("-fx-alignment: CENTER;");

        petrolExpected.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        petrolActual.setCellValueFactory(new PropertyValueFactory<>("actualString"));
        petrolActual.setStyle("-fx-alignment: CENTER;");

        petrolActual.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        petrolLoss.setCellValueFactory(new PropertyValueFactory<>("lossString"));
        petrolLoss.setStyle("-fx-alignment: CENTER;");

        petrolLoss.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                        }
                    }
                };
            }
        });

        petrolDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        petrolDate.setStyle("-fx-alignment: CENTER;");
        petrolRemarks.setCellValueFactory(new PropertyValueFactory<>("TankerNumber"));
        petrolRemarks.setStyle("-fx-alignment: CENTER;");
        petrolRemarks.setCellFactory(new Callback<TableColumn<RefillModel, String>, TableCell<RefillModel, String>>() {
            @Override
            public TableCell<RefillModel, String> call(TableColumn<RefillModel, String> param) {
                return new TableCell<RefillModel,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                           System.out.println(item);

                            if(item.charAt(item.length() - 1) == 'G') {
                                this.setTextFill(Color.GREEN);
                            } else if(item.charAt(item.length() - 1) == 'B') {
                                this.setTextFill(Color.BLUE);
                            } else if(item.charAt(item.length() - 1) == 'R') {
                                this.setTextFill(Color.RED);
                            } else {
                                this.setTextFill(Color.BLACK);
                            }

                            item=item.substring(0, (item.length() - 1));


                            setText(item);

                        }

                    }
                };
            }
        });


        task.setOnSucceeded((WorkerStateEvent e)->{

            dieselTableData=FXCollections.observableList(task.getValue().get(0));
            petrolTableData=FXCollections.observableList(task.getValue().get(1));

            dieselTable.setItems(dieselTableData);
            petrolTable.setItems(petrolTableData);

            progressLayout.setVisible(false);
            profitLossLayout.setVisible(true);
        });

        TaskManager.getInstance().executeInstantTask(task);

        selection.selectedToggleProperty().addListener((ObservableValue<? extends Toggle > ov, Toggle old_toggle, Toggle new_toggle)->{
            if(((JFXRadioButton)new_toggle).getText().equals("Diesel")) {
                dieselButton.setSelected(true);
                petrolButton.setSelected(false);
                petrolTable.setVisible(false);
                dieselTable.setVisible(true);
                mode=0;
            } else {
                petrolButton.setSelected(true);
                dieselButton.setSelected(false);
                dieselTable.setVisible(false);
                petrolTable.setVisible(true);
                mode=1;
            }
        });




    }

    @Override
    public void cleanUp() {
        if(task!=null)
            task.cancel();
    }

    public void addDieselModel(RefillModel model) {
        dieselTable.setItems(null);
        dieselTable.layout();
        dieselTableData.add(model);
        dieselTable.setItems(dieselTableData);
    }

    public void addPetrolModel(RefillModel model) {
        petrolTable.setItems(null);
        petrolTable.layout();
        petrolTableData.add(model);
        petrolTable.setItems(petrolTableData);
    }


    public static ProfitLossController getInstance() {
        return instance;
    }

    @FXML
    public void loadRefillChange(MouseEvent e) throws IOException {

        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/refillDialog.fxml"));

        Parent root=loader.load();
        RefillDialogController controller=loader.getController();

        controller.setMode(mode);
        if(mode==0) {
            controller.setToolBarTitle("Diesel Refill");

        } else {
            controller.setToolBarTitle("Petrol Refill");
        }

        Stage stage=new Stage();
        stage.setTitle("Refill Dialog");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void loadTankerCheck(MouseEvent mouseEvent) throws IOException {


        if(!searchTanker.getText().isEmpty()) {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/tankRemark.fxml"));
            Parent root= loader.load();

            Stage stage=new Stage();
            stage.setTitle("Remark Dialog");
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

            TankRemarkController controller=loader.getController();
            controller.setData(searchTanker.getText());
        } else {
            searchTanker.validate();
        }

    }
}
