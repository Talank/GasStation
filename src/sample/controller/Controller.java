package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    private Label label;

    @FXML
    private JFXTextField instantMeter;

    @FXML
    private JFXButton startButton;


    //methods triggered on mouse clicked events
    @FXML
    public void instantBillingHandler(MouseEvent mouseEvent) throws IOException {
        Parent instantBillingParent = FXMLLoader.load(getClass().getResource("../view/instantBilling.fxml"));
        Scene instantBillingScene = new Scene(instantBillingParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(instantBillingScene);
        window.show();
    }

    @FXML
    public void startButtonClicked(MouseEvent mouseEvent) throws IOException {
        Parent homeParent=FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        Scene homeScene=new Scene(homeParent);
        Stage window=(Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.show();
    }

    @FXML
    public void creditBillingHandler(MouseEvent mouseEvent) throws IOException {
        Parent creditBillingParent = FXMLLoader.load(getClass().getResource("../view/creditBilling.fxml"));
        Scene creditBillingScene = new Scene(creditBillingParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(creditBillingScene);
        window.show();
    }

    @FXML
    public void debtHandler(MouseEvent mouseEvent) throws IOException {
        Parent debtParent = FXMLLoader.load(getClass().getResource("../view/debt.fxml"));
        Scene debtScene = new Scene(debtParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(debtScene);
        window.show();
    }

    @FXML
    public void profitLossHandler(MouseEvent mouseEvent) throws IOException {
        Parent profitLossParent = FXMLLoader.load(getClass().getResource("../view/profitLoss.fxml"));
        Scene profitLossScene = new Scene(profitLossParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(profitLossScene);
        window.show();
    }

    @FXML
    public void dailyAnalyticsHandler(MouseEvent mouseEvent) throws IOException {
        Parent dailyAnalyticsParent = FXMLLoader.load(getClass().getResource("../view/dailyAnalytics.fxml"));
        Scene dailyAnalyticsScene = new Scene(dailyAnalyticsParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(dailyAnalyticsScene);
        window.show();
    }

    @FXML
    public void reFillHandler(MouseEvent mouseEvent) throws IOException {
        Parent reFillParent = FXMLLoader.load(getClass().getResource("../view/reFill.fxml"));
        Scene reFillScene = new Scene(reFillParent);
        Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        window.setScene(reFillScene);
        window.show();
    }
}