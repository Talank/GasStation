package sample.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.DatabaseHandler;
import sample.model.RateModel;

import javafx.scene.input.MouseEvent;
import sample.model.SystemStatus;

import java.io.IOException;


public class MainHomeController {

    private static MainHomeController instance;


    @FXML
    private TextField rDiesel,rPetrol;

    public MainHomeController() {
        instance=this;
    }

    @FXML
    public void initialize() {
        RateModel rateModel=DatabaseHandler.getCurrentRate();
        rDiesel.setText(String.valueOf(rateModel.getrDiesel()));
        rPetrol.setText(String.valueOf(rateModel.getrPetrol()));
        SystemStatus.setdRate(rateModel.getrDiesel());
        SystemStatus.setpRate(rateModel.getrPetrol());
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
    @FXML
    public void loadInstantBilling(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../view/instantBilling.fxml");
    }

    @FXML
    public void loadCreditBilling(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../view/creditBilling.fxml");
    }

    @FXML
    public void loadDebt(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../view/debt.fxml");
    }


    public void setRates(String rDiesel,String rPetrol) {
        this.rDiesel.setText(rDiesel);
        this.rPetrol.setText(rPetrol);
    }

    public static MainHomeController getInstance() {
        return instance;
    }

    private void loadStage(Node node,String path) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource(path));
        Stage stage=(Stage)node.getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
