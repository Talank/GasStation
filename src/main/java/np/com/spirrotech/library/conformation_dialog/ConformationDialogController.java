package np.com.spirrotech.library.conformation_dialog;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import np.com.spirrotech.library.dialog_bottombar.BottomBarInterface;
import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.utils.tasks.Tasks;

import java.io.IOException;

public class ConformationDialogController {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label conformationLabel;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Conformation");
        bottomBar.setButtonText("YES");
    }



    public void setConformationLabel(String label) {
        conformationLabel.setText(label);
    }

    public void setTitle(String title) {
        toolBar.setDialogTitle(title);
    }


    //set the bottomBarInterface listener
    public void setConformListener(BottomBarInterface bottomBarInterface) {
        bottomBar.setBottomBarInterface(bottomBarInterface);
    }
}
