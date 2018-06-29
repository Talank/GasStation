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

public class SetupOilStockTask extends Tasks {

    private String dieselStock,petrolStock;

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

            try {
                connection.rollback();
                SystemLogging.getLogger().fine(" Rollback SystemStatus oil stock to stable state");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning(" Rollback SystemStatus oil stock failed");
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Setting up Oil Stock.\n"+"Please Retry Again.";
            String title="Oil Stock Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }

    }
}
