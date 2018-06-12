package sample.controller.dialog_controller.dialog_list_view_controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.CreditCompanyList;
import sample.model.DebtListModel;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;

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
        expireListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        expireListView.getItems().addAll(CreditCompanyList.getExpiredCompanyList());

        expireListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DebtListModel> observable, DebtListModel oldValue, DebtListModel newValue)->{
            /*FXMLLoader loader=new FXMLLoader(getClass().getResource("../../../view/main_home_views/credit_company_details/creditCompanyDetails.fxml"));
            try {
                Parent root=loader.load();
                sample.controller.main_home_controller.credit_company_details_controller.CreditCompanyDetailsController controller=loader.getController();
                controller.setParameters(DatabaseHandler.generateTableName(newValue.getCompanyName()));
                SystemStatus.getStage().setScene(new Scene(root));
                SystemStatus.getStage().show();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        });

    }


    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }
}
