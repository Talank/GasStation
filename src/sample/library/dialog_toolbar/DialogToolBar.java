package sample.library.dialog_toolbar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class DialogToolBar extends HBox {

    @FXML
    private Label dialogTitle;

    public DialogToolBar() {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("dialogToolBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setDialogTitle(String title) {
        dialogTitle.setText(title);
    }

}
