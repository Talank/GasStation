package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import sample.library.toolbar.ToolBar;
import sample.model.CreditDetailsModel;
import sample.model.databases.DatabaseHandler;

import java.util.List;

public class CreditCompanyDetailsController {

    @FXML
    private ToolBar toolBar;

    @FXML
    private ListView<List<CreditDetailsModel>> detailsListView;


    @FXML
    public void initialize() {

        toolBar.setTitle("Credit Company Details");
        toolBar.setBackPath("../../view/main_home_views/credit_company_list/debt.fxml");

        detailsListView.setCellFactory(lv->new CreditListCell());
        detailsListView.setSelectionModel(new NoSelectionModel<>());


    }

    public void setParameters(String tableName) {
        detailsListView.getItems().addAll(FXCollections.observableList(DatabaseHandler.getCreditCompanyDetails(tableName)));
    }
}
