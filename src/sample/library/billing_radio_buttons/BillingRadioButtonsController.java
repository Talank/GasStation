package sample.library.billing_radio_buttons;

import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import sample.model.SystemStatus;

import java.io.IOException;

public class BillingRadioButtonsController extends HBox {

    @FXML
    private ToggleGroup selection;

    public BillingRadioButtonsController() {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("billingRadioButtons.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
