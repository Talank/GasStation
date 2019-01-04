package np.com.spirrotech.library;

import javafx.application.Platform;
import np.com.spirrotech.application.home.AppHomeController;

public class SnackBarToastGenerator {

    private String message;
    private int timeOut;


    public SnackBarToastGenerator(String message, int timeOut) {
        this.message = message;
        this.timeOut = timeOut;
    }

    public void show() {
        Platform.runLater(()->AppHomeController.getInstance().showMessage(message,timeOut));
    }

}
