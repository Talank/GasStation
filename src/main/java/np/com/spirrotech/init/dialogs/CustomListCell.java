package np.com.spirrotech.init.dialogs;


import javafx.scene.Node;
import javafx.scene.control.ListCell;

import np.com.spirrotech.model.DebtListModel;


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
