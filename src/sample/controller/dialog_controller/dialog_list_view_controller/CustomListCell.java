package sample.controller.dialog_controller.dialog_list_view_controller;


import javafx.scene.Node;
import javafx.scene.control.ListCell;

import sample.model.DebtListModel;


public class CustomListCell extends ListCell<DebtListModel> {


    private CustomListCellController controller=new CustomListCellController();
    private Node view=controller.getView();

    @Override
    protected void updateItem(DebtListModel item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(view);
        }
    }
}
