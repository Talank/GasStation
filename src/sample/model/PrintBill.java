package sample.model;

import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.stage.Stage;


public class PrintBill {

    public static boolean print(Node node) {
        PrinterJob printerJob=PrinterJob.createPrinterJob();
        Stage stage=new Stage();
        if(printerJob==null)
            return false;
        if(printerJob.showPrintDialog(stage)) {
            if(printerJob.printPage(node)) {
                printerJob.endJob();
                return true;
            }

        }
        return false;
    }

}
