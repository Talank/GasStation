package sample.library.output_task_loading;


import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OutputTaskLoading extends VBox {

    @FXML
    private ProgressBar progressBar;

    public OutputTaskLoading() {

        FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("sample/library/output_task_loading/outputTaskLoading.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setProgressProperty(ReadOnlyDoubleProperty progressProperty) {
        progressBar.progressProperty().bind(progressProperty);
    }


}
