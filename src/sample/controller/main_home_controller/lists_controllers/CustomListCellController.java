package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.CompanyCreditClosingTask;

import java.io.IOException;

public class CustomListCellController {

    @FXML
    private Label companyLabel,date,days;


    private DebtListModel model;

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
        this.model=model;
        companyLabel.setText(model.getCompanyName());
        date.setText("Credit Time:"+model.getDate());
        days.setText("Credit Cycle:"+model.getDays()+" Days");
        if(model.isExpired()) {
            companyLabel.setTextFill(Color.RED);
            date.setTextFill(Color.RED);
            days.setTextFill(Color.RED);
        }


    }

    @FXML
    public void closing(MouseEvent e) throws IOException {
        companyLabel.setTextFill(Color.BLACK);
        date.setTextFill(Color.BLACK);
        days.setTextFill(Color.BLACK);
        date.setText("Credit Time:"+SystemStatus.getCurrentDate());

        CompanyCreditClosingTask task=new CompanyCreditClosingTask(model);
        TaskManager.executeTask(task);


    }

    public Node getView() {
        return vbox;
    }

}
