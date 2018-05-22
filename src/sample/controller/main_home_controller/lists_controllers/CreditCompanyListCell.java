package sample.controller.main_home_controller.lists_controllers;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sample.model.DebtListModel;

public class CreditCompanyListCell extends ListCell<DebtListModel> {

    private CreditCompanyListCellController controller=new CreditCompanyListCellController();
    private Node view=controller.getView();

    @Override
    protected void updateItem(DebtListModel item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setGraphic(null);
        } else {
            controller.setModel(item);
            setGraphic(view);
        }
    }
}
