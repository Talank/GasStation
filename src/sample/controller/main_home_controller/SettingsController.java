package sample.controller.main_home_controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.main_home_controller.lists_controllers.DebtListCell;
import sample.controller.SystemLogging;
import sample.controller.dialog_controller.ChangeCompanyController;
import sample.controller.dialog_controller.LoginDialogController;
import sample.library.toolbar.ToolBar;
import sample.model.CreditCompanyList;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.model.DebtListModel;

import java.io.IOException;


public class SettingsController {

    @FXML
    private JFXListView<DebtListModel> debtList;

    @FXML
    private ToolBar toolBar;

    private static SettingsController instance;

    private int modelIndex;

    public ObservableList<DebtListModel> listModels;

    public SettingsController() {
        instance=this;
    }

    @FXML
    public void initialize() {

        toolBar.setTitle("Settings");
        toolBar.setBackPath(SystemStatus.getMainHomePath());

        debtList.setCellFactory(lv->new DebtListCell());
        debtList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listModels=FXCollections.observableList(CreditCompanyList.getDetailCompanyList());
        debtList.setItems(listModels);
        debtList.getSelectionModel().select(0);

    }

    @FXML
    public void addCompany(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/changeCompany.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void deleteCompany(MouseEvent e) {
        ObservableList<DebtListModel> selected=debtList.getSelectionModel().getSelectedItems();
        String query="";
        if(selected.size()==1) {
            query=selected.get(0).getId()+"";
            listModels.removeAll(selected);
        } else if(selected.size()!=0)
            query=selected.get(0).getId()+",";

        for(int i=1;i<selected.size();i++) {
            query=query+selected.get(i).getId();
            listModels.removeAll(selected);
        }
        System.out.println("Query:"+query);
        if(DatabaseHandler.deleteCompany(query)) {
            SystemLogging.getLogger().fine("Company Deleted SuccessFully.");
        } else {
            SystemLogging.getLogger().severe("Error Deleting Companies!");
        }

    }

    @FXML
    public void editCompany(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/changeCompany.fxml"));
        Parent root=loader.load();
        ChangeCompanyController controller=loader.getController();
        modelIndex=debtList.getSelectionModel().getSelectedIndex();
        DebtListModel model=debtList.getSelectionModel().getSelectedItem();
        controller.setEditData(model);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    public void changeUserNameAndPassword(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../../view/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void editModel(DebtListModel model) {
        listModels.remove(modelIndex);
        listModels.add(modelIndex,model);
    }


    public static SettingsController getInstance() {
        return instance;
    }


}
