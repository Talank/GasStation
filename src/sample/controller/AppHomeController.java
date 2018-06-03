package sample.controller;

import com.sun.deploy.util.FXLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.dialog_controller.LoginDialogController;
import sample.model.SystemStatus;

import java.io.IOException;

public class AppHomeController {

    @FXML
    private StackPane mainHome;

    @FXML
    private Label dieselRate,petrolRate,dieselStock,petrolStock;

    private static AppHomeController instance;

    @FXML
    public void initialize() {
        instance=this;

        dieselRate.setText("Rs. "+String.valueOf(SystemStatus.getdRate()));
        petrolRate.setText("Rs. "+String.valueOf(SystemStatus.getpRate()));
        dieselStock.setText(String.valueOf(SystemStatus.getDieselStock())+" L");
        petrolStock.setText(String.valueOf(SystemStatus.getPetrolStock())+" L");

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

    public void setRates(String dieselRate,String petrolRate) {
        this.dieselRate.setText("Rs. "+dieselRate);
        this.petrolRate.setText("Rs. "+petrolRate);
    }

    public void setStock(String dieselStock,String petrolStock) {
        this.dieselStock.setText(dieselStock+" L");
        this.petrolStock.setText(petrolStock+" L");
    }

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static AppHomeController getInstance() {
        return instance;
    }

}
