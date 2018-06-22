package sample;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationPreLoader extends Preloader {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;
        Parent root=FXMLLoader.load(getClass().getResource("view/startingScreen.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();

    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if(info.getType()== StateChangeNotification.Type.BEFORE_START) {
            primaryStage.hide();
        }
    }
}
