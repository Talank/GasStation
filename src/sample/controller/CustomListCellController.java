package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sample.model.DebtListModel;

public class CustomListCellController {

    @FXML
    private Label companyLabel;

    private AnchorPane anchorPane;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/customListCell.fxml"));
            loader.setController(this);
            anchorPane=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setData(DebtListModel model) {
        companyLabel.setText(model.getCompanyName());
    }

    public Node getView() {
        return anchorPane;
    }

}
