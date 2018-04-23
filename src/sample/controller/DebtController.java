package sample.controller;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.model.DatabaseHandler;
import sample.model.DebtListModel;

import java.io.IOException;


public class DebtController {

    @FXML
    private JFXListView<DebtListModel> debtList;

    @FXML
    public void initialize() {
        debtList.setCellFactory(lv->new DebtListCell());
        debtList.getItems().addAll(DatabaseHandler.getDebtCompany());
    }

    @FXML
    public void backToHome(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../view/mainHome.fxml"));
        Scene scene=new Scene(root);
        Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
