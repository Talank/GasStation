package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.library.toolbar.ToolBar;
import sample.model.CreditDetailsModel;
import sample.model.databases.DatabaseHandler;

public class CreditCompanyDetailsController {

    @FXML
    public ToolBar toolBar;

    @FXML
    private TableView<CreditDetailsModel> tableView;

    @FXML
    private TableColumn<CreditDetailsModel,String> vehicleNo;

    @FXML
    private TableColumn<CreditDetailsModel,String> type;

    @FXML
    private TableColumn<CreditDetailsModel,String> date;

    @FXML
    private TableColumn<CreditDetailsModel,String> volume;

    @FXML
    private TableColumn<CreditDetailsModel,String> price;



    @FXML
    public void initialize() {
        vehicleNo.setCellValueFactory(new PropertyValueFactory<>("vehicleNo"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        volume.setCellValueFactory(new PropertyValueFactory<>("volume"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

}
