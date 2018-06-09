package sample.library;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.dialog_controller.ErrorDialogController;
import sample.tasks.Tasks;

import java.io.IOException;

public class ErrorMessageDialogGenerator {

    private String errorMessage,title;

    private Tasks tasks=null;

    public ErrorMessageDialogGenerator(String errorMessage, String title) {
        this.errorMessage = errorMessage;
        this.title = title;
    }

    public void setTasks(Tasks tasks) {
        this.tasks=tasks;
    }

    public void showDialog() {
        Platform.runLater(()->{
            try {

                FXMLLoader loader=new FXMLLoader(getClass().getResource("../view/dialogs/errorDialog.fxml"));

                Parent root=loader.load();
                ErrorDialogController controller=loader.getController();

                controller.setTitle(title);
                controller.setErrorMessage(errorMessage);
                controller.setRetryTask(tasks);

                Stage stage=new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch(IOException e) {
                e.printStackTrace();
            }


        });
    }
}
