package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import sample.model.CreditDetailsModel;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;

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

    private PriceFormat priceFormat;
    private Precision precision;

    public CustomCreditListCellController() {

        precision = new Precision();
        priceFormat = new PriceFormat();

        try {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/main_home_views/credit_company_details/customCreditListCell.fxml"));
            loader.setController(this);
            vBox=loader.load();
            vehicleNo.setCellValueFactory(new PropertyValueFactory<>("vehicleNo"));
            vehicleNo.setStyle("-fx-alignment: CENTER;");
            itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            itemName.setStyle("-fx-alignment: CENTER;");
            rate.setCellValueFactory(new PropertyValueFactory<>("rate"));

            rate.setCellFactory(new Callback<TableColumn<CreditDetailsModel, String>, TableCell<CreditDetailsModel, String>>() {
                @Override
                public TableCell<CreditDetailsModel, String> call(TableColumn<CreditDetailsModel, String> param) {
                    return new TableCell<CreditDetailsModel, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if(!empty) {
                                setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                            }

                        }
                    };
                }
            });

            rate.setStyle("-fx-alignment: CENTER;");

            quantity.setCellValueFactory(new PropertyValueFactory<>("volume"));

            quantity.setCellFactory(new Callback<TableColumn<CreditDetailsModel, String>, TableCell<CreditDetailsModel, String>>() {
                @Override
                public TableCell<CreditDetailsModel, String> call(TableColumn<CreditDetailsModel, String> param) {
                    return new TableCell<CreditDetailsModel, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if(!empty) {
                                setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                            }

                        }
                    };
                }
            });

            quantity.setStyle("-fx-alignment: CENTER;");

            price.setCellValueFactory(new PropertyValueFactory<>("price"));

            price.setCellFactory(new Callback<TableColumn<CreditDetailsModel, String>, TableCell<CreditDetailsModel, String>>() {
                @Override
                public TableCell<CreditDetailsModel, String> call(TableColumn<CreditDetailsModel, String> param) {
                    return new TableCell<CreditDetailsModel, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if(!empty) {
                                setText(priceFormat.format(precision.format(Float.parseFloat(item))));
                            }

                        }
                    };
                }
            });

            price.setStyle("-fx-alignment: CENTER;");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(List<CreditDetailsModel> modelLists) {
        tableView.refresh();//refresh the table view, as javafx might cache the value, resulting in mixed data from many tables (took a whole day to figure out)


        //set the data to the table as observable list
        tableView.setItems(FXCollections.observableList(modelLists));

        //Compute the total of the transaction
        if(modelLists.size()!=0) {
            transactionDate.setText("Transaction Date:"+modelLists.get(0).getDate());
            float total=0;
            for(int i=0;i<modelLists.size();i++) {
                total+=Float.parseFloat(modelLists.get(i).getPrice());
            }
            PriceFormat priceFormat=new PriceFormat(String.valueOf(total));
            this.total.setText("Total:Rs."+priceFormat.getResult()+"/-");
        }

    }

    public Node getNode() {
        return vBox;
    }

}
