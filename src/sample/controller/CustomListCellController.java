package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sample.model.DebtListModel;

public class CustomListCellController {

    @FXML
    private Label companyLabel,date,days;

    private VBox vbox;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/customListCell.fxml"));
            loader.setController(this);
            vbox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setData(DebtListModel model) {
        companyLabel.setText(model.getCompanyName());
        date.setText("Credit Time:"+model.getDate());
        days.setText("Credit Cycle:"+model.getDays()+" Days");
    }

    public Node getView() {
        return vbox;
    }

}
