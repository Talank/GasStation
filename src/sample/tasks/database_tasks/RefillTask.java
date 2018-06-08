package sample.tasks.database_tasks;

import javafx.application.Platform;
import sample.controller.AppHomeController;
import sample.library.ErrorMessageDialogGenerator;
import sample.library.SnackBarToastGenerator;
import sample.model.RefillModel;
import sample.model.SystemStatus;
import sample.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RefillTask extends Tasks {

    private RefillModel model;

    private int mode;


    public RefillTask(RefillModel model,int mode) {
        this.model=model;
        this.mode=mode;
    }

    @Override
    public void execute() {
        try {

            if(mode==0) {

                PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO DieselRefill VALUES (?,?,?,?)");
                insertRefill.setFloat(1,model.getExpected());
                insertRefill.setFloat(2,model.getActual());
                insertRefill.setInt(3,Integer.parseInt(model.getTankerNumber()));
                insertRefill.setDate(4,SystemStatus.getCurrentDate());

                insertRefill.execute();

                insertRefill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                insertRefill.setString(1,String.valueOf(SystemStatus.getDieselStock()+model.getActual()));
                insertRefill.setDate(2,SystemStatus.getCurrentDate());
                insertRefill.setInt(3,14);

                insertRefill.execute();

                insertRefill.close();

                SystemStatus.setDieselStock(SystemStatus.getDieselStock()+model.getActual());
            } else {

                PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO PetrolRefill VALUES (?,?,?,?)");
                insertRefill.setFloat(1,model.getExpected());
                insertRefill.setFloat(2,model.getActual());
                insertRefill.setInt(3,Integer.parseInt(model.getTankerNumber()));
                insertRefill.setDate(4,SystemStatus.getCurrentDate());

                insertRefill.execute();

                insertRefill=connection.prepareStatement("UPDATE SystemStatus SET VALUE=?,DATE=? WHERE ID=?");
                insertRefill.setString(1,String.valueOf(SystemStatus.getPetrolStock()+model.getActual()));
                insertRefill.setDate(2,SystemStatus.getCurrentDate());
                insertRefill.setInt(3,15);

                insertRefill.execute();
                SystemStatus.setPetrolStock(SystemStatus.getPetrolStock()+model.getActual());

                insertRefill.close();
            }




            /**
             * Write the query to see if the row we want to modify already existed or not?
             *
             * if row with tanker number model.getTankerNumber() does not exist then
             * create a tow with TANKER_NO= model.getTankerNumber()
             * and insert TOTAL_LOSS= model.getLoss()
             * and set COUNTS= 1
             *
             * else
             * update TOTAL_LOSS= TOTAL_LOSS + model.getLoss(),
             * increment count by 1
             * **/

            Statement insertRefill=connection.createStatement();

            if (insertRefill.executeQuery("SELECT TANKER_NO FROM TANKERS WHERE TANKER_NO= ("+model.getTankerNumber()+")").next()) {

                PreparedStatement updateTanker=connection.prepareStatement("UPDATE TANKERS SET TOTAL_LOSS= TOTAL_LOSS+?,COUNTS=COUNTS+1 WHERE TANKER_NO=?");
                updateTanker.setFloat(1,model.getLoss());
                updateTanker.setInt(2,Integer.parseInt(model.getTankerNumber()));
                updateTanker.execute();

                updateTanker.close();

            } else {
                String tankerType=(mode==0)?"Diesel":"Petrol";

                PreparedStatement insertTanker=connection.prepareStatement("INSERT INTO TANKERS VALUES (?,?,?,?)");
                insertTanker.setInt(1,Integer.parseInt(model.getTankerNumber()));
                insertTanker.setFloat(2,model.getLoss());
                insertTanker.setInt(3,1);
                insertTanker.setString(4,tankerType);

                insertTanker.execute();

                insertTanker.close();

            }

            connection.commit();

            Platform.runLater(()->AppHomeController.getInstance().setStock());

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Refill Added",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                System.out.println("Unable to undo Changes.It restores when system restarts.");
            }

            String message="Error Adding Refill.\n"+"Please Retry Again.";
            String title="Refill Error";
            ErrorMessageDialogGenerator generator=new ErrorMessageDialogGenerator(message,title);
            generator.setTasks(this);
            generator.showDialog();
        }
    }
}
