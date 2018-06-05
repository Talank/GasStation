package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;


import sample.controller.SystemLogging;
import sample.controller.dialog_controller.ErrorDialogController;
import sample.controller.dialog_controller.LoginDialogController;
import sample.model.CreditCompanyList;
import sample.model.RateModel;
import sample.model.databases.DatabaseHandler;


import sample.model.SystemStatus;
import sample.tasks.TaskManager;
import sample.tasks.Tasks;
import sample.tasks.database_tasks.InitializeSystem;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;


public class Main extends Application {

    private FXMLLoader loader;
    private Parent root;
    private boolean startup=false;
    private String msg="";

    @Override
    public void init() throws Exception {
        InitializeSystem initializeSystem=new InitializeSystem();
        if(Tasks.setupConnection()&&initializeSystem.customExecute()) {
            TaskManager.init(1);
            startup=true;
//            loader=new FXMLLoader(getClass().getResource("view/main_home_views/mainHome.fxml"));
            loader=new FXMLLoader(getClass().getResource("view/appHome.fxml"));
        } else {
            msg=initializeSystem.getErrorMessage();
            startup=false;
            loader=new FXMLLoader(getClass().getResource("view/dialogs/errorDialog.fxml"));
        }
        root = loader.load();

    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println(LocalDateTime.now().getDayOfYear());
        SystemStatus.setStage(primaryStage);

        if(startup) {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/logo.png")));

            primaryStage.setTitle("Ohm Gas Station");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
            primaryStage.show();

            if(CreditCompanyList.getExpiredCompanyList().size()!=0) {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("view/dialogs/dialog_list_views/expireCompanyDialog.fxml"));
                Parent root=loader.load();
                Stage stage=new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.show();
            }


        } else {
            ErrorDialogController controller=loader.getController();
            controller.setTitle("Initialization Error");
            controller.setErrorMessage(msg);
            Stage stage=new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }

    }

    @Override
    public void stop() {
        /*
        * Close The Connection
        * Derby Database caches the primary key,so close it before using it
        * otherwise the primary key increments with 100 in every insertion
        * */
        DatabaseHandler.closeConnection();
    }


/*    public static void main(String[] args) {
        launch(args);
    }*/
}