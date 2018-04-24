package sample.controller;

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
import java.sql.Statement;


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
        Parent root=FXMLLoader.load(getClass().getResource("../view/dialogs/loginDialog.fxml"));
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
        loadStage((Node)e.getSource(),"../view/credit_company_list/debt.fxml");
    }

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {
        loadStage((Node)e.getSource(),"../view/settings.fxml");
    }

    @FXML
    public void loadDailyAnalytics(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/dialogs/finalReadings.fxml"));
        Parent root=loader.load();
        FinalReadingsController controller=loader.getController();
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void loadProfitLoss(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("../view/profitLoss.fxml"));
        Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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
        stage.setScene(new Scene(root));
        stage.show();
    }


}
