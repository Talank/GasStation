package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.model.DebtListModel;
import sample.model.nepaliPriceFormat.PriceFormat;

import java.io.IOException;

public class CreditCompanyListCellController {

    @FXML
    private Label companyName,companyCycle,companyDue,companyTotal,companyLastPaid;
    PriceFormat priceFormat=new PriceFormat();

    private VBox vBox;

    public CreditCompanyListCellController() {

        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/main_home_views/credit_company_list/creditCompanyListCell.fxml"));
            loader.setController(this);
            vBox=loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public void setModel(DebtListModel model) {
        companyName.setText(model.getCompanyName());
        companyCycle.setText("Cycle:"+model.getDays()+" Days.");
        priceFormat.setFormat(model.getDue()+"");
        companyDue.setText("Due:Rs"+priceFormat.getResult());
        priceFormat.setFormat(model.getTotal()+"");
        companyTotal.setText("Total:Rs"+priceFormat.getResult());
        companyLastPaid.setText("Current Session:"+model.getDate());

        companyDue.setTextFill(Color.RED);
        companyTotal.setTextFill(Color.GREEN);

        if(model.isExpired()) {
            companyLastPaid.setTextFill(Color.RED);
        }

    }

    public Node getView() {
        return vBox;
    }
}
