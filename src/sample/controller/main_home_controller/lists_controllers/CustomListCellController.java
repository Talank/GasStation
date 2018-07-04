package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.dialog_controller.PaymentDialogController;
import sample.model.CreditCompanyClosingBill;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.CompanyCreditClosingTask;

import java.io.IOException;

public class CustomListCellController {

    @FXML
    private Label companyLabel,date,days;

    @FXML
    private ImageView imageView;

    private VBox vbox;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/main_home_views/credit_company_list/customListCell.fxml"));
            loader.setController(this);
            vbox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setData(DebtListModel model) {
        companyLabel.setText(model.getCompanyName());
        date.setText("Credit Time: "+model.getDate());
        days.setText("Credit Cycle: "+model.getDays()+" Days");
        if(model.isExpired()) {
            companyLabel.setTextFill(Color.RED);
            date.setTextFill(Color.RED);
            days.setTextFill(Color.RED);
            imageView.setImage(new Image(getClass().getResourceAsStream("../../../resources/warn.png")));
        } else {
            companyLabel.setTextFill(Color.BLACK);
            date.setTextFill(Color.BLACK);
            days.setTextFill(Color.BLACK);
            imageView.setImage(new Image(getClass().getResourceAsStream("../../../resources/true.png")));
        }


    }

    public Node getView() {
        return vbox;
    }

}
