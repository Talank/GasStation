package sample.controller.main_home_controller.expenditure_list_controller;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sample.model.ExpenditureModel;

public class ExpenditureListCell extends ListCell<ExpenditureModel> {

    private ExpenditureListCellController controller=new ExpenditureListCellController();
    private Node node=controller.getView();

    @Override
    protected void updateItem(ExpenditureModel item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setGraphic(null);
        } else {
            controller.setData(item);
            setGraphic(node);
        }

    }
}
