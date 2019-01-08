package np.com.spirrotech.application.credit_billing;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TransactionListCellController {

    @FXML
    private Label itemType;

    private HBox hBox;

    public TransactionListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/credit_billing/transactionListCell.fxml"));
            loader.setController(this);
            hBox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItemType(String type) {
        itemType.setText(type);
    }

    public Node getNode() {
        return hBox;
    }
}
