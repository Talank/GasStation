package sample.controller.dialog_controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import sample.controller.main_home_controller.SettingsController;
import sample.library.SettingsConstants;
import sample.library.dialog_bottombar.BottomBarInterface;
import sample.library.dialog_bottombar.DialogBottomBar;
import sample.library.dialog_toolbar.DialogToolBar;
import sample.model.SystemStatus;
import sample.tasks.TaskInterface;
import sample.tasks.TaskManager;
import sample.tasks.database_tasks.SettingsTask;
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

            SettingsTask task = new SettingsTask(new String[] {billPrinterName.getValue(),analyticsPrinterName.getValue()}, new int[] {SettingsConstants.BILL_PRINTER, SettingsConstants.ANALYTICS_PRINTER}, SettingsConstants.CHANGE_PRINTER);
            task.addTaskInterfaceListener(new TaskInterface() {
                @Override
                public void onComplete(Object data) {
                    Platform.runLater(()->{
                        SystemStatus.setBillPrinter(billPrinterName.getValue());
                        SystemStatus.setAnalyticsPrinter(analyticsPrinterName.getValue());

                        String[][] model=SettingsController.getInstance().getModel();
                        model[8][0]=billPrinterName.getValue();
                        model[8][1]=SystemStatus.getNepaliCurrentDate();
                        model[9][0]=analyticsPrinterName.getValue();
                        model[9][1]=SystemStatus.getNepaliCurrentDate();
                        SettingsController.getInstance().loadSettings(model);

                        ((Node)e.getSource()).getScene().getWindow().hide();
                    });
                }

                @Override
                public void onError() {

                }
            });
            TaskManager.getInstance().executeTask(task);


            /*SetupPrintersTask task=new SetupPrintersTask(billPrinterName.getValue(),analyticsPrinterName.getValue());
            TaskManager.getInstance().executeTask(task);
            ((Node)e.getSource()).getScene().getWindow().hide();*/
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
