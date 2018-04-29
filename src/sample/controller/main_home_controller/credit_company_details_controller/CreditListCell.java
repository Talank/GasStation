package sample.controller.main_home_controller.credit_company_details_controller;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sample.model.CreditDetailsModel;

import java.util.List;

public class CreditListCell extends ListCell<List<CreditDetailsModel>> {

    private CustomCreditListCellController controller=new CustomCreditListCellController();
    private Node vBox=controller.getNode();

    @Override
    protected void updateItem(List<CreditDetailsModel> item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(vBox);
        }
    }
}
