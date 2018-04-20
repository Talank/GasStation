package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.derby.jdbc.EmbeddedDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {
    private static final String DRIVER="org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL="jdbc:derby:ohmoilpump;create=true";
    private static Connection connection;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/splashScreen.fxml"));
        primaryStage.setTitle("Ohm Gas Station");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            connection=DriverManager.getConnection(JDBC_URL);
            if(connection!=null)
                System.out.println("Connection Established");
            else
                System.out.println("Error Connecting Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}