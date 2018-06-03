package sample.tasks.database_tasks;

import sample.controller.main_home_controller.SettingsController;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SetupPrintersTask extends Tasks {

    private String billPrinterName,analyticsPrinterName;

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

                SystemStatus.setBillPrinter(billPrinterName);
                SystemStatus.setAnalyticsPrinter(analyticsPrinterName);

            } else {

                if(!SystemStatus.getBillPrinter().equals(billPrinterName)) {
                    PreparedStatement setupBill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    setupBill.setString(1,billPrinterName);
                    setupBill.setDate(2,SystemStatus.getCurrentDate());
                    setupBill.setInt(3,16);

                    setupBill.execute();
                    SystemStatus.setBillPrinter(billPrinterName);
                }

                if(!SystemStatus.getAnalyticsPrinter().equals(analyticsPrinterName)) {
                    PreparedStatement setupBill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    setupBill.setString(1,analyticsPrinterName);
                    setupBill.setDate(2,SystemStatus.getCurrentDate());
                    setupBill.setInt(3,17);

                    setupBill.execute();
                    SystemStatus.setAnalyticsPrinter(analyticsPrinterName);
                }
            }

            connection.commit();

            String[][] model=SettingsController.getInstance().getModel();
            model[8][0]=billPrinterName;
            model[8][1]=SystemStatus.getNepaliCurrentDate();
            model[9][0]=analyticsPrinterName;
            model[9][1]=SystemStatus.getNepaliCurrentDate();



        } catch(SQLException e) {
            System.out.println(e.getMessage());

            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }
        }
    }
}
