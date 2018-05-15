package sample.controller.dialog_controller.dialog_list_view_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import sample.model.DebtListModel;

public class CustomListCellController {

    @FXML
    private Label companyName;

    private HBox hBox;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/dialogs/dialog_list_views/customListCell.fxml"));
            loader.setController(this);
            hBox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(DebtListModel model) {
        companyName.setText(model.getCompanyName());
    }

    public Node getView() {
        return hBox;
    }

}
