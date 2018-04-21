package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.DatabaseHandler;
import sample.model.RateModel;

import javafx.scene.input.MouseEvent;
import java.io.IOException;


public class mainHomeController {

    @FXML
    private TextField rDiesel,rPetrol;

    @FXML
    public void initialize() {
        RateModel rateModel=DatabaseHandler.getCurrentRate();
        rDiesel.setText(String.valueOf(rateModel.getrDiesel()));
        rPetrol.setText(String.valueOf(rateModel.getrPetrol()));
    }

    @FXML
    public void changeRate(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../view/loginDialog.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

}
