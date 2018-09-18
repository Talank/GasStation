package sample.tasks.database_tasks;

import sample.controller.SystemLogging;
import sample.controller.main_home_controller.SettingsController;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @deprecated
 *Changes the default printer for bill and other purposes.
 */

public class SetupPrintersTask extends Tasks {

    private String billPrinterName,analyticsPrinterName;

    /**
     *
     * @param billPrinterName the bill printer name
     * @param analyticsPrinterName the analytics printer name
     */

    public SetupPrintersTask(String billPrinterName, String analyticsPrinterName) {
        this.billPrinterName = billPrinterName;
        this.analyticsPrinterName = analyticsPrinterName;
    }

    @Override
    public void execute() {
        try {

            if(!SystemStatus.getBillPrinter().equals(billPrinterName)&&!SystemStatus.getAnalyticsPrinter().equals(analyticsPrinterName)) {

                PreparedStatement updatePrinters=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                updatePrinters.setString(1,billPrinterName);
                updatePrinters.setDate(2,SystemStatus.getCurrentDate());
                updatePrinters.setInt(3,16);

                updatePrinters.addBatch();

                updatePrinters.setString(1,analyticsPrinterName);
                updatePrinters.setInt(3,17);

                updatePrinters.addBatch();

                updatePrinters.executeBatch();

                updatePrinters.close();
            } else {

                if(!SystemStatus.getBillPrinter().equals(billPrinterName)) {
                    PreparedStatement setupBill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    setupBill.setString(1,billPrinterName);
                    setupBill.setDate(2,SystemStatus.getCurrentDate());
                    setupBill.setInt(3,16);

                    setupBill.execute();

                    setupBill.close();
                }

                if(!SystemStatus.getAnalyticsPrinter().equals(analyticsPrinterName)) {
                    PreparedStatement setupBill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    setupBill.setString(1,analyticsPrinterName);
                    setupBill.setDate(2,SystemStatus.getCurrentDate());
                    setupBill.setInt(3,17);

                    setupBill.execute();

                    setupBill.close();
                }
            }

            connection.commit();
            SystemLogging.getLogger().fine(" SystemStatus printer setup updated");

            SystemStatus.setBillPrinter(billPrinterName);
            SystemStatus.setAnalyticsPrinter(analyticsPrinterName);

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Printer Saved",1000);
            snackBarToastGenerator.show();

            String[][] model=SettingsController.getInstance().getModel();
            model[8][0]=billPrinterName;
            model[8][1]=SystemStatus.getNepaliCurrentDate();
            model[9][0]=analyticsPrinterName;
            model[9][1]=SystemStatus.getNepaliCurrentDate();



        } catch(SQLException e) {
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning(" SystemStatus printer setup failed to update");

            rollBack();

            String message="Error Setting Up Printers.\n"+"Please Retry Again.";
            String title="Printer Setup Error";
            reportError(title, message, this);
        }
    }
}
