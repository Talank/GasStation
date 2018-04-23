package sample.controller;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import sample.model.DebtListModel;

public class DebtController {

    @FXML
    private JFXListView<DebtListModel> debtList;

    @FXML
    public void initialize() {
        debtList.setCellFactory(lv->new DebtListCell());
        debtList.getItems().addAll(new DebtListModel("SpirroTech"),new DebtListModel("Opaline"));
    }
}
