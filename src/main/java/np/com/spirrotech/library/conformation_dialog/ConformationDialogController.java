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

public class ConformationDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private Label conformationLabel;

    private Tasks task=null;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Conformation");
        bottomBar.setButtonText("YES");
        bottomBar.setBottomBarInterface(this);
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {

        if(task!=null) {
            TaskManager.getInstance().executeTask(task);
        }
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    //set the task that should be performed when the user conforms the operation
    public void setTask(Tasks task) {
        this.task = task;
    }

    public void setConformationLabel(String label) {
        conformationLabel.setText(label);
    }

    public void setTitle(String title) {
        toolBar.setDialogTitle(title);
    }
}
