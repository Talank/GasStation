package sample.library;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;

public class ToolBar extends GridPane {

    @FXML
    private Label toolbarTitle;

    private String backPath;

    public ToolBar() {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("toolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void setTitle(String title) {
        toolbarTitle.setText(title);
    }

    public void setBackPath(String path) {
        backPath=path;
    }

    @FXML
    public void backToPath(MouseEvent e) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource(backPath));
        Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
