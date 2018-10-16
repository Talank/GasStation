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
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;


/**+
 * This is the starting point for the javafx application
 * It initializes the system
 */

public class Main extends Application {

    private FXMLLoader loader;
    private Parent root;
    private boolean startup=false;
    private String msg="Error Connecting to Database. Please Contact SpirroTech Officials";

    /**+
     * This is called when the app initializes, during this period the preLoader will be displayed, so we are doing all the initialization
     * here, so that, no much work needs to be done in start() function, and the app loads the stage immediately
     * @throws Exception Thrown if something goes wrong
     */

    @Override
    public void init() throws Exception {
        InitializeSystem initializeSystem=new InitializeSystem();
        if(Tasks.setupConnection()&&initializeSystem.customExecute()) {
            TaskManager.getInstance();
            startup=true;
            loader=new FXMLLoader(getClass().getResource("view/appHome.fxml"));
        } else {
            msg=initializeSystem.getErrorMessage();
            startup=false;
            loader=new FXMLLoader(getClass().getResource("view/dialogs/errorDialog.fxml"));
        }
        root = loader.load();

    }

    /**+
     * This is called when it is time to load the screen, the preLoader now is disabled, and the primary stage is loaded with the scene.
     * @param primaryStage The main stage for the application provided by the javafx
     * @throws Exception Throws exception (IO exception) when the fxml cannot be inflated
     */

    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println(LocalDateTime.now().getDayOfYear());
        SystemStatus.setStage(primaryStage);

        if(startup) {
            primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));

            primaryStage.setTitle("Ohm Gas Station");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMaximized(true);
            primaryStage.show();

            if(CreditCompanyList.getExpiredCompanyList().size()!=0) {
                FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/view/dialogs/dialog_list_views/expireCompanyDialog.fxml"));
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

    /**+
     * Called when the app is closed, performs realising of resources like, database connections
     */

    @Override
    public void stop() {
        /*
        * Close The Connection
        * Derby Database caches the primary key,so close it before using it
        * otherwise the primary key increments with 100 in every insertion
        * */


        /**
         *Production Code
         */

        //uncomment the following code to enable the backup

       /* try {
            //store the current database mirror to the disk
            //this call will take time, and will run after the window is closed
            CallableStatement callableStatement= Tasks.getConnection().prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
            callableStatement.setString(1,Reference.DATABASE_BACKUP_PATH);
            callableStatement.execute();
            callableStatement.close();
            System.out.println("Database Backup Has Been Created!");
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error Creating Database Backup!");
        }*/
        DatabaseHandler.closeConnection();

    }

}