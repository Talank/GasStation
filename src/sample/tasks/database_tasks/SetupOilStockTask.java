package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.AppHomeController;
import sample.controller.SystemLogging;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.SystemStatus;
import sample.model.databases.DatabaseHandler;
import sample.nepali_date.NepaliDateGenerator;
import sample.tasks.Tasks;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @deprecated
 *Changes the oil stock for diesel and petrol.
 */

public class SetupOilStockTask extends Tasks {

    private String dieselStock,petrolStock;

    /**
     *
     * @param dieselStock the diesel stock
     * @param petrolStock the petrol stock
     */

    public SetupOilStockTask(String dieselStock,String petrolStock) {
        this.dieselStock=dieselStock;
        this.petrolStock=petrolStock;
    }


    @Override
    public void execute() {
        try {

            if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselStock)&&SystemStatus.getPetrolStock()!=Float.parseFloat(petrolStock)) {

                PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                updateStock.setString(1,dieselStock);
                updateStock.setDate(2,SystemStatus.getCurrentDate());
                updateStock.setInt(3,14);

                updateStock.addBatch();

                updateStock.setString(1,petrolStock);
                updateStock.setInt(3,15);

                updateStock.addBatch();

                updateStock.executeBatch();

                updateStock.close();

            } else {

                if(SystemStatus.getDieselStock()!=Float.parseFloat(dieselStock)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?, DATE=? WHERE ID=?");
                    updateStock.setString(1,dieselStock);
                    updateStock.setDate(2,SystemStatus.getCurrentDate());
                    updateStock.setInt(3,14);

                    updateStock.execute();

                    updateStock.close();
                }

                if(SystemStatus.getPetrolStock()!=Float.parseFloat(petrolStock)) {
                    PreparedStatement updateStock=connection.prepareStatement("UPDATE SystemStatus SET VALUE='"+petrolStock+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=15");
                    updateStock.setString(1,petrolStock);
                    updateStock.setDate(2,SystemStatus.getCurrentDate());
                    updateStock.setInt(3,15);
                    updateStock.execute();

                    updateStock.close();
                }

            }

            connection.commit();
            SystemLogging.getLogger().fine(" SystemStatus oil stock successfully updated");

            SystemStatus.setDieselStock(Float.parseFloat(dieselStock));
            SystemStatus.setPetrolStock(Float.parseFloat(petrolStock));

            Platform.runLater(()->AppHomeController.getInstance().setStock());

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Oil Stock Saved",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            System.out.println(e.getMessage());
            SystemLogging.getLogger().warning("  SystemStatus oil stock failed to update");

            rollBack();

            String message="Error Setting up Oil Stock.\n"+"Please Retry Again.";
            String title="Oil Stock Error";
            reportError(title, message, this);
        }

    }
}
