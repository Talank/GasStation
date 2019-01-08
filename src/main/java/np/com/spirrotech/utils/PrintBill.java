package np.com.spirrotech.utils;

import javafx.collections.ObservableSet;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import np.com.spirrotech.dao.SystemStatus;
import np.com.spirrotech.utils.SystemLogging;


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

            //we are using the default printer setup with custom margin
            PageLayout pageLayout = currentPrinter.createPageLayout(currentPrinter.getPrinterAttributes().getDefaultPaper(), currentPrinter.getPrinterAttributes().getDefaultPageOrientation(), 54,54,30,30);

            ((VBox)node).setPrefWidth(pageLayout.getPrintableWidth());

            printerJob.getJobSettings().setPageLayout(pageLayout);

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


    public boolean printTransactionBill(Node node, Boolean creditBill) {

        PrinterJob printerJob;
        Printer currentPrinter = getCurrentPrinter(SystemStatus.getBillPrinter());//use the bill printer

        if(currentPrinter!=null) {



            printerJob = PrinterJob.createPrinterJob(currentPrinter);


            //we are using the default printer setup with custom margin
            PageLayout pageLayout = currentPrinter.createPageLayout(currentPrinter.getPrinterAttributes().getDefaultPaper(), currentPrinter.getPrinterAttributes().getDefaultPageOrientation(), 1.44,1.44,54,54);

            printerJob.getJobSettings().setPageLayout(pageLayout);


            //Printing extra eopy of bill if it is a credit bill

            if(creditBill) {
                printerJob.getJobSettings().setCopies(2);
            }

            if(printerJob.printPage(node)) {

                /*if(billType == "CreditBill"){
                    if(printerJob.printPage(node)){
                        //bill printed successfully
                    }
                    else
                        SystemLogging.getLogger().fine("Printing 2nd bill error encountered");
                }*/

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