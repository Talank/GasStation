package sample.controller.main_home_controller.lists_controllers;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sample.model.transaction_model.Transaction;

public class TransactionListCell extends ListCell<Transaction> {

    private TransactionListCellController controller=new TransactionListCellController();
    private Node node=controller.getNode();

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setGraphic(null);
        } else {
            controller.setItemType(item.getType());
            setGraphic(node);
        }
    }
}
