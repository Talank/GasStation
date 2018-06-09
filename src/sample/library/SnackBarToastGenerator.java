package sample.library;

import javafx.application.Platform;
import sample.controller.AppHomeController;

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
