package sample.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.sun.deploy.util.FXLoader;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.dialog_controller.LoginDialogController;
import sample.model.SystemStatus;
import sample.model.nepaliPriceFormat.Precision;
import sample.model.nepaliPriceFormat.PriceFormat;

import java.io.IOException;

public class AppHomeController {

    @FXML
    private StackPane mainHome;

    private Precision precision;

    private PriceFormat priceFormat;

    @FXML
    private BorderPane root;

    @FXML
    private Label dieselRate,petrolRate,dieselStock,petrolStock;

    private static AppHomeController instance;

    private JFXSnackbar snackBar;

    @FXML
    public void initialize() {
        instance=this;

        precision = new Precision();
        priceFormat = new PriceFormat();

        snackBar=new JFXSnackbar(root);
        snackBar.getStylesheets().add(getClass().getResource("../css/appHomeStyle.css").toExternalForm());

        dieselRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getdRate()))));
        petrolRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getpRate()))));
        dieselStock.setText((priceFormat.format(precision.format(SystemStatus.getDieselStock())))+" L");
        petrolStock.setText((priceFormat.format(precision.format(SystemStatus.getPetrolStock())))+" L");

        try {

            mainHome.getChildren().clear();
            BorderPane borderPane=FXMLLoader.load(getClass().getResource("../view/main_home_views/mainHome.fxml"));
            mainHome.getChildren().add(borderPane);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public void loadStages(BorderPane borderPane) {

        mainHome.getChildren().clear();

        mainHome.getChildren().add(borderPane);

    }

    public void setRates() {
        dieselRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getdRate()))));
        petrolRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getpRate()))));
    }

    public void setStock() {
        dieselStock.setText((priceFormat.format(precision.format(SystemStatus.getDieselStock())))+" L");
        petrolStock.setText((priceFormat.format(precision.format(SystemStatus.getPetrolStock())))+" L");
    }

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../resources/logo.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void showMessage(String message,int timeOut) {
        snackBar.show(message,timeOut);
    }

    public static AppHomeController getInstance() {
        return instance;
    }

}
