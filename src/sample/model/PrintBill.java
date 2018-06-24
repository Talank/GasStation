package sample.model;

import javafx.collections.ObservableSet;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.stage.Stage;
import sample.controller.SystemLogging;


public class PrintBill {

    public static boolean print(Node node) {

        ObservableSet<Printer> printers=Printer.getAllPrinters();

        Printer currentPrinter=null;
        for(Printer printer:printers) {
            if(printer.getName().equals(SystemStatus.getBillPrinter())) {
                currentPrinter=printer;
            }
        }

    if(currentPrinter!=null) {
        PrinterJob printerJob=PrinterJob.createPrinterJob(currentPrinter);
        if(printerJob.printPage(node)) {
            printerJob.endJob();
            SystemLogging.getLogger().fine("Printing Operation successfully done");
            return true;
        }
    }

        /*if(printerJob.showPrintDialog(stage)) {
            if(printerJob.printPage(node)) {
                printerJob.endJob();
                return true;
            }

        }*/
        return false;
    }

}
