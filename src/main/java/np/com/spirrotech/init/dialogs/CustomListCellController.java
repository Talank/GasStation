package np.com.spirrotech.init.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import np.com.spirrotech.model.DebtListModel;

public class CustomListCellController {

    @FXML
    private Label companyName;

    private HBox hBox;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("np/com/spirrotech/init/dialogs/customListCell.fxml"));
            loader.setController(this);
            hBox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(DebtListModel model) {
        companyName.setText(model.getCompanyName());
    }

    public Node getView() {
        return hBox;
    }

}
