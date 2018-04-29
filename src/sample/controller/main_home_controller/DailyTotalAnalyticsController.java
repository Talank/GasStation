package sample.controller.main_home_controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.dialog_controller.FinalReadingsController;
import sample.library.toolbar.ToolBar;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.transaction_model.DailyTotalAnalyticModel;

import java.io.IOException;


public class DailyTotalAnalyticsController {

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
    public void initialize() {

        toolBar.setTitle("Daily Analytics");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        iDieselVol.setCellValueFactory(new PropertyValueFactory<>("iDieselVol"));
        iPetrolVol.setCellValueFactory(new PropertyValueFactory<>("iPetrolVol"));
        cDieselVol.setCellValueFactory(new PropertyValueFactory<>("cDieselVol"));
        cPetrolVol.setCellValueFactory(new PropertyValueFactory<>("cPetrolVol"));
        extraIncome.setCellValueFactory(new PropertyValueFactory<>("extraIncome"));
        oilIncome.setCellValueFactory(new PropertyValueFactory<>("oilIncome"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.setItems(FXCollections.observableList(DatabaseHandler.getDailyTotalAnalyticModel()));

    }

    @FXML
    public void close(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/finalReadings.fxml"));
        Parent root=loader.load();
        FinalReadingsController controller=loader.getController();
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

}
