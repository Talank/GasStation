package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SetupPrintersTask;

import java.io.IOException;
import java.util.ArrayList;

public class PrinterDialogController {

    @FXML
    private DialogBottomBar bottomBar;

    @FXML
    private DialogToolBar toolBar;


    @FXML
    private JFXComboBox<String> billPrinterName,analyticsPrinterName;




    @FXML
    public void initialize() {

        toolBar.setDialogTitle("Setup Printer");

        bottomBar.setButtonText("SAVE");
        bottomBar.setBottomBarInterface((e)->{
            SetupPrintersTask task=new SetupPrintersTask(billPrinterName.getValue(),analyticsPrinterName.getValue());
            TaskManager.executeTask(task);
            ((Node)e.getSource()).getScene().getWindow().hide();
        });

        ObservableSet<Printer> printers=Printer.getAllPrinters();
        ArrayList<String> printerList=new ArrayList<>();
        for(Printer printer:printers) {
            printerList.add(printer.getName());
        }

        billPrinterName.setItems(FXCollections.observableList(printerList));
        analyticsPrinterName.setItems(FXCollections.observableList(printerList));

        billPrinterName.setValue(SystemStatus.getBillPrinter());
        analyticsPrinterName.setValue(SystemStatus.getAnalyticsPrinter());

    }


}
