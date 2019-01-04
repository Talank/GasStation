package np.com.spirrotech.library.billing_radio_buttons;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class BillingRadioButtonsController extends HBox {

    @FXML
    private ToggleGroup selection;

    public BillingRadioButtonsController() {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("np/com/spirrotech/library/billing_radio_buttons/billingRadioButtons.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
