package np.com.spirrotech.library.dialog_bottombar;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import java.io.IOException;


public class DialogBottomBar extends HBox {

    @FXML
    private JFXButton positiveButton;

    @FXML
    private Label errorText;

    private BottomBarInterface bottomBarInterface;

    public DialogBottomBar() {
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("np/com/spirrotech/library/dialog_bottombar/dialogBottomBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setButtonText(String text) {
        positiveButton.setText(text);
    }

    public void setErrorText(String msg) {
        errorText.setText(msg);
    }

    public void setBottomBarInterface(BottomBarInterface bottomBarInterface) {
        this.bottomBarInterface=bottomBarInterface;
    }

    @FXML
    public void positiveActionPerformed(MouseEvent e) {
        try {
            bottomBarInterface.performAction(e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @FXML
    public void cancel(MouseEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

}
