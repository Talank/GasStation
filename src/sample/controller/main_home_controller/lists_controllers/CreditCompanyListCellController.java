package sample.controller.main_home_controller.lists_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.model.DebtListModel;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;

import java.io.IOException;

public class CreditCompanyListCellController {

    @FXML
    private Label companyName,companyCycle,companyDue,companyTotal,companyLastPaid,prePaid;
    private PriceFormat priceFormat;
    private Precision precision;

    private VBox vBox;

    public CreditCompanyListCellController() {
        priceFormat = new PriceFormat();
        precision = new Precision();
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

        companyDue.setText("Due: Rs "+priceFormat.format(precision.format(model.getDue())));

        companyTotal.setText("Total: Rs "+ priceFormat.format(precision.format(model.getTotal())));
        companyLastPaid.setText("Current Session:"+model.getDate());

        prePaid.setText("Prepaid: Rs "+priceFormat.format(precision.format(model.getPrePaid())));

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
