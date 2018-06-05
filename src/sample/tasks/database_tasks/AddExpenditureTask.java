package sample.tasks.database_tasks;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.controller.dialog_controller.ErrorDialogController;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddExpenditureTask extends Tasks {

    private  String errorMessage ="";
    private String memberName,expenditureRemarks;
    private String expenditureAmount;

    public AddExpenditureTask(String memberName, String expenditureAmount, String expenditureRemarks) {
        this.memberName= memberName;
        this.expenditureAmount= expenditureAmount;
        this.expenditureRemarks= expenditureRemarks;
    }


    @Override
    public void execute() throws IOException {
        try {

            PreparedStatement addExpenditure=connection.prepareStatement("INSERT INTO Expenditure VALUES (?,?,?,?,?)");

            addExpenditure.setString(1,memberName);
            addExpenditure.setInt(2,Integer.parseInt(expenditureAmount));
            addExpenditure.setString(3,expenditureRemarks);
            addExpenditure.setDate(4,SystemStatus.getCurrentDate());

            addExpenditure.execute();


            /*Statement addExpenditure=connection.createStatement();

            addExpenditure.execute("INSERT INTO Expenditure VALUES ('"+memberName+"',"+expenditureAmount+",'"+expenditureRemarks+"','"+SystemStatus.getCurrentDate()+"')");*/
            connection.commit();
            System.out.println(errorMessage);

        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage += "Error: "+e.getMessage()+"\n";
//            System.out.println(e.getMessage());
            //System.out.println(errorMessage);

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");
                errorMessage +="Database rolled back to stable state";

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
                errorMessage +="Can not rolled back to stable state \n Contact Software developers\n ALERT: keep ta backup of your data";
            }


            Parent root=FXMLLoader.load(getClass().getResource("../../view/dialogs/errorDialog.fxml"));

            ErrorDialogController errorDialogController = new ErrorDialogController();
            errorDialogController.setErrorMessage(errorMessage);

            Platform.runLater(()-> {
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.show();
            });

        }

        /**
         * alert!
         * the data should be removed in the beginning of each month
         */

    }
}

