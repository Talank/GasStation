package sample.controller;

import com.sun.deploy.util.FXLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AppHomeController {

    @FXML
    private StackPane mainHome;

    @FXML
    public void initialize() {
        try {
            BorderPane borderPane=FXMLLoader.load(getClass().getResource("../view/main_home_views/mainHome.fxml"));
            mainHome.getChildren().add(borderPane);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}
