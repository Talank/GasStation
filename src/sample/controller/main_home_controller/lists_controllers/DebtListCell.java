package sample.controller.main_home_controller.lists_controllers;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sample.controller.main_home_controller.lists_controllers.CustomListCellController;
import sample.model.DebtListModel;

public class DebtListCell extends ListCell<DebtListModel> {

    private CustomListCellController controller=new CustomListCellController();
    private Node view=controller.getView();

    @Override
    protected void updateItem(DebtListModel item, boolean empty) {
        super.updateItem(item, empty);
        if(empty)
            setGraphic(null);
        else {
            controller.setData(item);
            setGraphic(view);
        }
    }
}
