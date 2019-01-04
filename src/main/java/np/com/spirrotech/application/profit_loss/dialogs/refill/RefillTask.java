package np.com.spirrotech.application.profit_loss.dialogs.refill;

import javafx.application.Platform;
import np.com.spirrotech.application.home.AppHomeController;
import np.com.spirrotech.utils.SystemLogging;
import np.com.spirrotech.application.profit_loss.ProfitLossController;
import np.com.spirrotech.library.ErrorMessageDialogGenerator;
import np.com.spirrotech.library.SnackBarToastGenerator;
import np.com.spirrotech.model.RefillModel;
import np.com.spirrotech.model.SystemStatus;
import np.com.spirrotech.utils.tasks.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *Adds the refill data to the specified table.
 * Also, checks for the tanker, if it is already used, then its updated, otherwise it's new entry is created and stored.
 */

public class RefillTask extends Tasks {

    private RefillModel model;

    private int mode;


    /**
     *
     * @param model the model to be stored in database
     * @param mode mode = 0 (For Diesel) and mode = 1 (For Petrol)
     */

    public RefillTask(RefillModel model,int mode) {
        this.model=model;
        this.mode=mode;
    }

    @Override
    public void execute() {
        try {


            if(mode==0) {

                Statement getTanker = connection.createStatement();
                ResultSet getTankerDetails = getTanker.executeQuery("SELECT TANKER_NO, TYPE FROM TANKERS");

                while (getTankerDetails.next()) {
                    if (model.getTankerNumber().equalsIgnoreCase(getTankerDetails.getString(1)) && !model.getTankerModel().getType().equals(getTankerDetails.getString(2))) {
                        if(taskInterface!=null) {
                            taskInterface.onError();
                            getTankerDetails.close();
                            getTanker.close();
                            return;
                        }
                    }
                }


                PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO DieselRefill VALUES (?,?,?,?)");
                insertRefill.setFloat(1,model.getExpected());
                insertRefill.setFloat(2,model.getActual());
                insertRefill.setString(3,model.getTankerNumber());
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

                Statement getTanker = connection.createStatement();
                ResultSet getTankerDetails = getTanker.executeQuery("SELECT TANKER_NO, TYPE FROM TANKERS");

                while (getTankerDetails.next()) {
                    if (model.getTankerNumber().equalsIgnoreCase(getTankerDetails.getString(1)) && !model.getTankerModel().getType().equals(getTankerDetails.getString(2))) {
                        if(taskInterface!=null) {
                            taskInterface.onError();
                            getTankerDetails.close();
                            getTanker.close();
                            return;
                        }
                    }
                }

                PreparedStatement insertRefill=connection.prepareStatement("INSERT INTO PetrolRefill VALUES (?,?,?,?)");
                insertRefill.setFloat(1,model.getExpected());
                insertRefill.setFloat(2,model.getActual());
                insertRefill.setString(3,model.getTankerNumber());
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


            if(model.isAvailable()) {

                //The tanker is available int the Model (no need to read from database) so, compute the data and update both the model and the database table

                float avgValue=((model.getTankerModel().getLoss())+model.getLoss())/((model.getTankerModel().getCount())+1);
                StringBuilder stringBuilder=new StringBuilder(model.getTankerNumber().substring(0, model.getTankerNumber().length() - 1));

                //Assign the code to the tanker

                if (avgValue >= 0 && avgValue <= 20) {
                    stringBuilder.append("G");
                } else if (avgValue >= 21 && avgValue <= 50) {
                    stringBuilder.append("B");
                } else if (avgValue > 50) {
                    stringBuilder.append("R");

                } else
                    stringBuilder.append("P");

                String codeData=stringBuilder.toString();


                PreparedStatement updateTanker=connection.prepareStatement("UPDATE TANKERS SET TOTAL_LOSS= TOTAL_LOSS+?,COUNTS=COUNTS+1 WHERE TANKER_NO=?");
                updateTanker.setFloat(1,model.getLoss());
                updateTanker.setString(2,codeData.substring(0, (codeData.length() - 1)));
                updateTanker.execute();

                updateTanker.close();

                connection.commit();

                //update the model

                model.setTankerNumber(codeData);
                model.getTankerModel().setAvg(avgValue);
                model.getTankerModel().setCount((model.getTankerModel().getCount())+1);
            } else {

                //This tanker may be new or it may not have been loaded to memory

                Statement insertRefill=connection.createStatement();
                ResultSet resultSet=insertRefill.executeQuery("SELECT TANKER_NO,TOTAL_LOSS,COUNTS FROM TANKERS WHERE TANKER_NO='" + model.getTankerNumber() +"'");
                if (resultSet.next()) {
                    float avgValue=(resultSet.getFloat(2)+model.getLoss())/(resultSet.getInt(3)+1);
                    StringBuilder stringBuilder=new StringBuilder(resultSet.getString(1));
                    if (avgValue >= 0 && avgValue <= 20) {
                        stringBuilder.append("G");
                    } else if (avgValue >= 21 && avgValue <= 50) {
                        stringBuilder.append("B");
                    } else if (avgValue > 50) {
                        stringBuilder.append("R");

                    } else
                        stringBuilder.append("P");

                    String codeData=stringBuilder.toString();

                    PreparedStatement updateTanker=connection.prepareStatement("UPDATE TANKERS SET TOTAL_LOSS= TOTAL_LOSS+?,COUNTS=COUNTS+1 WHERE TANKER_NO=?");
                    updateTanker.setFloat(1,model.getLoss());

                    updateTanker.setString(2,codeData.substring(0,codeData.length() - 1));
                    updateTanker.execute();

                    updateTanker.close();

                    connection.commit();

                    model.setTankerNumber(codeData);
                    model.getTankerModel().setAvg(avgValue);
                    model.getTankerModel().setCount(resultSet.getInt(3)+1);

                } else {

                    float avgValue=model.getLoss();
                    StringBuilder stringBuilder=new StringBuilder(model.getTankerNumber());
                    if (avgValue >= 0 && avgValue <= 20) {
                        stringBuilder.append("G");
                    } else if (avgValue >= 21 && avgValue <= 50) {
                        stringBuilder.append("B");
                    } else if (avgValue > 50) {
                        stringBuilder.append("R");

                    } else
                        stringBuilder.append("P");

                    String codeData=stringBuilder.toString();


                    String tankerType=(mode==0)?"Diesel":"Petrol";

                    PreparedStatement insertTanker=connection.prepareStatement("INSERT INTO TANKERS VALUES (?,?,?,?)");
                    insertTanker.setString(1,codeData.substring(0,codeData.length() - 1));
                    insertTanker.setFloat(
                            2,model.getLoss());
                    insertTanker.setInt(3,1);
                    insertTanker.setString(4,tankerType);

                    insertTanker.execute();

                    insertTanker.close();

                    connection.commit();

                    model.setTankerNumber(codeData);
                    model.getTankerModel().setAvg(avgValue);
                    model.getTankerModel().setCount(1);
                }
                SystemLogging.getLogger().fine("Tanker Refill done");
            }

            if(mode==0)
                Platform.runLater(()->ProfitLossController.getInstance().addDieselModel(model));
            else
                Platform.runLater(()->ProfitLossController.getInstance().addPetrolModel(model));

            Platform.runLater(()->AppHomeController.getInstance().setStock());

            SnackBarToastGenerator snackBarToastGenerator=new SnackBarToastGenerator("Refill Added",1000);
            snackBarToastGenerator.show();

        } catch(SQLException e) {
            e.printStackTrace();
            SystemLogging.getLogger().warning("Tanker Refill couldn't be  done ");
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                SystemLogging.getLogger().fine("Rollback Refill  done ");
                System.out.println("Error in Database detected. Rolling back Database to stable stage.");

            } catch (SQLException e1) {
                SystemLogging.getLogger().warning("Rollback Refill couldn't be done ");
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
