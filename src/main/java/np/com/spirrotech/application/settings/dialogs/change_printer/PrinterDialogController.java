package np.com.spirrotech.application.settings.dialogs.change_printer;

import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.Node;

import np.com.spirrotech.application.settings.SettingsController;
import np.com.spirrotech.library.SettingsConstants;

import np.com.spirrotech.library.dialog_bottombar.DialogBottomBar;
import np.com.spirrotech.library.dialog_toolbar.DialogToolBar;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.utils.tasks.TaskInterface;
import np.com.spirrotech.utils.tasks.TaskManager;
import np.com.spirrotech.application.settings.SettingsTask;

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
