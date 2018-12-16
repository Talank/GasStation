package sample;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**+
 * This class is just for the splash screen, to give user something to look when the application is loading.
 */

public class ApplicationPreLoader extends Preloader {

    private Stage primaryStage;


    /**+
     * Called when it is time to start the preLoader, the view is loaded here
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;
        Parent root=FXMLLoader.load(getClass().getResource("view/startingScreen.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("sample/resources/logo.png")));
        primaryStage.setTitle("Shree Om Petroleum Traders");

        primaryStage.setScene(new Scene(root));

        primaryStage.setMaximized(true);
        primaryStage.show();

    }

    /**+
     * Called when the application state changes. We hide the preLoader when the application "start()" function is called.
     * @param info the object containing info about the type of state the app is in.
     */

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if(info.getType()== StateChangeNotification.Type.BEFORE_START) {
            primaryStage.hide();
        }
    }
}
