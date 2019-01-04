package np.com.spirrotech.init.dialogs;

import javafx.fxml.FXML;

import javafx.scene.Node;


import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import np.com.spirrotech.library.NoSelectionModel;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.model.CreditCompanyList;
import np.com.spirrotech.model.DebtListModel;


import java.io.IOException;

public class ExpireCompanyDialogController implements BottomBarInterface {


    @FXML
    private ListView<DebtListModel> expireListView;

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;




    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Expired Companies");

        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);

        expireListView.setCellFactory(lv->new CustomListCell());
        expireListView.setSelectionModel(new NoSelectionModel<>());

        expireListView.getItems().addAll(CreditCompanyList.getExpiredCompanyList());

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }
}
