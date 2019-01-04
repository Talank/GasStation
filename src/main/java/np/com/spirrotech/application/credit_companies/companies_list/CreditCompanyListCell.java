package np.com.spirrotech.application.credit_companies.companies_list;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import np.com.spirrotech.model.DebtListModel;

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
