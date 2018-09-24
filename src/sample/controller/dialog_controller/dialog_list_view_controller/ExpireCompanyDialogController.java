package sample.controller.dialog_controller.dialog_list_view_controller;

import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;

import javafx.scene.Node;


import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

import sample.controller.main_home_controller.credit_company_details_controller.NoSelectionModel;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.CreditCompanyList;
import sample.model.DebtListModel;


import java.io.IOException;

public class ExpireCompanyDialogController implements BottomBarInterface {


    @FXML
    private ListView<DebtListModel> expireListView;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;




    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Expired Companies");

        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);

        expireListView.setCellFactory(lv->new CustomListCell());
        expireListView.setSelectionModel(new NoSelectionModel<>());

        expireListView.getItems().addAll(CreditCompanyList.getExpiredCompanyList());

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }
}
