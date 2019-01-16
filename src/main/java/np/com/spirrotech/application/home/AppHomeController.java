package np.com.spirrotech.application.home;

import com.jfoenix.controls.JFXSnackbar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import np.com.spirrotech.application.home.dialogs.LoginDialogController;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.utils.nepaliPriceFormat.Precision;
import np.com.spirrotech.utils.nepaliPriceFormat.PriceFormat;

import java.io.IOException;

/**
 * This is the controller for the main screen
 * We keep this scene alive, and swap others scene within this scene
 */

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

    /**
     * initializes the View
     */

    @FXML
    public void initialize() {
        instance=this;

        precision = new Precision();
        priceFormat = new PriceFormat();

        snackBar=new JFXSnackbar(root);
        //Note we are not using relative paths now, because JAR file doesn't know .., as it is a file system property
        snackBar.getStylesheets().add(getClass().getClassLoader().getResource("css/appHomeStyle.css").toExternalForm());

        dieselRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getdRate()))));
        petrolRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getpRate()))));
        dieselStock.setText((priceFormat.format(precision.format(SystemStatus.getDieselStock())))+" L");
        petrolStock.setText((priceFormat.format(precision.format(SystemStatus.getPetrolStock())))+" L");

        try {

            mainHome.getChildren().clear();
            BorderPane borderPane=FXMLLoader.load(getClass().getClassLoader().getResource("application/home/menu/mainHome.fxml"));
            mainHome.getChildren().add(borderPane);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Loads new layout in the application
     * @param borderPane the layout to display
     */

    public void loadStages(BorderPane borderPane) {

        mainHome.getChildren().clear();

        mainHome.getChildren().add(borderPane);

    }

    /**
     * Refresh the diesel and petrol rate displays
     */

    public void setRates() {
        dieselRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getdRate()))));
        petrolRate.setText("Rs. "+(priceFormat.format(precision.format(SystemStatus.getpRate()))));
    }

    /**
     * Refresh petrol stock and diesel stock
     */

    public void setStock() {
        dieselStock.setText((priceFormat.format(precision.format(SystemStatus.getDieselStock())))+" L");
        petrolStock.setText((priceFormat.format(precision.format(SystemStatus.getPetrolStock())))+" L");
    }

    /**
     * Loads the login dialog, to make entry point to settings
     * @param e
     * @throws IOException
     */

    @FXML
    public void loadSettings(MouseEvent e) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/home/dialogs/loginDialog.fxml"));
        Parent root=loader.load();
        LoginDialogController controller=loader.getController();
        controller.setStatus(1);
        controller.setStage((Stage)((Node)e.getSource()).getScene().getWindow());
        Stage stage=new Stage();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/logo.png")));
        stage.setTitle("Login");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Displays the message in the snackBar
     * @param message the message to display
     * @param timeOut time in milliseconds to display the message for
     */

    public void showMessage(String message,int timeOut) {
        snackBar.show(message,timeOut);
    }

    /**
     * Gets the current instance of the class
     * @return the current instance
     */

    public static AppHomeController getInstance() {
        return instance;
    }

}
