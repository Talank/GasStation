package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sample.model.CreditDetailsModel;

import java.util.List;

public class CustomCreditListCellController {

    @FXML
    private Label transactionDate,total;

    @FXML
    private TableView<CreditDetailsModel> tableView;

    @FXML
    private TableColumn<CreditDetailsModel,String> vehicleNo;

    @FXML
    private TableColumn<CreditDetailsModel,String> itemName;

    @FXML
    private TableColumn<CreditDetailsModel,String> rate;

    @FXML
    private TableColumn<CreditDetailsModel,String> quantity;

    @FXML
    private TableColumn<CreditDetailsModel,String> price;

    private VBox vBox;

    public CustomCreditListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/main_home_views/credit_company_details/customCreditListCell.fxml"));
            loader.setController(this);
            vBox=loader.load();
            vehicleNo.setCellValueFactory(new PropertyValueFactory<>("vehicleNo"));
            itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
            quantity.setCellValueFactory(new PropertyValueFactory<>("volume"));
            price.setCellValueFactory(new PropertyValueFactory<>("price"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(List<CreditDetailsModel> modelLists) {
        tableView.setItems(FXCollections.observableList(modelLists));
        if(modelLists.size()!=0) {
            transactionDate.setText("Transaction Date:"+modelLists.get(0).getDate());
            float total=0;
            for(int i=0;i<modelLists.size();i++) {
                total+=Float.parseFloat(modelLists.get(i).getPrice());
            }
            this.total.setText("Total:Rs."+String.valueOf(total)+"/-");
        }

    }

    public Node getNode() {
        return vBox;
    }

}
