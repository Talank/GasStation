package np.com.spirrotech.application.system_status;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import np.com.spirrotech.dao.ExpenditureModel;

public class ExpenditureListCellController {

    @FXML
    private Label name,date,remarks,amount;

    private VBox vBox;

    public ExpenditureListCellController() {

        try {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/system_status/expenditure_list_cell.fxml"));
            loader.setController(this);
            vBox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(ExpenditureModel model) {
        name.setText(model.getName());
        date.setText(model.getDate());
        remarks.setText(model.getRemarks());
        amount.setText("Rs: "+model.getAmount()+"/-");
    }

    public Node getView() {
        return vBox;
    }


}
