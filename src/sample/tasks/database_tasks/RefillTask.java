package sample.tasks.database_tasks;

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


            Statement insertRefill=connection.createStatement();

            if(mode==0) {
                insertRefill.addBatch("INSERT INTO DieselRefill VALUES ("+model.getExpected()+","+model.getActual()+","+model.getTankerNumber()+",'"+SystemStatus.getCurrentDate().toString()+"')");
                insertRefill.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getDieselStock()+model.getActual())+"', DATE='"+SystemStatus.getCurrentDate().toString()+"' WHERE ID=14");
                SystemStatus.setDieselStock(SystemStatus.getDieselStock()+model.getActual());
            } else {

                insertRefill.addBatch("INSERT INTO PetrolRefill VALUES ("+model.getExpected()+","+model.getActual()+","+model.getTankerNumber()+",'"+SystemStatus.getCurrentDate().toString()+"')");
                insertRefill.addBatch("UPDATE SystemStatus SET VALUE='"+String.valueOf(SystemStatus.getPetrolStock()+model.getActual())+"', DATE='"+SystemStatus.getCurrentDate()+"' WHERE ID=15");
                SystemStatus.setPetrolStock(SystemStatus.getPetrolStock()+model.getActual());
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

            if (insertRefill.executeQuery("SELECT TANKER_NO FROM TANKERS WHERE TANKER_NO= ("+model.getTankerNumber()+")").next()) {

                    insertRefill.addBatch("UPDATE TANKERS SET TOTAL_LOSS= TOTAL_LOSS+(" + model.getLoss() + "), COUNTS= COUNTS+1 WHERE TANKER_NO= (" + model.getTankerNumber() + ")");


            } else {
                //System.out.println("got to line 66, else part");
                String tankerType=(mode==0)?"Diesel":"Petrol";
                insertRefill.addBatch("INSERT INTO TANKERS VALUES ("+model.getTankerNumber()+","+model.getLoss()+",1,'"+tankerType+"')");
            }


            insertRefill.executeBatch();
            connection.commit();

        } catch(SQLException e) {
            e.printStackTrace();
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
