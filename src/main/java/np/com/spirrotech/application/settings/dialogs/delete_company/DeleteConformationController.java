package np.com.spirrotech.application.settings.dialogs.delete_company;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.dao.DebtListModel;
import np.com.spirrotech.model.RemoveCompanyTask;
import np.com.spirrotech.utils.tasks.TaskManager;

import java.io.IOException;

public class DeleteConformationController implements BottomBarInterface {
    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label remarkLabel,companyLabel;

    private DebtListModel model;

    public ObservableList<DebtListModel> listModels;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Delete Conformation");

        bottomBar.setButtonText("DELETE");

        bottomBar.setBottomBarInterface(this);

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {

        //Delete the company and remove it from the list
        listModels.remove(model);
        TaskManager.getInstance().executeTask(new RemoveCompanyTask(model));

        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public void initializeData(DebtListModel model, ObservableList<DebtListModel> listModels) {
        this.model = model;
        this.listModels = listModels;
    }
    public void showBalance(){
        remarkLabel.setText("Due:"+model.getDue()  +"  Total :"+model.getTotal());
        companyLabel.setText("Company : "+model.getCompanyName());
    }
}
