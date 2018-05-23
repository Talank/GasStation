package sample.controller.dialog_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.ProfitLossController;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.tasks.Tasks;

import java.io.IOException;
import java.sql.*;

public class TankRemarkController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    private static TankRemarkController instance;
    @FXML
    private Label avgLossLabel;
    @FXML
    private Label remarkLabel;

    public TankRemarkController() {
        instance=this;
    }

    private Connection connection;

    @FXML
    public void initialize() {
        toolBar.setDialogTitle("Tanker Check");
        bottomBar.setButtonText("OK");
        bottomBar.setBottomBarInterface(this);
        remarkLabel.setText("Yo");

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/main_home_views/profitLoss.fxml"));


    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    public static TankRemarkController getInstance() {
        return instance;
    }

    public void setData(int data) {
        float avg=0;
        Statement stmt= null;
        try {
            stmt = Tasks.getConnection().createStatement();

            ResultSet resultSet;
            System.out.print("THe data is"+data);
            resultSet = stmt.executeQuery("SELECT * FROM TANKERS WHERE TANKER_NO="+ data);
            resultSet.next();
            if (resultSet.getRow() > 0) {
                avg = resultSet.getFloat(2) / resultSet.getInt(3);

            /* Rating of the tanker
            /* Done under the specified condition
             */
                if (avg > 0 && avg <= 20) {

                   // this.remarkLabel.setText("The tanker is good");
                    System.out.print("The tanker is good");
                } else if (avg >= 21 && avg <= 50) {
                    //this.remarkLabel.setText("Normal tanker");
                    System.out.print("Normal tanker");
                } else if (avg > 50) {
                    //this.remarkLabel.setText("Not good tanker");
                    System.out.print("Not good tanker");

                } else
                   // this.remarkLabel.setText("Not valid tanker");
                System.out.print("Not valid tnaker");

            }
            else{
                System.out.print("Tanker Not found");
//                this.remarkLabel.setText("tanker not found");
            }
        }

        catch(SQLException e) {
            e.printStackTrace();
        }




    }
}
