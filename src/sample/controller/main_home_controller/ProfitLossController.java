package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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

public class ProfitLossController {

    @FXML
    private JFXButton reFill;

    @FXML
    private ToolBar toolBar;

    @FXML
    private TableView<RefillModel> tableView;

    @FXML
    private TableColumn<RefillModel,String> expected;

    @FXML
    private TableColumn<RefillModel,String> actual;

    @FXML
    private TableColumn<RefillModel,String> date;

    @FXML
    private TableColumn<RefillModel,String> loss;

    private ObservableList<RefillModel> tableData;

    private static ProfitLossController instance;

    public ProfitLossController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("ReFill Management");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        expected.setCellValueFactory(new PropertyValueFactory<>("expected"));
        actual.setCellValueFactory(new PropertyValueFactory<>("actual"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        loss.setCellValueFactory(new PropertyValueFactory<>("loss"));
        tableData=FXCollections.observableList(DatabaseHandler.getRefillModel());
        tableView.setItems(tableData);
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
