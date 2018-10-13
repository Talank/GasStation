package sample.model;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.ObservableSet;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import sample.controller.SystemLogging;


public class PrintBill {

    private PrinterJob printerJob;

    private Printer currentPrinter;


    public PrintBill() {
        ObservableSet<Printer> printers=Printer.getAllPrinters();

        currentPrinter=null;
        for(Printer printer:printers) {
            if(printer.getName().equals(SystemStatus.getBillPrinter())) {
                currentPrinter=printer;
            }
        }

        if(currentPrinter!=null) {
            printerJob = PrinterJob.createPrinterJob(currentPrinter);
        }
    }

    public PrinterJob getPrinterJob() {
        return printerJob;
    }


    public boolean printClosingBill(Node node) {

        PrinterJob printerJob;
        Printer currentPrinter = getCurrentPrinter(SystemStatus.getAnalyticsPrinter());//use the analytics printer


        //check if the printer is available
        if(currentPrinter!=null) {
            printerJob = PrinterJob.createPrinterJob(currentPrinter);//create a printer job to print

            if(printerJob.printPage(node)) {
                printerJob.endJob();
                SystemLogging.getLogger().fine("Printing Operation successfully done");
                return true;
            }
        }


        return false;
    }

    private Printer getCurrentPrinter(String printerName) {
        ObservableSet<Printer> printers=Printer.getAllPrinters();
        Printer currentPrinter = null;

        for(Printer printer:printers) {
            if(printer.getName().equals(printerName)) {
                currentPrinter=printer;
                break;
            }
        }

        return currentPrinter;
    }


    public boolean printTransactionBill(Node node) {

        PrinterJob printerJob;
        Printer currentPrinter = getCurrentPrinter(SystemStatus.getBillPrinter());//use the bill printer

        if(currentPrinter!=null) {
            printerJob = PrinterJob.createPrinterJob(currentPrinter);
            //create custom paper size for the printer
            Paper ohmPaper = PrintHelper.createPaper("Chinese Printer", 80, 150, Units.MM);

            //set the margin to hardware minimum
            if(printerJob.printPage(currentPrinter.createPageLayout(ohmPaper, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM),node)) {
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
