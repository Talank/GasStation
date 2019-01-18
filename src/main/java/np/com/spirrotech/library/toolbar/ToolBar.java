package np.com.spirrotech.library.toolbar;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import np.com.spirrotech.application.home.AppHomeController;


import java.io.IOException;

public class ToolBar extends GridPane {

    @FXML
    private Label toolbarTitle;

    private BackButtonInterface backButtonInterface;

    public ToolBar() {
        backButtonInterface=null;
        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("library/toolbar/toolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void setBackButtonInterface(BackButtonInterface backButtonInterface) {
        this.backButtonInterface=backButtonInterface;
    }

    public void setTitle(String title) {
        toolbarTitle.setText(title);
    }

    @FXML
    public void backToPath(MouseEvent e) throws IOException {
        if(backButtonInterface!=null)
            backButtonInterface.backButtonPressed();

    }

}
