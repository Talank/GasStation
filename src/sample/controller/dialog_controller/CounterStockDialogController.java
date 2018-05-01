package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.databases.DatabaseHandler;

import java.io.IOException;

public class CounterStockDialogController implements BottomBarInterface {

    @FXML
    private DialogToolBar toolBar;

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private JFXTextField counterStock;


    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Setup Counter Stock");

        bottomBar.setButtonText("SETUP");
        bottomBar.setBottomBarInterface(this);

        RequiredFieldValidator counterStockValidator=new RequiredFieldValidator();
        counterStockValidator.setMessage("Counter Stock Cannot Be Empty");
        counterStock.getValidators().add(counterStockValidator);


        counterStock.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.matches("[0-9]{0,10}"))
                counterStock.setText(oldValue);
            else
            if(newValue.isEmpty())
                counterStock.validate();
            else
                counterStock.resetValidation();
        });
    }

    @Override
    public void performAction(MouseEvent e) throws IOException {
        if(counterStock.getText().isEmpty()) {
            counterStock.validate();
        } else {
            if(DatabaseHandler.setupCounterStock(counterStock.getText())) {
                ((Node)e.getSource()).getScene().getWindow().hide();
            } else {
                System.out.println("Error Updating Counter Stock");
            }
        }
    }

    public void setInitialStock(String stock) {
        counterStock.setText(stock);
    }
}
