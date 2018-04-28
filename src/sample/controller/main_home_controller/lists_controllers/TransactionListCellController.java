package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TransactionListCellController {

    @FXML
    private Label itemType;

    private HBox hBox;

    public TransactionListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/main_home_views/credit_company_list/transactionListCell.fxml"));
            loader.setController(this);
            hBox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItemType(String type) {
        itemType.setText(type);
    }

    public Node getNode() {
        return hBox;
    }
}
