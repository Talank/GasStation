package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import org.apache.derby.jdbc.EmbeddedDriver;
import sample.model.DatabaseHandler;
import sample.model.SetupDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        if(!DatabaseHandler.setupConnection()) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setContentText("Database Connection cannot be established. Please Contact SpirroTech Officials.");
            alert.showAndWait();
            System.exit(-1);
        } else if(DatabaseHandler.resumeSystem()) {
            Parent root = FXMLLoader.load(getClass().getResource("view/splashScreen.fxml"));
            primaryStage.setTitle("Ohm Gas Station");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
        Parent root = FXMLLoader.load(getClass().getResource("view/mainHome.fxml"));
        primaryStage.setTitle("Ohm Gas Station");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        DatabaseHandler.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}